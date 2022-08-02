package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryService;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseEntity.ok(brandService.queryBrandByPage(page, rows, sortBy, desc, key));

    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 根据商品的分类查询对应的品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.queryByCid(cid));


    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryById(id));
    }


    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("ids")
    public ResponseEntity<List<Brand>> queryByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryByIds(ids));

    }



}
