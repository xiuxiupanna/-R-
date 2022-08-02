package com.lezijie.lambda;

import sun.rmi.runtime.Log;

/**
 * 习题三：
 *      1):声明一个带两个泛型的函数式接口，泛型类型为<T,R> T为参数，R为返回值
 *      2):接口中声明对应抽象方法
 *      3):在TestLambda类型中声明方法，参数三个，两个long类型的参数，接口作为参数，接口实现中计算两个long型参数的和
 *      4):再计算两个long型参数的乘积
 *
 */
public class LambdaDemo04 {
    public static void main(String[] args) {

        System.out.println(longLambda(103L, 232L, (l1, l2) -> l1 + l2));
        System.out.println(longLambda(103L, 232L, (l1, l2) -> l1 * l2));


    }

    public static Long longLambda(long l1, long l2, MyFunction2<Long,Long> my2 ) {
        return my2.test(l1, l2);

    }
}



@FunctionalInterface
interface MyFunction2<T,R> {
    R test(T t1, T t2);

}