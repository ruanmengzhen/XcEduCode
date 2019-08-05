package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.CookieUtil;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//购物车的控制层
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference(timeout = 6000)
    private CartService cartService;



    /** //从cookie中获取购物车列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取登录人  当获取的用户名是 annoymousUser 表示没有登录，
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //得到Cookie的值,
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        //判断购物车列表是否为空
        if (cartListString==null || cartListString.equals("")){
            //没有购物车列表 将其设置为一个空数组
            cartListString="[]";
        }
        //有购物车列表将其转为json，形式的 list列表 并返回
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);


        //判断是否登录
        if (username.equals("anonymousUser")){//未登录，读取cookie中的购物车列表

            return cartList_cookie;

        }else {//已登录，读取redis中的购物车列表

            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            //判断cookie中是否有购物车
            if (cartList_cookie.size()>0){
                //有，则将cookie中的购物车合并到redis中，合并后删除cookie中的购物车
                cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //删除cookie中的购物车
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的购物车存储到redis中
                cartService.saveCartListToRedis(cartList_redis,username);

            }


            return cartList_redis;
        }
    }


    /**
     * 向购物车中添加商品
     *
     * @param itemId sku id
     * @param num 商品数量
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){

        //允许跨域请求，设置头信息
        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials","true");

        //获取登录人  当获取的用户名是 annoymousUser 表示没有登录，
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户："+username);

        try {
            //获取cookie中的购物车列表
            List<Cart> cartList = findCartList();
            //向购物车中添加商品
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            //判断是否登录
            if (username.equals("anonymousUser")){//未登录，将商品保存到cookie中
                //将购物车存入cookie中 设置Cookie的值 在指定时间内生效, 编码参数(指定编码)
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
                System.out.println("向 cookie 存入数据");
            }else {//已登录，将商品保存到redis中

                cartService.saveCartListToRedis(cartList,username);
            }

            return new Result(true,"添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

}
