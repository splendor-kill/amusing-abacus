#!/bin/bash

protoc --java_out=../projects/Trickraft/src/ ProtoBasis.proto ProtoAdmin.proto  ProtoLogin.proto  ProtoPlayer.proto ProtoAlliance.proto
