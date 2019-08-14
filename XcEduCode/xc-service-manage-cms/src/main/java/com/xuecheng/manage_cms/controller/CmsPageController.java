package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页controller  实现分页查询接口
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    //注入service层
    @Autowired
    PageService pageService;

    /**
     * 测试 controller 接口是否可以运行
     * @param page 当前页码
     * @param size 每页显示条数
     * @param queryPageResult 页面请求模型
     * @return
     */
    @Override
    @GetMapping("/list/{page}/{size}")//发送get请求  @PathVariable:用在参数列表绑定url中的占位符，value属性表示占位符
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageResult queryPageResult) {

       /* //创建请求对象
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(2l);//数据总数
        //新建list集合 表示数据列表
        List list=new ArrayList();
        //创建cms页面实体类
        CmsPage cmsPage=new CmsPage();
        cmsPage.setPageAliase("首页");
        //添加数据到请求数据列表
        list.add(cmsPage);
        queryResult.setList(list);//请求的数据列表
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
*/
       //调用service的分页查询方法
        QueryResponseResult queryResponseResult = pageService.findList(page, size, queryPageResult);
        return queryResponseResult;
    }

    //新建页面
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {

        return pageService.add(cmsPage);
    }

    /**
     * 根据页面id 查询页面信息,返回页面
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.findById(id);
    }

    /**
     *修改页面信息
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id")String id,@RequestBody CmsPage cmsPage) {

        return pageService.update(id,cmsPage );
    }


    /**
     * 删除页面
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id")String id) {
        return pageService.delete(id);
    }
}
