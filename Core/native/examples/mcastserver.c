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

void dosend( int sd, char* databuf, int datalen, void* grpSockPtr ) {
	struct sockaddr_in* groupSock = (struct sockaddr_in*) grpSockPtr;
	if(sendto(sd, databuf, datalen, 0, (struct sockaddr*)groupSock, sizeof(*groupSock)) < 0) {
		perror("Sending datagram message error");
	} else
  	printf("Sending datagram message...OK\n");
}

int main (int argc, char *argv[ ]) {

int sd;
char databuf[1024] = "Multicast test message lol!";
int datalen = sizeof(databuf);

/* Create a datagram socket on which to send. */

sd = socket(AF_INET, SOCK_DGRAM, 0);

if(sd < 0) {
  perror("Opening datagram socket error");
  exit(1);
} else
  printf("Opening the datagram socket...OK.\n");

/* Initialize the group sockaddr structure with a */
/* group address of 225.1.1.1 and port 5555. */


/* Set local interface for outbound multicast datagrams. */
/* The IP address specified must be associated with a local, */
/* multicast capable interface. */

addMulticastLocalIF( sd, "192.168.10.1" );


/* Send a message to the multicast group specified by the*/
/* groupSock sockaddr structure. */
/*int datalen = 1024;*/

struct sockaddr_in* groupSock = makeGroupSockIP4( "226.1.1.1", 4321 );

while( true ) {
    dosend( sd, databuf, datalen, groupSock );
    

/* Try the re-read from the socket if the loopback is not disable

if(read(sd, databuf, datalen) < 0) {
	perror("Reading datagram message error\n");
	close(sd);
	exit(1);
} else {
	printf("Reading datagram message from client...OK\n");
	printf("The message is: %s\n", databuf);
}

*/

return 0;

}


