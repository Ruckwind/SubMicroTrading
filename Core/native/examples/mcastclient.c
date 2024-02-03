/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
/* Receiver/client multicast Datagram example. */

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

 
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


int main(int argc, char *argv[]) {

int sd;
int datalen;
char databuf[1024];

/* Create a datagram socket on which to receive. */

sd = socket(AF_INET, SOCK_DGRAM, 0);

if(sd < 0) {
	perror("Opening datagram socket error");
	exit(1);
} else
	printf("Opening datagram socket....OK.\n");

/* Enable SO_REUSEADDR to allow multiple instances of this */
/* application to receive copies of the multicast datagrams. */

{
	int reuse = 1;

	if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, (char *)&reuse, sizeof(reuse)) < 0) {
		perror("Setting SO_REUSEADDR error");
		close(sd);
		exit(1);
	} else
		printf("Setting SO_REUSEADDR...OK.\n");
}

/* Bind to the proper port number with the IP address */
/* specified as INADDR_ANY. */

bindMCastAnyIF_IP4( sd, 4321 );

/* Join the multicast group 226.1.1.1 on the local 203.106.93.94 */
/* interface. Note that this IP_ADD_MEMBERSHIP option must be */
/* called for each local interface over which the multicast */
/* datagrams are to be received. */

join( sd, "226.1.1.1", "192.168.10.1" );
join( sd, "226.1.1.2", "192.168.10.1" );

/* Read from the socket. */

datalen = sizeof(databuf);

if(read(sd, databuf, datalen) < 0) {
	perror("Reading datagram message error");
	close(sd);
	exit(1);
} else {
	printf("Reading datagram message...OK.\n");
	printf("The message from multicast server is: \"%s\"\n", databuf);
}

return 0;

}


