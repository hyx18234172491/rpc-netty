package part1.client.proxy;

import part1.client.retry.guavaRetry;
import part1.client.rpcClient.impl.SimpleSocketRpcCilent;
import lombok.AllArgsConstructor;
import part1.client.serviceCenter.ServiceCenter;
import part1.client.serviceCenter.impl.ZKServiceCenter;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;
import part1.client.rpcClient.RpcClient;
import part1.client.rpcClient.impl.NettyRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private String host;
    private int port;

    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;

    public ClientProxy() throws InterruptedException {
        serviceCenter=new ZKServiceCenter();
        rpcClient=new NettyRpcClient(serviceCenter);
    }
    public ClientProxy(String host,int port,int choose) throws InterruptedException {
        switch (choose){
            case 0:
                rpcClient=new NettyRpcClient(this.serviceCenter);
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
        //数据传输
        RpcResponse response;
        //后续添加逻辑：为保持幂等性，只对白名单上的服务进行重试
        if (serviceCenter.checkRetry(request.getInterfaceName())){
            //调用retry框架进行重试操作
            response=new guavaRetry().sendServiceWithRetry(request,rpcClient);
        }else {
            //只调用一次
            response= rpcClient.sendRequest(request);
        }
        return response.getData();
    }

    public <T>T getProxy(Class<T>clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)o;
    }
}
