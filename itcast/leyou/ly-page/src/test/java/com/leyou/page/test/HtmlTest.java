package com.leyou.page.test;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HtmlTest {

    @Autowired
    private PageService pageService;

    @Test
    public void testCreateHtml() {
        pageService.createItemHtml(76L);


    }






}
