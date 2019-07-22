package com.pinyougou.search.service;
//按照规格表的数据去搜索

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索方法 首页的搜索关键字
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);


    /**
     * 向solr索引库中导入数据
     * @param list
     */
    public void importList(List list);


    /**
     * 根据商品id 删除索引库的商品信息
     * @param goodsIdList 商品id的集合
     */
    public void deleteByGoodsId(List goodsIdList);
}
