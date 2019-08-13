package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
//页面请求模型  条件查询的类型

@Data//自动生成 getter setter 方法
@ToString//自动生成 tostring 方法
public class QueryPageResult extends RequestData {
    /**
     * 站点id 页面id 页面名称 页面别名  模板id
     */

    //站点ID
    @ApiModelProperty("站点id")
    private String siteId;

    //页面ID
    @ApiModelProperty("页面ID")
    private String pageId;

    //页面名称
    @ApiModelProperty("页面名称")
    private String pageName;

    //别名
    @ApiModelProperty("页面别名")
    private String pageAliase;

    //模板id
    @ApiModelProperty("模板id")
    private String templateId;


}
