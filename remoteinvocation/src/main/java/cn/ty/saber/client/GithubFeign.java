package cn.ty.saber.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @Description：使用 Feign 访问 Github 查询 API
 */
@FeignClient(value = "demo01")
public interface GithubFeign {

    @RequestMapping(
            value = "/search/repositories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    String searchPepo(@RequestParam("str") String str);


}
