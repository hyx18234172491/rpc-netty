package client.netty.nettyInitializer;


import client.netty.handler.NettyClientHandler;
import common.protocal.ProtocolDecoder;
import common.protocal.ProtocolEncoder;
import common.serializer.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/26 17:26
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //使用自定义的编/解码器
        pipeline.addLast(new ProtocolDecoder());
        pipeline.addLast(new ProtocolEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}
