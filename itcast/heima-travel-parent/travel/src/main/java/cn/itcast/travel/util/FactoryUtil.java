package cn.itcast.travel.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

/**
 * 生产对象的工程
 */
public class FactoryUtil {

    private  static ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("impl");
    }

    /**
     * 根据接口名称获取对应实现类实例对象
     * @param itfName
     * @return Object
     */
    public static Object getInstance(String itfName){
        try {
            String className =  resourceBundle.getString(itfName);
            return Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
