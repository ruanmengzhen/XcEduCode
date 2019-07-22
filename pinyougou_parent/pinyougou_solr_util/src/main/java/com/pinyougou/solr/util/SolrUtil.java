package com.pinyougou.solr.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//编写专门的导入程序，将商品数据导入到 Solr 系统中
@Component
public class SolrUtil {
    @Autowired
    public SolrTemplate solrTemplate;
    @Autowired//需要引入spring相关依赖，在applicationcontext.xml中配置注解扫描
    private TbItemMapper itemMapper;

    //导入商品数据
    public void importItemData(){
        //将状态为1 的数据查询出来 添加进 Solr中
        //构建查询条件
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> items = itemMapper.selectByExample(example);
        for (TbItem item : items) {
            //获取Spe的数据，转为json对象
            Map specMap = JSON.parseObject(item.getSpec());
            //给带注解的字段 赋值
            item.setSpecMap(specMap);
            System.out.println(item.getBrand());
        }

        solrTemplate.saveBeans(items);
        solrTemplate.commit();


    }

    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = context.getBean(SolrUtil.class);
        solrUtil.importItemData();
    }
}
