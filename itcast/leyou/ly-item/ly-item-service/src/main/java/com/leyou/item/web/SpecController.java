package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类查询规格组信息
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGrooupByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specService.queryGroupByCid(cid));

    }

    /**
     * 根据条件查询的参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching) {
        return ResponseEntity.ok(specService.queryParams(gid, cid, searching));

    }
    /**
     * 根据cid查询规格组以及组内参数
     * @param cid
     * @return
     */
    @GetMapping("/list/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid) {

        return ResponseEntity.ok(specService.querySpecsByCid(cid));


    }










}
