# http-client-webserver

This is a HTTP application for webclient and webserver. Here the TCP Transport Protocol is used. 

## Client Usage: 
http_client [OPTIONS ...]  URL
where
OPTIONS  := { -r range | -s bytes  timeout | -h[elp] }

## Server Usage: 

http_server [OPTIONS ...]  DIRECTORY
where
OPTIONS  := { -p port | -l logfile | -h[elp] }

### Client general functionality:

Client can request a certain part of a ressouce
Client can send a request in slow motion
Client sends a HTTP request with appopriate headers

### Server General functionality:

GET and HEAD requests are served.
Server is able to deliver only a certain part of a resource
Server can serve several clients at the same time
Server creates a log file where all major events are written down
Server can read ByteStream
