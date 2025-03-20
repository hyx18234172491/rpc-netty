package part1.client.proxy;

import part1.client.proxy.rpcClient.impl.SimpleSocketRpcCilent;
import lombok.AllArgsConstructor;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;
import part1.client.proxy.rpcClient.RpcClient;
import part1.client.proxy.rpcClient.impl.NettyRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private String host;
    private int port;

    private RpcClient rpcClient;
    public ClientProxy(String host,int port,int choose){
        switch (choose){
            case 0:
                rpcClient=new NettyRpcClient(host,port);
                break;
            case 1:
                rpcClient=new SimpleSocketRpcCilent(host,port);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();

        // 发送出去
        //数据传输
        RpcResponse response= rpcClient.sendRequest(request);
        return response.getData();
    }

    public <T>T getProxy(Class<T>clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)o;
    }
}
