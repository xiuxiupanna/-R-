package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.BrandClient;
import com.leyou.item.client.CategoryClient;
import com.leyou.item.client.GoodsClient;
import com.leyou.item.client.SpecClient;
import com.leyou.item.pojo.*;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 把Spu转为一个Goods对象
     *
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu) {
        // 1 商品相关搜索信息的拼接： 标题，分类，品牌，规格参数，规格信息等
        // 1.1 分类
        String categoryNames = categoryClient.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                .stream().map(Category::getName).collect(Collectors.joining(","));
        // 1.2 品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        // 1.3 标题(拼接起来)
        String all = spu.getTitle() + categoryNames + brand.getName();

        // 2 spu 下所有的JSON数组
        //准备一个集合，用map来代替sku,只需要sku中的部分数据
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        List<Map<String, Object>> skuMap = new ArrayList<>();
        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("price", sku.getPrice());
            map.put("title", sku.getTitle());
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skuMap.add(map);
        }
        // 3 当前spu下所有sku的价格的集合
        Set<Long> price = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        // 4 当前spu的规格参数
        HashMap<String, Object> specs = new HashMap<>();
        // 4.1 获取规格参数 key, 来自于SpecParam中当前分类下的需要搜索的规格
        List<SpecParam> specParamList = specClient.queryParams(null, spu.getCid3(), true);
        // 4.2 查询规格参数的值，来自于spuDetail
        SpuDetail spuDetail = goodsClient.queryDetailBySpuId(spu.getId());
        // 4.2.1 通用规格参数值
        String json1 = spuDetail.getGenericSpec();

        Map<Long, Object> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, Object.class);
        // 4.2.2 特有规格参数值
        String json2 = spuDetail.getSpecialSpec();
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        for (SpecParam specParam : specParamList) {
            // 获取规格参数的名称
            String key = specParam.getName();
            // 获取规格参数值
            Object value = null;
            // 判断是否是通用规格
            if (specParam.getGeneric()) {
                // 通用规格
                value = genericSpec.get(specParam.getId());
            } else {
                //特有规格
                value = specialSpec.get(specParam.getId());
            }
            //判断是否是数字类型
            if (specParam.getNumeric()) {
                // 是数字类型，分段
                value = chooseSegment(value, specParam);
            }
            value = (value == null || StringUtils.isBlank(value.toString())) ? "其它" : value;
            // 添加到 specs
            specs.put(key, value);

        }
        Goods goods = new Goods();
        //从spu对象中拷贝与goods对象中属性名一致的属性
        BeanUtils.copyProperties(spu, goods);
        goods.setCreateTime(spu.getCreateTime().getTime());
        goods.setSkus(JsonUtils.toString(skuList)); // spu 下所有的JSON数组
        goods.setSpecs(null); // 当前spu下规格参数
        goods.setPrice(price); // 当前spu下所有sku的价格的集合
        goods.setAll(null); // 商品相关搜索信息的拼接： 标题，分类，品牌，规格参数，规格信息等

        return goods;

    }

    private String chooseSegment(Object value, SpecParam p) {
        if (value == null) {
            return "其它";
        }
        double val = NumberUtils.toDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 搜索
     *
     * @param request
     * @return
     */
    public PageResult<Goods> search(SearchRequest request) {
        // 获取搜索条件
        String key = request.getKey();
        if (StringUtils.isBlank(key)) {
            throw new LyException(ExceptionEnum.SKU_GOODS_NOT_FOUND);
        }
        // 查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 搜索结果字段过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        // 分页
        int page = request.getPage() - 1;
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 搜索条件

        QueryBuilder basicQuery = buildBasicQuery(request);

        queryBuilder.withQuery(basicQuery);
        // 添加聚合条件
        String categoryAggName = "categoryAgg";
        // 分类聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 品牌聚合
        String brandAggName = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 搜索查询结果
        AggregatedPage<Goods> result= elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
//        Page<Goods> result = goodsRepository.search(queryBuilder.build());


        // 解析结果
        // 解析分页结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> content = result.getContent();
        // 过滤条件的集合
        List<Map<String, Object>> filterList = new ArrayList<>();
        // 解析分类的聚合结果
        Aggregations aggregations = result.getAggregations();
        LongTerms cateAggregationTerms = aggregations.get(categoryAggName);
        List<Long> idList = handlerCategoryAgg(cateAggregationTerms, filterList);
        //处理规格参数
        if (idList != null && idList.size() == 1) {
            //当前分类有且只有一个，可以进行规格参数的聚合,参数有三个：分类id,搜索的条件,过滤条件集合
            handlerSpecAgg(idList.get(0), basicQuery, filterList);


        }
        LongTerms brandAggregationTerms = aggregations.get(brandAggName);
        handlerBrandAgg(brandAggregationTerms, filterList);

        // 解析品牌的聚合结果

        // 封装结果
        return new SearchResult(total, totalPages, content, filterList);

    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        // 创建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 添加搜索条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        // 添加过滤条件
        // 获取所有需要过滤的条件
        Map<String, String> requestFilter = request.getFilter();
        // 循环添加过滤条件
        for (Map.Entry<String, String> stringEntry : requestFilter.entrySet()) {
            // 过滤条件的key
            String key = stringEntry.getKey();
            // 过滤条件的值
            String value = stringEntry.getValue();
            // 如果不是分类和品牌,需要对key添加前缀
            if (!"cid3".equals(key) && !"brandId".equals(key)) {
                key = "specs." + key;
            }

            boolQueryBuilder.filter(QueryBuilders.termQuery(key, value));

        }
        return boolQueryBuilder;


    }

    private void handlerSpecAgg(Long cid, QueryBuilder basicQuery, List<Map<String, Object>> filterList) {
        // 根据分类,查找到可以用来搜索的规格
        List<SpecParam> specParams = specClient.queryParams(null, cid, true);
        // 在用户搜索结果基础上,对规格参数进行聚合
        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加搜索条件
        queryBuilder.withQuery(basicQuery);
        for (SpecParam specParam : specParams) {
            // 获取规格参数的名称,作为聚合的名称,方便后来根据名称获取聚合
            String name = specParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name));
        }
        // 设置size为最小,避免搜索结果
        queryBuilder.withPageable(PageRequest.of(0, 1));
        // 聚合,获取结果
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //将规格参数聚合结果整理后返回
        Aggregations resultAggregations = result.getAggregations();
        //遍历规格参数，根据参数名称，取出每个聚合
        for (SpecParam specParam : specParams) {
            //获取名称
            String name = specParam.getName();
            //根据名称，获取聚合结果
            StringTerms terms = resultAggregations.get(name);
            //获取聚合的buckets,作为过滤项
            List<String> options = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsString())
                    .collect(Collectors.toList());
            // 准备过滤项的结构
            Map<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", options);
            filterList.add(map);

        }
    }

    private void handlerBrandAgg(LongTerms brandAggregationTerms, List<Map<String, Object>> filterList) {
        //解析聚合结果，从桶中获取brand的ID的集合
        List<Long> brandIdList = brandAggregationTerms.getBuckets().stream()
                .map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据id查询品牌
        List<Brand> brandList = brandClient.queryByIds(brandIdList);
        Map<String, Object> map = new HashMap<>();
        map.put("key", "brandId");
        map.put("options", brandList);
        filterList.add(map);

    }

    private List<Long>  handlerCategoryAgg(LongTerms cateAggregationTerms, List<Map<String, Object>> filterList) {
        //解析聚合结果，从桶中获取category的ID的集合
        List<Long> categoryIdList = cateAggregationTerms.getBuckets().stream()
                .map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据id查询品牌
        List<Category> categoryList = categoryClient.queryByIds(categoryIdList);
        Map<String, Object> map = new HashMap<>();
        map.put("key", "cid3");
        map.put("options", categoryList);
        filterList.add(map);
        return categoryIdList;
    }

    /**
     * 根据spuId删除索引库
     * @param spuId
     */
    public void deleteById(Long spuId) {
        goodsRepository.deleteById(spuId);

    }

    /**
     * 根据spuId 更新索引库
     * @param spuId
     */
    public void insertOrUpdate(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //新增goods
        goodsRepository.save(goods);
    }
}
