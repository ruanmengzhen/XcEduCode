package com.xuecheng.manage_cms.service;


import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//cms 页面的业务逻辑层  页面的条件查询
@Service
public class PageService {

    //注入dao 查询数据库
    @Autowired
    CmsPageRepository cmsPageRepository;


    /**分页查询
     *查询条件如下：
     * 站点Id：精确匹配
     * 模板Id：精确匹配
     * 页面别名：模糊匹配
     * @param pageNum 当前页面 从1开始，
     * @param PageSize 每页显示条数
     * @param queryPageResult 查询条件即页面请求参数模型
     * @return
     */
    public QueryResponseResult findList(int pageNum, int PageSize, QueryPageResult queryPageResult){
        //创建条件值对象
        CmsPage cmsPage=new CmsPage();
        //对条件对象做非空校验
//        //public T orElse(T other)如果存在值，则返回值，否则返回other
//        QueryPageResult qp = Optional.ofNullable(queryPageResult).orElse(new QueryPageResult());
        if (StringUtils.isEmpty(queryPageResult.getSiteId())){
            cmsPage.setSiteId(queryPageResult.getSiteId());//站点 精确匹配
        }
        if (StringUtils.isEmpty(queryPageResult.getTemplateId())){
            cmsPage.setTemplateId(queryPageResult.getTemplateId());//模板 精确匹配
        }
        if (StringUtils.isEmpty(queryPageResult.getPageAliase())){
            cmsPage.setPageAliase(queryPageResult.getPageAliase());//别名 模糊匹配
        }


        //条件匹配器对象
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建条件实例对象,需要条件值对象  条件匹配器作为参数
        Example<CmsPage> example=Example.of(cmsPage, exampleMatcher);

        //对当前页码 和每页显示条数做安全性校验
        if (pageNum<=0){
            pageNum=1;
        }
        pageNum=pageNum-1;//dao中的页码要求是从0开始的
        if (PageSize<=0){
            PageSize=10;
        }
        //分页查询需要分页对象
        Pageable pageable= PageRequest.of(pageNum, PageSize);
        //调用dao层的分页查询方法 条件查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        //创建分页请求参数对象，设置请求的数据列表和 数据总和
        QueryResult<CmsPage> cmsPageQueryResult=new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());//数据列表
        cmsPageQueryResult.setTotal(all.getTotalElements());//数据总和

        //创建返回值对象，需要响应结果ResultCode 和请求参数 queryResult
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
        return queryResponseResult;
    }


    /**
     * 新增页面
     * @param cmsPage
     * @return 返回分页结果
     */
    public CmsPageResult add(CmsPage cmsPage){
        //确认页面是否存在 根据页面名称，站点id 页面物理路径 查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //页面不存在 则调用到的添加方法进行添加
        if (cmsPage1==null){
            //设置pageId 由spring data 自动生成
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            //返回响应结果
            CmsPageResult cmsPageResult=new CmsPageResult(CommonCode.SUCCESS,cmsPage );
            return cmsPageResult;

        }
        return new CmsPageResult(CommonCode.FAIL,null );

    }

}
