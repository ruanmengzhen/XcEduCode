package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms配置的管理接口",description = "cms配置管理接口，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi{

    /**
     * 根据配置id获取cms的配置数据模型
     * @param id
     * @return
     */
    @ApiOperation("根据配置id获取cms的配置数据模型")
    @ApiImplicitParam(name = "id",value = "配置id",required = true,defaultValue = "String")
    public CmsConfig getmodel(String id);
}
