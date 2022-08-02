package cn.lezijie.spring;

/**
 * Bean 工厂接口 定义
 */
public interface MyFactory {
    //通过id属性值获取对象
    public Object getBean(String id);


}
