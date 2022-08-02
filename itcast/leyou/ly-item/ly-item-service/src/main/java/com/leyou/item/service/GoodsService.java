package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Slf4j
@Service
public class GoodsService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 分页查询Spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //搜索条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //关键字搜索
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");

        }
        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //过滤逻辑删除
        criteria.andEqualTo("valid", true);
        //排序
        example.setOrderByClause("last_update_time DESC");
        //查询结果
        List<Spu> spuList = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spuList)) {
            throw new LyException(ExceptionEnum.SPU_GOODS_NOT_FOUND);

        }
        //处理分类和品牌的名称
        handlerCategoryAndBrandName(spuList);

        //封装结果返回
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spuList);
        return new PageResult<>(spuPageInfo.getTotal(), spuList);


    }

    /**
     * 处理分类和品牌的名称
     *
     * @param spuList
     */
    private void handlerCategoryAndBrandName(List<Spu> spuList) {
        for (Spu spu : spuList) {
            //查询分类
            String cname = categoryService
                    .queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream()
                    .map(Category::getName)
                    .collect(Collectors.joining("/"));
            //设置分类名称
            spu.setCname(cname);
            //品牌名称
            spu.setBname("");
            Brand brand = brandService.queryById(spu.getBrandId());
            spu.setBname(brand.getName());

        }
    }

    /**
     * 新增商品
     *
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setValid(true);
        spu.setSaleable(false);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());

        int count = spuMapper.insertSelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_INSERT_ERROR);

        }
        //新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.insertSelective(spuDetail);
        saveSkuAndStock(spu);
        log.info("【商品服务】 新增商品成功， 商品id：{}", spu.getId());

        sendMessage(spu, "insert");

    }

    private void sendMessage(Spu spu, String type) {
        try {
            //发送消息
            amqpTemplate.convertAndSend("item." + type, spu.getId());
        } catch (Exception e) {
            log.error("【商品服务】消息发送失败！", e);

        }
    }

    private  void saveSkuAndStock(Spu spu) {
        int count;
        //新增sku
        List<Sku> skuList = spu.getSkus();
        //创建集合,记录sku的库存
        List<Stock> stockList = new ArrayList<>();

        for (Sku sku : skuList) {
            //填写sku信息
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            count = skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOODS_INSERT_ERROR);

            }
            //初始化库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        //新增stock
        count = stockMapper.insertList(stockList);
        if (count != stockList.size()) {
            throw new LyException(ExceptionEnum.GOODS_INSERT_ERROR);

        }
    }

    /**
     * 根据spuId查询SpuDetail
     *
     * @param spuId
     * @return
     */
    public SpuDetail queryDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPU_GOODS_NOT_FOUND);

        }
        return spuDetail;


    }

    /**
     * 根据SpuId查询sku集合
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        //根据sku集合
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_GOODS_NOT_FOUND);

        }
        //获取sku的id的集合
        List<Long> idList = skuList.stream()
                .map(Sku::getId)
                .collect(Collectors.toList());
        // 填充sku的库存
        fillSkuByStock(idList, skuList);

        return skuList;
    }


    /**
     * 更新商品
     *
     * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) {
        Long spuId = spu.getId();
        if (spuId == null) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //删除以前的sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        //查询以前的sku
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            //非空删除
            int count = skuMapper.delete(sku);
            if (count != skuList.size()) {
                throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
            }
            //删除stock
            List<Long> idList = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            count = stockMapper.deleteByIdList(idList);
            if (count != skuList.size()) {
                throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
            }

        }
        //修改spu
        spu.setValid(null); // 不需要修改的字段，一定要强制为null
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改spuDetail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //新增sku 和 stock
        saveSkuAndStock(spu);
        log.info("【商品服务】修改商品成功，商品id：{}", spuId );

        sendMessage(spu, "update");
    }

    /**
     * 根据spuId
     * 查询spu
     * @param spuId
     * @return
     */
    public Spu querySpuById(Long spuId) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu == null) {
            throw new LyException(ExceptionEnum.SPU_GOODS_NOT_FOUND);
        }
        //查询spuDetail
        spu.setSpuDetail(queryDetailBySpuId(spuId));
        //查询sku集合
        spu.setSkus(querySkuBySpuId(spuId));
        return spu;

    }

    /**
     *  通过ids 查询 sku集合
     * @param ids
     * @return
     */
    public List<Sku> querySkuByIds(List<Long> ids) {
        List<Sku> skuList = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_GOODS_NOT_FOUND);

        }
        fillSkuByStock(ids, skuList);

        return skuList;

    }

    private void fillSkuByStock(List<Long> ids, List<Sku> skuList) {
        // 查询sku的库存
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnum.SKU_GOODS_NOT_FOUND);

        }
        // 把库存集合变成一个map,其key是skuId, 值是库存值
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        for (Sku sku : skuList) {
            sku.setStock(stockMap.get(sku.getId()));
        }
    }

    /**
     * 减库存
     * @param cartDTOList
     */
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO : cartDTOList) {
            // 直接减库存
            int count = stockMapper.decreaseStock(cartDTO.getSkuId(), cartDTO.getNum());
            if (count != 1) {
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }


        }



    }
}
