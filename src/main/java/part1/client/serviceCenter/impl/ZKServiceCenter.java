package part1.client.serviceCenter.impl;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import part1.client.cache.ServiceCache;
import part1.client.serviceCenter.ServiceCenter;
import part1.client.serviceCenter.ZkWatcher.WatchZK;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter {
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    //ServiceCache
    private ServiceCache cache;

    //负责zookeeper客户端的初始化，并与zookeeper服务端进行连接
    public ZKServiceCenter() throws InterruptedException {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
        this.cache = new ServiceCache();
        WatchZK watchZK = new WatchZK(this.client, this.cache);
        watchZK.watchToUpdate(ROOT_PATH);

    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //先从本地缓存中找
            List<String> serviceList=cache.getServcieFromCache(serviceName);
            //如果找不到，再去zookeeper中找
            //这种i情况基本不会发生，或者说只会出现在初始化阶段
            if(serviceList==null) {
                serviceList=client.getChildren().forPath("/" + serviceName);
            }
            // 这里默认用的第一个，后面加负载均衡
            String str = serviceList.get(0);
            return parseAddress(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }

    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
