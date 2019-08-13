package com.xuecheng.manage_cms.dao;
//页面dao的测试

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)//
@SpringBootTest//表示是个spring boot的测试类 启动时会去找springboot的启动类  加载spring容器
public class CmsPageRepositoryTest {


    @Autowired
    CmsPageRepository cmsPageRepository;

    //自定义条件查询 根据站点id精确匹配查询
    @Test
    public void testFindAllByExample(){
        //分页参数
        int page=0;
        int size=10;
        Pageable pageable = new PageRequest(page,size );


        //条件值对象 -----> 条件值设置的是精确匹配
        CmsPage cmsPage=new CmsPage();

      /*  cmsPage.setSiteId("s01");//站点id
        cmsPage.setTemplateId("t01"); //设置模板 id
        */
        //根据别名模糊匹配
        cmsPage.setPageAliase("课程");
        //条件匹配器对象
        ExampleMatcher exampleMatcher=ExampleMatcher.matching();
        //使用条件匹配器进行 匹条件 根据别名模糊匹配
        //设置条件匹配器的值  参数：要查询的提交值，匹配值-->精确匹配，模糊匹配等
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());//包含别名
//ExampleMatcher.GenericPropertyMatchers.contains() 包含


        //创建条件实例，参数：条件值对象，条件匹配器（精确匹配、模糊匹配）
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);

        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        for (CmsPage cmsPage1 : content) {
            System.out.println(cmsPage1);
        }

       //System.out.println(all);
    }

    //测试分页查询,使用断点 debugger 方式进行测试
    @Test
    public void testFindPage(){
        Pageable pageable = PageRequest.of(1,10 );
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }


    //添加
    @Test
    public void testSave(){
        CmsPage cmspage=new CmsPage();
        cmspage.setSiteId("s01");
        cmspage.setTemplateId("t01");
        cmspage.setPageName("测试页面");
        cmspage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams=new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmspage.setPageParams(cmsPageParams);

        cmsPageRepository.save(cmspage);
    }

    //修改
    @Test
    public void testUpdate(){
        //查询 根据id 查询
        Optional<CmsPage> optional = cmsPageRepository.findById("5d4a91f6eccda31af4518462");
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("测试页面001");

            cmsPageRepository.save(cmsPage);
        }

    }

}
