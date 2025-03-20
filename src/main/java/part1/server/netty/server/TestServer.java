package part1.server.netty.server;


import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;
import part1.server.netty.provider.ServiceProvider;
import part1.server.netty.server.impl.NettyRPCRPCServer;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/11 19:39
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();

        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer=new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
