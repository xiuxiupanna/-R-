package com.leyou.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.client.GoodsClient;
import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadDataTest {

  /*  @MockBean
    private EurekaAutoServiceRegistration eurekaAutoServiceRegistration;*/

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;


    @Test
    public void loadData() {
        int page = 1, rows = 100, size = 0;
        do {
            try {
                //查询spu
                PageResult<Spu> pageResult = goodsClient.querySpuByPage(page, rows, true, null);
                //取出spu
                List<Spu> items = pageResult.getItems();
                //转换
                List<Goods> goodsList = items.stream().map(searchService::buildGoods).collect(Collectors.toList());
                page++;
                size = items.size();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (size == 100);
    }
}
