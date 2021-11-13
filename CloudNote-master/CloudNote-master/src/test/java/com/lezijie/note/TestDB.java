package com.lezijie.note;

import com.leizijie.note.util.DBUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by HMF on 2021/07/09 00:06
 */
public class TestDB {
    // 使用日志工厂类，记录日志
    private Logger logger = LoggerFactory.getLogger(TestDB.class);

    /**
     * 单元测试方法
     * 1. 方法的返回值，建议使用 void，一般没有返回值
     * 2. 参数列表，建议空参，一般是没有参数
     * 3. 方法上需要设置 @Test 注解
     * 4. 每个方法都能独立运行
     * <p>
     * 判定结果：
     * 绿色：成功
     * 红色：失败
     */
    @Test
    public void testDB() {
        System.out.println(DBUtil.getConnection());
        // 使用日志
        logger.info("获取数据库连接：" + DBUtil.getConnection());
        logger.info("获取数据库连接：{}", DBUtil.getConnection());
    }

}
