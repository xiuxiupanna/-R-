package com.leyou.sms.utils;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 获取随机数
 *
 * @author qianyi
 *
 */
public class RandomUtil {
    private static final Random random = new Random();
    private static final DecimalFormat fourdf = new DecimalFormat("0000");
    private static final DecimalFormat sixdf = new DecimalFormat("000000");


    public static String getFourBitRandom() {
        return fourdf.format(random.nextInt(1000));
    }

    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(100000));
    }

    /**
     * 给定数组，抽取n个数据
     * @param list
     * @param n
     * @return
     */
    public static ArrayList getRandom(List list, int n) {
        Random random = new Random();

        HashMap<Object, Object> hashMap = new HashMap<>();
        // 生成随机数字,并存入HashMap
        for (int i = 0; i < list.size(); i++) {
            int number = random.nextInt(100) + 1;
            hashMap.put(number, i);

        }
        // 从HashMap导入数组
        Object[] objects = hashMap.values().toArray();
        ArrayList arrayList = new ArrayList();

        // 遍历打印数据
        for (int i = 0; i < n; i++) {
            arrayList.add(list.get((int) objects[i]));
            System.out.println(list.get((int) objects[i]) + "\t");

        }
        System.out.println("\n");
        return arrayList;
    }






}
