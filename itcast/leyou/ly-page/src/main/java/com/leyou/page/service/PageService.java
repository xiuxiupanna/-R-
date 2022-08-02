package com.leyou.page.service;

import com.leyou.item.client.BrandClient;
import com.leyou.item.client.CategoryClient;
import com.leyou.item.client.GoodsClient;
import com.leyou.item.client.SpecClient;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.Spu;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private SpringTemplateEngine templateEngine;


    public Map<String, Object> loadItemModel(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询分类
        List<Category> categories = categoryClient.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        //查询规格
        List<SpecGroup> specs = specClient.querySpecsByCid(spu.getCid3());

        Map<String, Object> data = new HashMap<>();
        data.put("categories",categories);
        data.put("brand", brand);
        data.put("title", spu.getTitle());
        data.put("subTitle", spu.getSubTitle());
        data.put("detail", spu.getSpuDetail());
        data.put("skus", spu.getSkus());
        data.put("specs", specs);
        return data;
    }


    public void createItemHtml(Long spuId) {
        // 创建上下文
        Context context = new Context();
        // 给上下文添加数据
        context.setVariables(loadItemModel(spuId));
        // 获取目标文件路径
        File filePath = getFilePath(spuId);
        // 创建输出流，关联一个文件
//        Writer writer = new BufferedWriter(OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));

       try (PrintWriter printWriter = new PrintWriter(filePath, "UTF-8")) {
           // 利用模板引擎，输出页面内容到文件
           templateEngine.process("item", context, printWriter);

       } catch (IOException e) {
           log.error("【静态页服务】创建商品静态页失败，商品id: {}", spuId, e);

       }
    }

    /**
     * 获取目标文件路径
     * @param spuId
     * @return
     */
    private File getFilePath(Long spuId) {
        //准备目标文件
        File dir = new File("D:\\software\\nginx\\nginx-1.18.0\\nginx-1.18.0\\html\\item");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File filePath = new File(dir, spuId + ".html");
        return filePath;
    }

    public void deleteItemHtml(Long spuId) {
        File path = getFilePath(spuId);
        if (path.exists()) {
            path.delete();
        }

    }
}
