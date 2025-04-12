package common.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import common.serializer.mySerializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * SPI类加载器
 * */
@Slf4j
public class SpiLoader {
    // 存储已加载的类，但是还没有实例化的类
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    // 已加载类的实例
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    // 系统 SPI 目录
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    // 用户自定义 SPI 目录
    private static final String RPC_USER_SPI_DIR = "META-INF/rpc/user/";

    // 两个包扫描路径
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_USER_SPI_DIR};

    // 存储所有需要加载的类列表
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(
            Serializer.class
    );  // 可灵活添加

    public static void loadAll(){
        LOAD_CLASS_LIST.stream().forEach(SpiLoader::loadByClass);
    }

    private static Map<String,Class<?>> loadByClass(Class<?> loadClass) {
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        // 扫描所有目录下所，类全名为loadClassloadClass的文件
        Arrays.stream(SCAN_DIRS)
                .map(scanDir -> ResourceUtil.getResources(scanDir + loadClass.getName()))
                .flatMap(List::stream)
                .forEach(resource->{
                    try (
                            InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
                    ) {
                        bufferedReader.lines()
                                .map(line -> line.split("="))
                                .filter(parts -> parts.length > 1)
                                .forEach(parts -> {
                                    String key = parts[0];
                                    String className = parts[1];
                                    try {
                                        keyClassMap.put(key, Class.forName(className));
                                    } catch (ClassNotFoundException e) {
                                        log.error("Class not found for: {}", className, e);
                                    }
                                });
                    } catch (Exception e) {
                        log.error("spi resource load error", e);
                    }
                });
        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;
    }

    public static void main(String[] args) {
        SpiLoader.loadAll();
    }
}
