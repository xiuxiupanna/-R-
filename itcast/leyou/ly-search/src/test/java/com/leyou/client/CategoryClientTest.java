package com.leyou.client;

import com.leyou.item.client.CategoryClient;
import com.leyou.item.pojo.Category;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest extends TestCase {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryByIds() {

        List<Category> categoryList = categoryClient.queryByIds(Arrays.asList(1L, 2L, 3L));
        Assert.assertEquals(3, categoryList.size());
        for (Category category : categoryList) {
            System.out.println("category = " + category);
        }


    }
}