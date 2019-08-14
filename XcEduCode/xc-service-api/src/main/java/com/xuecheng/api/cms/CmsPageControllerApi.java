package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

//分页查询的接口
@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {


    /**
     * 页面查询
     * @param page 当前页码
     * @param size 每页显示条数
     * @param queryPageResult 页面请求模型
     * @return
     */
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value ="当前页 码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value="每页显示条数",required=true,paramType="path",dataType="int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageResult queryPageResult);




    /**新增页面
     *
     * @param cmsPage
     * @return 返回 是否操作成功，操作代码，提示信息
     */
    @ApiOperation("新建页面列表")
    public CmsPageResult add(CmsPage cmsPage);




    /**
     * 根据页面id 查询页面信息,返回页面  使用get请求
     * @param id
     * @return
     */
    @ApiOperation("根据id 查询页面信息")
    @ApiImplicitParam(name = "id",value = "页面id",required = true,dataType = "String")
    public CmsPage findById(String id);


    /**
     * 修改页面
     * @param cmsPage
     * @return
     */
    @ApiOperation("修改页面")
    @ApiImplicitParam(name = "id",value = "页面id",required = true,defaultValue = "String")
    public CmsPageResult edit(String id,CmsPage cmsPage);


    /**
     * 根据id删除页面
     * @param id
     * @return
     */
    @ApiOperation("删除页面")
    @ApiImplicitParam(name = "id",value = "页面id",required = true,defaultValue = "String")
    public ResponseResult delete(String id);
}
