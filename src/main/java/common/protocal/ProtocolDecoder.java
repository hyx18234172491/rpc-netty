package common.protocal;

import common.Message.MessageType;
import common.Message.RpcRequest;
import common.Message.RpcResponse;
import common.serializer.Serializer;
import common.serializer.SerializerFactory;
import common.serializer.SerializerKeys;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/6/2 22:24
 * 按照自定义的消息格式解码数据
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> out) throws Exception {
        // 分别从指定位置读出 Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        // 校验魔数
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.readByte());
        header.setSerializer(buffer.readByte());
        header.setType(buffer.readByte());
        header.setStatus(buffer.readByte());
        header.setRequestId(buffer.readLong());
        header.setBodyLength(buffer.readInt());
        // 解决粘包问题，只读指定长度的数据
        byte[] bodyBytes = new byte[header.getBodyLength()];
        buffer.readBytes(bodyBytes);

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                ProtocolMessage<RpcRequest> rpcRequestProtocolMessage = new ProtocolMessage<>(header, request);
                out.add(rpcRequestProtocolMessage);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, response);
                out.add(rpcResponseProtocolMessage);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }

//        //1.读取消息类型
//        short messageType = in.readShort();
//        // 现在还只支持request与response请求
//        if(messageType != MessageType.REQUEST.getCode() &&
//                messageType != MessageType.RESPONSE.getCode()){
//            System.out.println("暂不支持此种数据");
//            return;
//        }
//        //2.读取序列化的方式&类型
//        short serializerType = in.readShort();
//        Serializer serializer = SerializerFactory.getInstance(SerializerKeys.JDK);
//        if(serializer == null)
//            throw new RuntimeException("不存在对应的序列化器");
//        //3.读取序列化数组长度
//        int length = in.readInt();
//        //4.读取序列化数组
//        byte[] bytes=new byte[length];
//        in.readBytes(bytes);
//        Object deserialize= serializer.deserialize(bytes);
//        out.add(deserialize);
    }
}
