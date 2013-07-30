#!/bin/bash

runloc=`dirname $0`
java -cp ${runloc}/Supervisor.jar:./lib/log4j.jar:./lib/netty-3.2.5.Final.jar:./lib/protobuf-java-2.4.1.jar com.tentacle.hegemonic.supervisor.GameMasterTalk

