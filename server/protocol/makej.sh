#!/bin/bash

protoc --java_out=../projects/CallOfWild/src/ ProtoBasis.proto ProtoAdmin.proto ProtoLogin.proto ProtoPlayer.proto ProtoAlliance.proto
