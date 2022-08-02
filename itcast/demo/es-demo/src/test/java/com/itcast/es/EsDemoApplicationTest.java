package com.itcast.es;

import com.itcast.es.pojo.Item;
import com.itcast.es.repository.ItemRepository;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsDemoApplicationTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void contentLoads() {
        elasticsearchTemplate.createIndex(Item.class);
        elasticsearchTemplate.putMapping(Item.class);


    }
    @Test
    public void insertIndex() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);

    }

    @Test
    public void query() {
        Iterable<Item> result = itemRepository.findAll();
        for (Item item : result) {
            System.out.println("item = " + item);

        }

    }

    @Test
    public void queryByMatch() {
        List<Item> list1 = itemRepository.queryItemsByTitleMatches("小米手机");
        for (Item item : list1) {

            System.out.println("item: " + item);
        }
        System.out.println("=======================================");

        List<Item> list2 = itemRepository.queryItemsByPriceBetween(1000d, 4000d);
        for (Item item : list2) {

            System.out.println("item: " + item);
        }

    }

    @Test
    public void nativeQuery() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机"))
                .withPageable(PageRequest.of(0, 2))
                .withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        Page<Item> items = itemRepository.search(queryBuilder.build());


        System.out.println("总条数：" + items.getTotalElements());
        System.out.println("总页数：" + items.getTotalPages());

        List<Item> itemsContent = items.getContent();
        for (Item item : itemsContent) {
            System.out.println("item= " + item);
        }

    }

    @Test
    public void nativeAgg() {

        //创建原生搜索的查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //减少聚合的结果
        queryBuilder.withPageable(PageRequest.of(0,1));
        //添加聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("popularBrand").field("brand"));
        //得到聚合结果
        AggregatedPage<Item> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Item.class);
        //解析
        Aggregations resultAggregations = result.getAggregations();
        //取出一个聚合
        StringTerms terms = resultAggregations.get("popularBrand");
        //取出buckets
        for (StringTerms.Bucket bucket : terms.getBuckets()) {
            System.out.println("key: " + bucket.getKeyAsString());
            System.out.println("count:" + bucket.getDocCount());
        }


    }







}
