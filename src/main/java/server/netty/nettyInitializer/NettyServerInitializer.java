package server.netty.nettyInitializer;


import common.serializer.myCode.MyDecoder;
import common.serializer.myCode.MyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import common.serializer.mySerializer.JsonSerializer;
import server.netty.handler.NettyRPCServerHandler;
import server.provider.ServiceProvider;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/26 16:15
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //使用自定义的编/解码器
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());

        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
