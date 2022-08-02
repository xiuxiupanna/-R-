package com.leyou.item.client;

import com.leyou.item.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface BrandClient {
    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("brand/{id}")
    Brand queryById(@PathVariable("id") Long id);

    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("brand/ids")
    List<Brand> queryByIds(@RequestParam("ids") List<Long> ids);

}
