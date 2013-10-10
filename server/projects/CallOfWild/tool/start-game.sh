#!/bin/bash

java -server -Xms1024M -Xmx1024M -cp ./TrickraftServer.jar:lib/commons-logging-1.1.1.jar:lib/ecj-3.7.2.jar:lib/commons-io-2.4.jar:lib/log4j.jar:lib/proxool-0.9.1.jar:lib/c3p0-0.9.1.2.jar:lib/proxool-cglib.jar:lib/mysql-connector-java-5.0.8-bin.jar:lib/netty-3.2.5.Final.jar:lib/protobuf-java-2.4.1.jar:lib/opencsv-2.3.jar:lib/dom4j-1.6.1.jar:lib/trove-3.0.3.jar com.tentacle.trickraft.server.TrickraftServer &

