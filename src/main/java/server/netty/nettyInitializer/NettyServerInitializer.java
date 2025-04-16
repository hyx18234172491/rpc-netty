package server.netty.nettyInitializer;


import common.protocal.ProtocolDecoder;
import common.protocal.ProtocolEncoder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;
import common.serializer.JsonSerializer;
import server.netty.handler.NettyRPCServerHandler;
import server.provider.ServiceProvider;


@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    // 这个ChannelInitializer就是一个初始化器，初始化连接后进行事件读写的通道
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 这里就是添加具体的handler，添加handler对数据进行处理
        // 原理就是判断读写空闲时间是否太长
        ChannelPipeline pipeline = ch.pipeline();
        // 这里用来检测连接假死现象，就是Tcp连接已经断开了，但是程序不知道
        pipeline.addLast(new IdleStateHandler(5,5,5));
        // 当发生了这些上面的读写超时的时候，就需要自己来处理了，因此需要一个双向的处理器，
        pipeline.addLast(new ChannelDuplexHandler(){
            // 这个上面的读写超时是自定义事件，就不能用channel read这些了
            // 用下面这个来处理特殊事件
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
                // 触发了读空闲事件
                if(idleStateEvent.state()== IdleState.READER_IDLE){

                }else if(idleStateEvent.state()== IdleState.WRITER_IDLE){
                    // 触发了写空闲事件
                }
//                super.userEventTriggered(ctx, evt);
            }
        });
        // 这里主要用LengthFieldBasedFrameDecoder来解决粘包半包问题
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024,13,4));
        pipeline.addLast(new ProtocolEncoder(new JsonSerializer()));
        pipeline.addLast(new ProtocolDecoder());

        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
