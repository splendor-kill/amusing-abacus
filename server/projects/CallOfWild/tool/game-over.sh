#!/bin/bash

echo process $(pgrep -fx .*TrickraftServer) is over.

java -cp ./TrickraftServer.jar:lib/log4j.jar:lib/netty-3.2.5.Final.jar:lib/protobuf-java-2.4.1.jar com.tentacle.trickraft.server.SafeQuit admin d41d8cd98f00b204e9800998ecf8427e

