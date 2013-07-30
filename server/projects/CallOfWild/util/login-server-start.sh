#!/bin/bash

java -server -Xms256M -Xmx256M -XX:PermSize=64M -XX:MaxPermSize=64M -Xss250k -XX:+UseBiasedLocking -XX:+AggressiveOpts -cp TrickraftServer.jar com.tentacle.trickraft.server.LoginServer &

