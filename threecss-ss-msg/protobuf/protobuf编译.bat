C:
cd C:\Users\xp\Desktop\threecss-ss\trunk\threecss-ss-msg\protobuf
protoc --java_out=./ MinaMsg.proto
protoc --java_out=./ DistributedLock.proto
protoc --java_out=./ WebSocketMsg.proto
