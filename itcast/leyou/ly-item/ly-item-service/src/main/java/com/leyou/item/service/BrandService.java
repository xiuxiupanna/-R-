package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //条件过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                                    .orEqualTo("letter", key.toUpperCase());
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);

        }
        //搜索结果
        List<Brand> brandList = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //封装返回
        PageInfo<Brand> info = new PageInfo<>(brandList);

        return new PageResult<>(info.getTotal(), brandList );


    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        int count = brandMapper.insertSelective(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_INSERT_ERROR);
        }

        for (Long cid : cids) {
             count = brandMapper.insertCategoryBrand(cid, brand.getId());
             if (count != 1) {
                 throw new LyException(ExceptionEnum.BRAND_INSERT_ERROR);

             }

        }
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);

        }
        return brand;
    }

    /**
     * 根据商品的分类查询对应的品牌
     * @param cid
     * @return
     */
    public List<Brand> queryByCid(Long cid) {
        List<Brand> brandList = brandMapper.queryByCid(cid);

        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);

        }
        return brandList;


    }

    /**
     * 根据ids批量查询商品
     * @param ids
     * @return
     */
    public List<Brand> queryByIds(List<Long> ids) {
        List<Brand> brandList = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brandList;

    }
}
