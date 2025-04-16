package common.protocal;

import common.serializer.Serializer;
import common.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProtocolEncoder extends MessageToByteEncoder {
    private Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg == null) {
            return;
        }
        if (msg instanceof ProtocolMessage<?>) {
            ProtocolMessage<?> protocolMessage = (ProtocolMessage<?>) msg;
            if (protocolMessage.getHeader() == null) {
                return;
            }
            ProtocolMessage.Header header = protocolMessage.getHeader();
            out.writeByte(header.getMagic());
            out.writeByte(header.getVersion());
            out.writeByte(header.getSerializer());
            out.writeByte(header.getType());
            out.writeByte(header.getType());
            out.writeByte(header.getStatus());
            out.writeLong(header.getRequestId());

            // 获取序列化器
            ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
            if (serializerEnum == null) {
                throw new RuntimeException("序列化协议不存在");
            }
            Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
            byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
            // 写入 body 长度和数据
            out.writeInt(bodyBytes.length);
            out.writeBytes(bodyBytes);
        }


        //        System.out.println(msg.getClass());
//        //1.写入消息类型
//        if(msg instanceof RpcRequest){
//            out.writeShort(MessageType.REQUEST.getCode());
//        }
//        else if(msg instanceof RpcResponse){
//            out.writeShort(MessageType.RESPONSE.getCode());
//        }
//        //2.写入序列化方式
//        out.writeShort(serializer.getType());
//        //得到序列化数组
//        byte[] serializeBytes = serializer.serialize(msg);
//        //3.写入长度
//        out.writeInt(serializeBytes.length);
//        //4.写入序列化数组
//        out.writeBytes(serializeBytes);
    }
}
