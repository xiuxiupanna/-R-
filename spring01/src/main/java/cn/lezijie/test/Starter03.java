package cn.lezijie.test;

import cn.lezijie.controller.TypeController;
import cn.lezijie.dao.TypeDao;
import cn.lezijie.service.TypeService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * IOC容器Bean对象实例化
 *        1.构造器实例化
 *        Bean对象需要提供空构造方法
 *
 */
public class Starter03 {
    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("spring01.xml");
        TypeDao typeDao = ((TypeDao) factory.getBean("typeDao"));
        typeDao.test();


        //静态工厂实例化
        TypeService typeService = ((TypeService) factory.getBean("typeService"));
        typeService.test();

        //实例化工厂实例化
        TypeController typeController = (TypeController) factory.getBean("typeController");
        typeController.test();






    }













}
