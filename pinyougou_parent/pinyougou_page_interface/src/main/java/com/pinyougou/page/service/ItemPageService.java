package com.pinyougou.page.service;
//生成静态页面的接口
public interface ItemPageService {

    /**
     * 根据商品id 生成商品详情页面
     * @param goodsId
     * @return
     */
    public Boolean getItemHtml(Long goodsId);

    /**
     * 根据商品id 删除商品详情页面
     * @param goodsIds
     * @return
     */
    public Boolean deleteItemHtml(Long[] goodsIds);
}
