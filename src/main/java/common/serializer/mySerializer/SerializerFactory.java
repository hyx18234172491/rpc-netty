package common.serializer.mySerializer;

import common.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/*
* 序列化器的工厂
* */
public class SerializerFactory {
    static {
        SpiLoader.loadByClass(Serializer.class);
    }
    /*
    * 默认序列化器
    * */
    private static final Serializer DEFAULT_SERIALIZER =new ObjectSerializer();

    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }

}
