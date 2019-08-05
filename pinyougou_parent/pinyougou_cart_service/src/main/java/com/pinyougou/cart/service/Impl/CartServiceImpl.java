package com.pinyougou.cart.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    /**
     * 合并购物车,将cookie中的添加到redis中 或者将redis中的添加到cookie中
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //遍历购物车1 ，将1 添加到2中
        for (Cart cart : cartList1) {
            //获取购物明细
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                //将1中的商品添加到 2 中
                cartList2 = addGoodsToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }



        return cartList2;
    }




    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据用户名 从redis中查找 购物车
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        //判断购物车是否存在
        if (cartList == null) {
            cartList = new ArrayList();
        }
        return cartList;
    }


    /**
     * 向redis中添加购物车
     *
     * @param cartList
     * @param username
     */
    @Override
    public void saveCartListToRedis(List<Cart> cartList, String username) {
        //根据用户名将购物车存储到Redis中 使用hash存储，用户名做键，购物车做值
        redisTemplate.boundHashOps("cartList").put(username,cartList);

        System.out.println("将购物车列表存入redis");

    }


    /**
     * 添加商品到购物车
     *
     * @param cartList 购物车列表
     * @param itemId skuid
     * @param num 商品数量
     * @return
     */
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据itemId查询sku信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");

        }
        //2.获取商家id
        String sellerId = item.getSellerId();

        //3.根据商家id查询是否有该商家对应的购物车
        //调用购物车对象的方法
        Cart cart = searchCartBySellerId(cartList, sellerId);

        //4.没有购物车，则，新建购物车对象，
        if (cart == null) {
            cart = new Cart();//创建购物车对象
            cart.setSellerId(sellerId);//商家id
            cart.setSellerName(item.getSeller());//商家名称
            //调用创建购物明细的方法，
            TbOrderItem orderItem = createOrderItem(item, num);
            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);
            //需要的是个购物车明细的集合，因此需要创建一个集合存储购物明细对象
            cart.setOrderItemList(orderItemList);//购物车明细

            //4.1.将购物车对象添加到购物车列表中
            cartList.add(cart);

        } else {
            //5有购物车，判断该购物车中有没有该商品   调用查询购物明细对象的方法
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //5.1没有该商品，新建购物明细，调用创建购物明细的方法，并将购物明细添加到购物车中
            if (orderItem == null) {
                orderItem = createOrderItem(item, num);
                //获取购物明细列表，将购物明细添加进列表
                cart.getOrderItemList().add(orderItem);
            } else {
                //5.2 有该商品，则将商品的数量累加，价钱用单价*数量
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                //判断购物明细的数量
                if (orderItem.getNum() <= 0) {
                    //移除购物明细对象
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果没有购物明细 则将购物车对象移除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }


    //定义方法查询购物车对象 根据skuid查询购物车明细对象
    public Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        //当购物车中的商家id和参数sellerId相同时，则有该购物车对象，否则没有
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }

        return null;
    }


    //定义方法查询购物明细对象,根据sku的id  即itemId 查询购物明细对象
    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        //遍历orderItemList
        //orderItem中的itemId的值与itemId相同时，有该对象，否则没有
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }


    //定义方法创建购物车明细，设置购物明细到的每一项的值，不包含订单id即order_id，因为还没有提交没法生成订单id
    public TbOrderItem createOrderItem(TbItem item, Integer num) {
        //当数量大于0时，设置订单明细
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();

        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());//商品图片地址
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));//商品总金额,doubleValue()获取价格的值

        return orderItem;
    }


}
