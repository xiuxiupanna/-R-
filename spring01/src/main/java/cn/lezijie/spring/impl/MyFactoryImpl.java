package cn.lezijie.spring.impl;

import cn.lezijie.spring.MyBean;
import cn.lezijie.spring.MyFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模拟Spring的实现
 * 1.通过构造器得到相关配置文件
 * 2.通过dom4j解析xml文件,得到List 存放id和class
 * 3.通过反射实例化得到对象 Class.forName(类的全路径).newInstance(); 通过Map<id,Class> 存储
 * 4.得到指定的实例化对象
 */
public class MyFactoryImpl implements MyFactory {
    private Map<String, Object> beans = new HashMap<>(); //实例化后的对象放入map
    private List<MyBean> myBeans; //存放已读取bean配置信息

    /**
     * 通过带参构造器得到对应的配置文件
     *
     * @param fileName
     */
    public MyFactoryImpl(String fileName) {
        //通过dom4j解析配置文件(xml),得到List集合
        this.parseXml(fileName);
        //通过反射得到相应的实例化对象,放置在Map对象
        this.instanceBean();


    }

    private void parseXml(String fileName) {
        //获取解析器
        SAXReader saxReader = new SAXReader();
        //得到配置文件的url
        URL url = this.getClass().getClassLoader().getResource(fileName);

        try {
            //解析器解析xml文件
            Document document = saxReader.read(url);
            XPath xPath = document.createXPath("beans/bean");
            List<Element> list = xPath.selectNodes(document);
            if ((list != null && list.size() > 0)) {
                myBeans = new ArrayList<>();
                for (Element element : list) {
                    //获取标签元素中的属性
                    String id = element.attributeValue("id");
                    String clazz = element.attributeValue("class");
                    System.out.println(element.attributeValue("id"));
                    System.out.println(element.attributeValue("class"));
                    //得到Bean对象
                    MyBean bean = new MyBean(id, clazz);
                    //将 Bean对象设置到集合中
                    myBeans.add(bean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instanceBean() {
        //判断bean集合是否为空,不为空遍历得到对应Bean对象
        if (myBeans != null && myBeans.size() > 0) {
            for (MyBean bean : myBeans) {
                try {
                    //通过类的全路径实例化对象
                    Object object = Class.forName(bean.getClazz()).newInstance();
                    beans.put(bean.getId(), object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过id获取对应map对象中的value(实例化好的bean对象       )
     *
     * @param id
     * @return
     */
    @Override
    public Object getBean(String id) {
        Object object = beans.get(id);
        return object;
    }
}
