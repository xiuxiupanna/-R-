package com.itcast.es.repository;

import com.itcast.es.pojo.Item;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface ItemRepository extends ElasticsearchRepository<Item, Long> {
    List<Item> queryItemsByTitleMatches(String key);
    List<Item> queryItemsByPriceBetween(Double begin, Double end);





}
