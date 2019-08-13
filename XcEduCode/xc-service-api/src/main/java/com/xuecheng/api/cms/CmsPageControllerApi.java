package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

//分页查询的接口
@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {


    /**
     * 页面查询
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @param queryPageResult 页面请求模型
     * @return
     */
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="pageNum",value ="当前页 码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="pageSize",value="每页显示条数",required=true,paramType="path",dataType="int")
    })
    public QueryResponseResult findList(int pageNum, int pageSize, QueryPageResult queryPageResult);




    /**新增页面
     *
     * @param cmsPage
     * @return 返回 是否操作成功，操作代码，提示信息
     */
    public CmsPageResult add(CmsPage cmsPage);




}
