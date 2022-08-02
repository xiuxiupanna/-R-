package com.leyou.item.client;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service", path = "spec")
public interface SpecClient {

    /**
     * 根据条件查询的参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("/params")
    List<SpecParam> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching);

    /**
     * 根据cid查询规格组以及组内参数
     * @param cid
     * @return
     */
    @GetMapping("/list/{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);





}
