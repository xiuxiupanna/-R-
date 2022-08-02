package com.lezijie.lambda;

import java.util.Locale;

/**
 * 习题二：
 *      1)声明函数式接口，接口中声明抽象方法 public String getValue(String str)
 *      2)声明类 TestLambda,类中编写方法使用接口作为参数，将一个字符串转写成大写，并作为方法的返回值
 *      3）再将一个字符串的第二个索引 和第四个索引位置进行拦截取子串
 */
public class LambdaDemo03 {
    public static void main(String[] args) {
        System.out.println(strHandler("woshiqitiandasheng", s -> s.toUpperCase()));
        System.out.println(strHandler("woshiqitiandasheng", s -> s.substring(2, 5)));


    }
    //封装一个功能：可以对某一个字符串，进行某种操作，结果返回
    public static String strHandler(String str, MyFunction my) {

        return my.getValue(str);
    }




}
@FunctionalInterface
interface MyFunction {
    public String getValue(String str);
}
