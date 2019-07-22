package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;//商品信息，存储一般数据是个spu
import com.pinyougou.pojo.TbGoodsDesc;//商品的扩展信息 存储信息的描述，图片 等一些数据较大的信息是个spu
import com.pinyougou.pojo.TbItem;//存储商品的规格属性，是sku

import java.io.Serializable;
import java.util.List;

//商品信息的组合实体类
public class Goods  implements Serializable {
    private TbGoods tbGoods;
    private TbGoodsDesc goodsDesc;
    private List<TbItem> itemList;

    public Goods() {
    }

    public Goods(TbGoods tbGoods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
        this.tbGoods = tbGoods;
        this.goodsDesc = goodsDesc;
        this.itemList = itemList;
    }

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "tbGoods=" + tbGoods +
                ", goodsDesc=" + goodsDesc +
                ", itemList=" + itemList +
                '}';
    }
}
