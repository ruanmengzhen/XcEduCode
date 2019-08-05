package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList 购物车列表
     * @param ItemId skuid
     * @param num 商品数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long ItemId,Integer num);

    /**
     * 根据用户名 从redis中查找 购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);


    /**
     * 向redis中添加购物车
     * @param cartList
     * @param username
     */
    public void saveCartListToRedis(List<Cart> cartList,String username);


    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
