package com.southwind.test;

import com.southwind.entity.User;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 三步
 * 1.数据源 2.数据收集 3.收集结果
 */
public class TestStreamDemo {
    public static void main(String[] args) {
       /* List<User> list = Arrays.asList(
                new User("张三", 18, 2000 ),
                new User("李四", 48, 20000 ),
                new User("法外狂徒", 118, 1222000 )
        );
        // 选出年龄小于100的
        List<User> list1 = new ArrayList<>();
        for (User user : list) {
            if (user.getAge() < 100) list1.add(user);
        }
        // 选出salary大于 10000的
        List<User> list2 = new ArrayList<>();
        for (User user : list) {
            if (user.getSalary() < 10000) list1.add(user);
        }


        // stream 流
        Predicate<User> predicate1 = user -> user.getAge() <100;
        Predicate<User> predicate2 = user -> user.getSalary() <10000;

        List<User> collect = list.stream()
                .filter(predicate1.and(predicate2))
                .collect(Collectors.toList());

        System.out.println(collect);*/


        // filter
//        List<String> list = Arrays.asList("Hello", "HEsfosjf", "jfsojfoHHEIJF", "jAVA","Python","math","English");
//        list.stream()
//                .filter(str ->str.length() > 6)
//                .forEach(str-> System.out.println(str));

        // limit
//        list.stream()
//                .limit(5)
//                .forEach(str-> System.out.println(str));

        //sorted 由小到大排列
        List<Integer> list = Arrays.asList(11, 14, 2, 4, 5, 654, 45, 5352);
        /**
         * 由小到大排列
         */
//        list.stream()
//                .sorted()
//                .forEach(num-> System.out.println(num));
        /**
         * 由大到小排列
         */
/*        list.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(num-> System.out.println(num));

    }*/
        /**
         * 求最大或最小
         */
        System.out.println(list.stream().max(Integer::compareTo).get());

    }
    // map


}
