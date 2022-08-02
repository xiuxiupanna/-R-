package com.lezijie.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 题一:调用Collections.sort() 方法，通过定值排序比较两个Employee（先按年龄比，年龄相同按姓名比）
 *
 *
 */
public class LambdaDemo02 {
    public static void main(String[] args) {
        List<Employee> list = Arrays.asList(
                new Employee(101, "孙悟空", 500),
                new Employee(10, "齐天大圣", 1000),
                new Employee(103, "斗战胜佛", 10000),
                new Employee(104, "唐僧", 2000),
                new Employee(105, "唐太宗", 68),
                new Employee(106, "秦始皇", 60),
                new Employee(107, "敌敌畏", 90)
        );

        System.out.println(list);

        Collections.sort(list, (e1, e2)->{
            if (e1.getAge() == e2.getAge()) {
                return e1.getName().compareTo(e2.getName());

            }
            return e1.getAge()-e2.getAge();
        });
        System.out.println(list);


    }


}
