import part1.client.proxy.ClientProxy;
import part1.common.pojo.User;
import part1.common.service.UserService;

public class test {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy=new ClientProxy();
        UserService proxy=clientProxy.getProxy(UserService.class);

        for (int i=0;i<100;i++){
            User user = proxy.getUserByUserId(1);
            System.out.println("从服务端得到的user="+user.toString());

            User u=User.builder().id(100).userName("wxx").sex(true).build();
            Integer id = proxy.insertUserId(u);
            System.out.println("向服务端插入user的id"+id);
            System.out.println(i);
        }
    }
}
