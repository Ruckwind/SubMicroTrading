/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
/* Send Multicast Datagram code example. */

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h> 
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>


static char* OUT_MCAST_GRP = "226.1.1.2";
static char* OUT_NIC_IP    = "192.168.10.1";
static int   OUT_PORT      = 4322;

static char* IN_MCAST_GRP = "226.1.1.1";
static char* IN_NIC_IP    = "192.168.10.1";
static int   IN_PORT       = 4321;

static struct timespec tp;

unsigned long getNow() {
    if ( clock_gettime(CLOCK_REALTIME, &tp) != 0 ) {
		perror("clock_gettime");    
		exit(2);
	}  

	return tp.tv_sec * 1000000000 + tp.tv_nsec;
}

/* IN METHODS */


void join( int sd, char* mcastGrpAddrIP, char* localInterfaceIP ) {
        struct ip_mreq group;
        group.imr_multiaddr.s_addr = inet_addr( mcastGrpAddrIP );
        group.imr_interface.s_addr = inet_addr( localInterfaceIP );

        if(setsockopt(sd, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *)&group, sizeof(group)) < 0) {
                perror("Adding multicast group error");
                close(sd);
                exit(1);
        } else
                printf("Adding multicast group...OK.\n");
}

void bindMCastAnyIF_IP4( int sd, int port ) {
        struct sockaddr_in localSock;
        memset((char *) &localSock, 0, sizeof(localSock));
        localSock.sin_family = AF_INET;
        localSock.sin_port = htons(port);
        localSock.sin_addr.s_addr = INADDR_ANY;

        if(bind(sd, (struct sockaddr*)&localSock, sizeof(localSock))) {
                perror("Binding datagram socket error");
                close(sd);
                exit(1);
        } else
                printf("Binding datagram socket...OK.\n");
}

void setReuse( int sd ) {
        int reuse = 1;

        if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, (char *)&reuse, sizeof(reuse)) < 0) {
                perror("Setting SO_REUSEADDR error");
                close(sd);
                exit(1);
        } else
                printf("Setting SO_REUSEADDR...OK.\n");

}

static struct sockaddr from;
static socklen_t fromlen;

void readBack( int idx, int sd, unsigned char* databuf, int datalen ) {
	int rc;
	while ((rc = recvfrom(sd, databuf, datalen, MSG_DONTWAIT, &from, &fromlen)) < 0) {
		if ( rc != - 1 ) { 
				perror("Reading datagram message error");
				close(sd);
				exit(1);
		}
	}

	unsigned char *t = databuf;

    unsigned char b1 = *(t++);
    unsigned char b2 = *(t++);
    unsigned char b3 = *(t++);
    unsigned char b4 = *(t++);

    unsigned int id = (b1 << 24) |
                      (b2 << 16) |
                      (b3 << 8)  |
                      (b4);

    b1 = *(t++);
    b2 = *(t++);
    b3 = *(t++);
    b4 = *(t++);

    unsigned char b5 = *(t++);
    unsigned char b6 = *(t++);
    unsigned char b7 = *(t++);
    unsigned char b8 = *(t++);

    unsigned long nanos = (((long)b1) << 56) |
                          (((long)b2) << 48) |
                          (((long)b3) << 40) |
                          (((long)b4) << 32) |
                          (((long)b5) << 24) |
                          (((long)b6) << 16) |
                          (((long)b7) << 8) |
                          (((long)b8) );



	unsigned long now = getNow();
	unsigned long delay = now - nanos;

	if ( id < 10 )	
	printf( "Found id=%d, nanos=%ld, delay=%ld\n", id, nanos, delay );	
}

/* OUT METHODS */
 
struct sockaddr_in *makeGroupSockIP4( char *mcastAddr, int port ) {
	struct sockaddr_in *groupSock = (struct sockaddr_in *) malloc( sizeof(struct sockaddr_in) );
	printf( "sizeof(sockaddr_in)=%d, sizeof(*ptr)=%d\n", sizeof(struct sockaddr_in), sizeof(*groupSock) );
	memset((char *) groupSock, 0, sizeof(*groupSock));
	groupSock->sin_family = AF_INET;
	groupSock->sin_addr.s_addr = inet_addr(mcastAddr);
	groupSock->sin_port = htons(port);
	return groupSock;
}

void addMulticastLocalIF( int sd, char* localInterfaceIP ) {
	struct in_addr localInterface;
	localInterface.s_addr = inet_addr( localInterfaceIP );

	if(setsockopt(sd, IPPROTO_IP, IP_MULTICAST_IF, (char *)&localInterface, sizeof(localInterface)) < 0) {
  		perror("Setting local interface error");
  		exit(1);
	} else
  		printf("Setting the local interface...OK\n");
}


void dosend( int idx, int sd, char* databuf, int datalen, void* grpSockPtr ) {
	struct sockaddr_in* groupSock = (struct sockaddr_in*) grpSockPtr;

	if(sendto(sd, databuf, datalen, 0, (struct sockaddr*)groupSock, sizeof(*groupSock)) < 0) {
		perror("Sending datagram message error");
	} 
}

int makeSocket() {
	int sd;
	sd = socket(AF_INET, SOCK_DGRAM, 0);

	if(sd < 0) {
		  perror("Opening datagram socket error");
		  exit(1);
	} else
		  printf("Opening the datagram socket...OK.\n");

        fcntl(sd, F_SETFL, O_NONBLOCK);

	return sd;
}


int main (int argc, char *argv[ ]) {

int outSD;
int inSD;
int datalen = 12;
char *bufIN  = malloc(datalen);

outSD = makeSocket();
addMulticastLocalIF( outSD, OUT_NIC_IP );
struct sockaddr_in* groupSockOUT = makeGroupSockIP4( OUT_MCAST_GRP, OUT_PORT );

inSD = makeSocket();
setReuse( inSD );
bindMCastAnyIF_IP4( inSD, IN_PORT );


join( inSD, IN_MCAST_GRP, IN_NIC_IP );


int idx=0;

while( 1 ) {
    readBack( idx, inSD, bufIN, datalen );

	dosend( idx, outSD, bufIN, datalen, groupSockOUT );
}


return 0;

}


