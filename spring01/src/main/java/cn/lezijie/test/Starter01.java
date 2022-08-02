package cn.lezijie.test;

import cn.lezijie.dao.UserDao;
import cn.lezijie.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * IOC 配置文件的加载
 *      1.相对路径加载资源
 *      2.绝对路径加载资源
 *      3.多配置加载
 *        可变参数  传入多个文件名
 *        通过总的配置文件import 其他配置文件. 只需加载总配置文件即可
 *
 */
public class Starter01 {
    public static void main(String[] args) {
//        //相对路径加载资源
//        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//        //绝对路径加载资源
//        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("E:\\git\\spring01\\src\\main\\resources\\springdiy.xml");

//        //多配置文件加载 可变参数 传入多个文件
//        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml","beans.xml");
        // 多配置文件加载   通过总的配置文件import 其他配置文件. 只需加载总配置文件即可

        ApplicationContext context = new ClassPathXmlApplicationContext("services.xml");
        //通过id值得到指定的bean对象
        UserService userService = (UserService) context.getBean("userService");
        UserDao userDao = (UserDao) context.getBean("userDao");

        //调用实例化好的javabean对象的方法
        userService.test();
        userDao.test();
    }









}
