package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchResult extends PageResult<Goods> {

    private List<Map<String, Object>> filterList; // 所有过滤条件的集合

    public SearchResult() {

    }

    public SearchResult(Long total, List<Goods> items, List<Map<String, Object>> filterList) {
        super(total, items);
        this.filterList = filterList;

    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Map<String, Object>> filterList) {
        super(total, totalPage, items);
        this.filterList = filterList;


    }










}
