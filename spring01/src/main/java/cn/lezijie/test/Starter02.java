package cn.lezijie.test;

import cn.lezijie.dao.TypeDao;
import cn.lezijie.dao.UserDao;
import cn.lezijie.service.UserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Type;

/**
 * IOC容器Bean对象实例化
 *        1.构造器实例化
 *        Bean对象需要提供空构造方法
 *
 */
public class Starter02 {
    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("spring01.xml");
        TypeDao typeDao = ((TypeDao) factory.getBean("typeDao"));
        typeDao.test();


    }









}
