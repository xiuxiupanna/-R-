package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.thoughtworks.xstream.security.RegExpTypePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);

        }
        return list;

    }

    /**
     * 根据条件查询参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean searching) {

        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(specParam);

        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);

        }
        return list;
    }

    /**
     * 根据cid查询规格组以及组内参数
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecsByCid(Long cid) {
        //查询分类下的组
        List<SpecGroup> specGroups = queryGroupByCid(cid);

        //查询当前分类下的所有参数
        List<SpecParam> specParams = queryParams(null, cid, null);
        //我们使用流的api.把集合进行分组，分组的依据是groupId
        Map<Long, List<SpecParam>> map = specParams.stream()
                .collect(Collectors.groupingBy(SpecParam::getGroupId));

        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
