package part1.server.provider;

import part1.server.ratelimit.provider.RateLimitProvider;
import part1.server.serviceRegister.ServiceRegister;
import part1.server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/16 17:35
 */
public class ServiceProvider {
    private Map<String, Object> interfaceProvider;
    private int port;
    private String host;
    //注册服务类
    private ServiceRegister serviceRegister;
    //限流器
    private RateLimitProvider rateLimitProvider;

    public ServiceProvider(String host,int port){
        //需要传入服务端自身的网络地址
        this.host=host;
        this.port=port;
        this.interfaceProvider=new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();
        this.rateLimitProvider=new RateLimitProvider();
    }

    public void provideServiceInterface(Object service,boolean cantry) {
        String serviceName = service.getClass().getName();  // part1.common.service.Impl.UserServiceImpl
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        // 也就是说这里是这个类实现的所有接口的集合
        for (Class<?> clazz : interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
            // System.out.println(clazz.getName());            // part1.common.service.UserService
            //在注册中心注册服务
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port),cantry);
        }
    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider(){
        return rateLimitProvider;
    }

}
