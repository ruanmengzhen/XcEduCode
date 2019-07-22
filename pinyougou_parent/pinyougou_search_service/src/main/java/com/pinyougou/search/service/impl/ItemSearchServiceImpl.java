package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 搜索关键字
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {

        //创建返回的map对象
        Map<String, Object> map = new HashMap<>();

       /* //构建关键字搜索条件
        Query query = new SimpleQuery("*:*");
        //搜索的关键字
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //分页查询
        Page<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        //List<TbItem> content = page.getContent();//当前页展示的数据
        */

       //关键字搜索的时候对空格进行处理
       String keywords = (String) searchMap.get("keywords");
       //将空格替换成空字符串
        String replaceKeywords = keywords.replace(" ", "");
        searchMap.put("keywords",replaceKeywords);

        //1.按关键字查询（高亮显示
        map.putAll(searchList(searchMap));
        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //3.根据商品分类名称查询品牌列表和规格列表
        /*if (categoryList.size()>0){
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }*/
        String category = (String) searchMap.get("category");
        if (!category.equals("")) {
            map.putAll(searchBrandAndSpecList(category));
        } else {
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }


        return map;
    }

    //1.按关键字查询（高亮显示
    private Map<String, Object> searchList(Map searchMap) {
        //创建返回的map对象
        Map<String, Object> map = new HashMap<>();
        //搜索的关键字 高亮显示
        //2.创建高亮显示需要用的query对象
        HighlightQuery query = new SimpleHighlightQuery();
        //3.构建高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//在哪一行高亮显示
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮饿肚肚前缀
        highlightOptions.setSimplePostfix("</em>");//高亮的后缀
        //4.给查询对象设置高亮显示
        query.setHighlightOptions(highlightOptions);

        //1.1按关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按商品分类过滤
        if (!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.44按规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {

                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5按价格过滤
        if (!"".equals(searchMap.get("price"))) {//当页面传的价格参数不为空字符串时
            String priceStr = (String) searchMap.get("price");//价格
            String[] price = priceStr.split("-");//价格区间：0-500，拆分为 起始值：0   终点值：500
            //如果价格区间的起始值不为 0，那么起始值就大于等于0索引处的值
            if (!price[0].equals("0")) {
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);//大于等于price[0]处的值
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //如果价格区间的终点值不为*即正无穷大，那么终点值就小于等于1索引处的值
            if (!price[1].equals("*")) {
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);//小于等于1索引处的值
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //4.1分页查询
        Integer pageNum = (Integer) searchMap.get("pageNum");//获取页面发送的当前页码
        if (pageNum == null && pageNum < 1) {//初始化 当前页码
            pageNum = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//获取页面发送到后台的每页显示的记录数
        if (pageSize == null && pageSize < 0) {
            pageSize = 20;
        }
        //设置数据从第几条记录开始查询
        query.setOffset((pageNum-1)*pageSize);
        //设置每页显示的记录数
        query.setRows(pageSize);

        //1.6排序
        String sortValue = (String) searchMap.get("sort");//获取排序方式,ASC  DESC
        String sortField = (String) searchMap.get("sortField");//获取排序字段
        //当排序方式不为空时，默认排序方式按升序
        if (!sortValue.equals("")&&sortValue!=null){
            if (sortValue.equals("ASC")){
                Sort sort =new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }else {
                Sort sort =new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }

        }





        //***********  获取高亮结果集  ***********
        //1.构建高亮页对
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //6.获取高亮的入口集合
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        //7.遍历集合 获取高亮列表
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();//高亮列表
            //获取原来的实体类
            TbItem item = highlightEntry.getEntity();
            //遍历高亮列表得到每个域存储的值
            for (HighlightEntry.Highlight h : highlightList) {
                List<String> snipplets = h.getSnipplets();//每个域存储的值
                System.out.println(snipplets);
                item.setTitle(snipplets.get(0));//设置标题
            }

           /* //获取每个域中的值  只有一列，可以拿0索引处的值
           if (highlightList.size()>0 && highlightList.get(0).getSnipplets().size()>0 ){

                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }*/

        }

        map.put("rows", page.getContent());
        //分页查询 返回总页码 和总记录数
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());//总记录数
        return map;
    }


    //2.根据关键字查询商品分类
    private List<String> searchCategoryList(Map searchMap) {
        //创建返回的集合
        List<String> list = new ArrayList<>();
        //构建关键字搜索条件
        Query query = new SimpleQuery("*:*");
        //根据关键字查询  相当于sql语句的 where条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项  相当于 sql语句的 group by 分组条件
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //分组分页查询,获取分组分页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");//根据那个域获取的结果
        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        //遍历集合
        for (GroupEntry<TbItem> entry : entryList) {
            //将分组结果添加到返回值中
            list.add(entry.getGroupValue());
        }
        return list;
    }

    @Autowired//注入redisTemplate 从缓存中获取数据
    private RedisTemplate redisTemplate;

    /**
     * 查询品牌列表和规格列表,
     *
     * @param category
     * @return
     */
    private Map<String, Object> searchBrandAndSpecList(String category) {//参数;分类名称
        //创建返回的map对象
        Map<String, Object> map = new HashMap<>();
        //1.根据商品分类名称获取模板ID
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (templateId != null) {
            //根据模板id获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //根据模板id获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);
        }
        return map;
    }


    /**
     * 向solr索引库中导入数据
     * @param list
     */
    @Override
    public void importList(List list) {
        if (list!=null&&list.size()>0){
            solrTemplate.saveBeans(list);
            solrTemplate.commit();
        }
    }

    /**
     * 根据商品id 删除索引库的商品信息
     * @param goodsIdList 商品id的集合
     */
    @Override
    public void deleteByGoodsId(List goodsIdList) {

        Query query = new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
