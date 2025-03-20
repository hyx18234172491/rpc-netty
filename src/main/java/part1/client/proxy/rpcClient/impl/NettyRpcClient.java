package part1.client.proxy.rpcClient.impl;

import part1.client.netty.nettyInitializer.NettyClientInitializer;
import part1.client.proxy.rpcClient.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

public class NettyRpcClient implements RpcClient {
    private String host;
    private int port;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    public NettyRpcClient(String host,int port){
        this.host=host;
        this.port=port;
    }

    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                // NettyClientInitializer这里配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try{
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse rpcResponse = channel.attr(key).get();
            System.out.println(rpcResponse);
            System.out.println("收到了netty rpcResponse");
            return rpcResponse;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
