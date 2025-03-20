package part1.server.netty.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/16 17:35
 */
public class ServiceProvider {
    private Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }

    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();  // part1.common.service.Impl.UserServiceImpl
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        // 也就是说这里是这个类实现的所有接口的集合
        for (Class<?> clazz : interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
            System.out.println(clazz.getName());            // part1.common.service.UserService
        }
    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }

}
