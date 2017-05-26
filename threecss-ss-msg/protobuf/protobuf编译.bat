C:
cd C:\Users\admin\Desktop\kaola\trunk\kaola-msg\protobuf
protoc --java_out=./ MinaMsg.proto
protoc --java_out=./ DistributedLock.proto
protoc --java_out=./ WebSocketMsg.proto
