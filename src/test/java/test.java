import client.proxy.ClientProxy;
import common.service.UserService;

public class test {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy=new ClientProxy();
        UserService proxy=clientProxy.getProxy(UserService.class);

        for (int i=0;i<1;i++){
            System.out.println(proxy.getClass());
            System.out.println(proxy.getClass().getName());
        }
    }
}
