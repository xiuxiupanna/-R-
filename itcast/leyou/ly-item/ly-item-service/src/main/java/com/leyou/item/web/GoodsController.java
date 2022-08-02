package com.leyou.item.web;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询Spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key) {

        return ResponseEntity.ok(goodsService.querySpuByPage(page, rows, saleable, key));

    }

    /**
     * 新增商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 更新商品
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 根据spu的id查询spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> queryDetailBySpuId(@PathVariable("spuId") Long spuId) {
        return ResponseEntity.ok(goodsService.queryDetailBySpuId(spuId));

    }

    /**
     * 根据SpuId查询sku集合
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));

    }


    /**
     * 根据 ids查询sku商品集合
     * @param ids
     * @return
     */
    @GetMapping("/sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));

    }


    /**
     * 根据spu的id查询spu
     * @param spuId
     * @return
     */
     @GetMapping("spu/{id}")
     public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long spuId) {
         return ResponseEntity.ok(goodsService.querySpuById(spuId));
     }


    /**
     * 减库存
     * @param cartDTOList
     */
    @PutMapping("/stock/decrease")
   public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> cartDTOList) {
        goodsService.decreaseStock(cartDTOList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }






}
