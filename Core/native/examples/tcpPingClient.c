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
#include <linux/tcp.h>
#include <unistd.h>

static char* OUT_IP   = "192.168.10.2";
static int   OUT_PORT = 4322;

static int   IN_PORT  = 4321;

static struct timespec tp;

unsigned long getNow() {
    if ( clock_gettime(CLOCK_REALTIME, &tp) != 0 ) {
		perror("clock_gettime");    
		exit(2);
	}  

	return tp.tv_sec * 1000000000 + tp.tv_nsec;
}

/* IN METHODS */


void readBack( int idx, int sd, unsigned char* databuf, int datalen ) {
	int rc;
	while ((rc = read(sd, databuf, datalen)) < 0) {
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
 
void dosend( int idx, int sd, char* databuf, int datalen ) {

   int bytesToWrite = datalen;
    int t;

    while( bytesToWrite > 0 ) {
        if((t=write(sd, databuf, datalen)) < 0) {
            perror("Sending datagram message error");
            exit( 1 );
        } else  if ( t == 0 ) {
            printf( "write returned 0\n" );
        }

        bytesToWrite = bytesToWrite - t;
        databuf += t;
        datalen -= t;
    }

    if ( idx < 10 )
        printf("Sent datagram message id=%d\n", idx );

}

void setNoDelay( int sd ) {
        int flag = 1;
        int result = setsockopt(sd, IPPROTO_TCP, TCP_NODELAY, (char *) &flag, sizeof(int));
        if(result< 0) {
                perror("error setting tcp nodelay");
                exit(1);
        }
}

int makeSocket() {
	int sd;
	sd = socket(AF_INET, SOCK_STREAM, 0);

	if(sd < 0) {
		  perror("Opening datagram socket error");
		  exit(1);
	} else
		  printf("Opening the datagram socket...OK.\n");

	return sd;
}

void setBlocking( int sd ) {
	int x;
  	x=fcntl( sd, F_GETFL, 0 );
  	fcntl( sd, F_SETFL, x & ~(O_NONBLOCK));
}

void setNonBlocking( int sd ) {
	int x;
  	x=fcntl( sd, F_GETFL, 0 );
  	fcntl( sd, F_SETFL, x  | O_NONBLOCK);
}

void connectOut( int sd, int port ) {
    struct sockaddr_in serv_addr;
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = inet_addr( OUT_IP ); 
    serv_addr.sin_port = port;
	setBlocking( sd );
    printf( "connecting out to %s port %d\n", OUT_IP, port );
    while (connect(sd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) {
		sleep(1);
    }	
    printf( "connected out to %s\n", OUT_IP );
    setNonBlocking( sd );
    setNoDelay( sd );
  }

int connectIn( int sd, int port ) {
     socklen_t clilen;
     struct sockaddr_in serv_addr, cli_addr;

     bzero((char *) &serv_addr, sizeof(serv_addr));
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = INADDR_ANY;
     serv_addr.sin_port = port;
     if (bind(sd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) 
              error("ERROR on binding");
     listen(sd,5);
     clilen = sizeof(cli_addr);
	 setBlocking( sd );
     printf( "accept on %d\n", port );
     int newsockfd = accept(sd, (struct sockaddr *) &cli_addr, &clilen);
     if (newsockfd < 0) 
          error("ERROR on accept");

     printf( "accepted\n" );

     setNoDelay( newsockfd );
	 setNonBlocking( newsockfd );

     return newsockfd;
}

int main (int argc, char *argv[ ]) {

int outSD;
int inSD;
int datalen = 12;
char *bufIN  = malloc(datalen);

outSD = makeSocket();

connectOut( outSD, OUT_PORT );

int idx=0;

while( 1 ) {
    readBack( idx, outSD, bufIN, datalen );

	dosend( idx, outSD, bufIN, datalen );

	++idx;
}


return 0;

}


