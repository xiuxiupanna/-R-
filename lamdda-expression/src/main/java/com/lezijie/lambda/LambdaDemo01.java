package com.lezijie.lambda;

/**
 * lambda 表达式
 *        目的：简化大量使用匿名内部类，可以java8提供的lambda表达来简化
 *        前提： 函数式接口：只要一个必须被重写的抽象方法的接口
 *        检查函数型接口的注解： @FunctionalInterface
 *        语法： （）->{}
 *              ():重写的抽象方法的参数列表
 *              ->:lambda符号，箭头符号
 *              {}：重写抽象方法的方法体
 *        注意： lambda体 重写哪个接口的抽象方法，需要看前面的引用
 *
 *
 */
public class LambdaDemo01 {
    public static void main(String[] args) {

        //匿名内部类
        Smoke s = ()-> {
            System.out.println("hello world!");
        };
        s.smoking();

//        A a = ()-> { };
        //如果lambda中的方法体{} 中语句体只有一句 前后的 {} 可以省略
//        a = ()-> System.out.println(" hello java！");

        // 如果抽象方法有形参，参数的数据类型可以省略

//        A a1 = (x, y)-> System.out.println(x+y);
//        a1.a(1,4);
        //如果抽象方法有形参,b并且参数只有一个，前后（）可以省略
//         A a2 = x -> System.out.println("我是"+ x);

//         a2.a(7);
        A a3 = x -> x>11;
        System.out.println(a3.a(121));


    }


}

@FunctionalInterface
interface Smoke {
    void smoking();
    default void test() {}

}

@FunctionalInterface
interface A {

//    void a(int x, int y);
//    void a(int x);

    boolean a(int x);
}

