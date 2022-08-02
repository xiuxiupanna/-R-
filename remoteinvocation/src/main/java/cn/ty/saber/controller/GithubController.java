package cn.ty.saber.controller;

import cn.ty.saber.client.GithubFeign;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description：使用 Feign 访问 Github 查询 API
 */
@RestController
@RequestMapping(
        value = "/github",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class GithubController {

    @Resource
    private GithubFeign githubFeign;

    @RequestMapping(
            value = "/search/repositories",
            method = RequestMethod.GET)
    String searchRepo(@RequestParam("str") String str) {
        return githubFeign.searchPepo(str);

    }


}
