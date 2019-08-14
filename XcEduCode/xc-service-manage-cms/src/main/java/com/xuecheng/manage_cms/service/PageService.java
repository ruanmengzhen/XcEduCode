package com.xuecheng.manage_cms.service;


import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageResult;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import sun.security.x509.CertificatePolicyMap;

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
     * @param page 当前页面 从1开始，
     * @param size 每页显示条数
     * @param queryPageResult 查询条件即页面请求参数模型
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageResult queryPageResult){
        //创建条件值对象
        CmsPage cmsPage=new CmsPage();
        //对条件对象做非空校验 当条件不为空的时候 将条件设置给cmsPage
//        //public T orElse(T other)如果存在值，则返回值，否则返回other
//        QueryPageResult qp = Optional.ofNullable(queryPageResult).orElse(new QueryPageResult());
        if (StringUtils.isNotEmpty(queryPageResult.getSiteId())){
            cmsPage.setSiteId(queryPageResult.getSiteId());//站点 精确匹配
        }
        if (StringUtils.isNotEmpty(queryPageResult.getTemplateId())){
            cmsPage.setTemplateId(queryPageResult.getTemplateId());//模板 精确匹配
        }
        if (StringUtils.isNotEmpty(queryPageResult.getPageAliase())){
            cmsPage.setPageAliase(queryPageResult.getPageAliase());//别名 模糊匹配
        }


        //条件匹配器对象
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建条件实例对象,需要条件值对象  条件匹配器作为参数
        Example<CmsPage> example=Example.of(cmsPage, exampleMatcher);

        //对当前页码 和每页显示条数做安全性校验
        if (page<=0){
            page=1;
        }
        page=page-1;//dao中的页码要求是从0开始的
        if (size<=0){
            size=10;
        }
        //分页查询需要分页对象
        Pageable pageable= PageRequest.of(page, size);
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
    public CmsPageResult add(CmsPage cmsPage) {
        //确认页面是否存在 根据页面名称，站点id 页面物理路径 查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //页面不为空 则页面存在存在 给出页面已存在的提示信息
        if (cmsPage1!=null){
           throw  new CustomException(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //设置pageId 由spring data 自动生成
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        //返回响应结果
        CmsPageResult cmsPageResult=new CmsPageResult(CommonCode.SUCCESS,cmsPage );
        return cmsPageResult;


        //return new CmsPageResult(CommonCode.FAIL,null );

    }


    //根据页面id 查询页面信息,返回页面
    public CmsPage findById(String id){
        //调用dao 层的 findById()方法，返回optional
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        //判断optional 是否为空
        CmsPage cmsPage = optional.orElse(null);
        //不为空，返回 cmsPage
        //为空 ， 返回 null
        return cmsPage;
    }

    //更新页面信息
    public CmsPageResult update(String id,CmsPage cmsPage){
        //调用 findById()获取cmsPage
        CmsPage cp = this.findById(id);

        //当cmsPage 为空的时提示页面不存在的信息
        if (cp==null){
            throw new CustomException(CmsCode.CMS_NOT_FINDPAGE);
        }

        // //当页面存在时，将前台传过来的页面的值设置给查询到的cmsPage
        cp.setSiteId(cmsPage.getSiteId());//站点id
        cp.setTemplateId(cmsPage.getTemplateId());//模板id
        cp.setPageName(cmsPage.getPageName());//页面名称
        cp.setPageAliase(cmsPage.getPageAliase());//别名
        cp.setPageWebPath(cmsPage.getPageWebPath());//访问路径
        cp.setPagePhysicalPath(cmsPage.getPagePhysicalPath());//物理路径
        cp.setDataUrl(cmsPage.getDataUrl());//数据URL
        //调用save()方法，
        CmsPage save = cmsPageRepository.save(cp);
        //此对象为空时 提示参数有误
        if (save == null){
            throw  new CustomException(CommonCode.INVAILD_PARAMS);
        }

        //此对象不为空时 给页面响应 修改成功，及修改后的对象
        return new CmsPageResult(CommonCode.SUCCESS,save );


        //否则 返回 失败信息，null
        //return new CmsPageResult(CommonCode.FAIL,null);
    }

    //根据id删除页面
    public ResponseResult delete(String id){
        //根据id 查询页面
        CmsPage cmsPage = this.findById(id);
        //为空，页面不存在
        if (cmsPage==null){
            throw  new CustomException(CmsCode.CMS_NOT_FINDPAGE);
        }
        //当页面存在时 删除页面
        cmsPageRepository.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS );

        //return new ResponseResult(CommonCode.FAIL);
    }

}
