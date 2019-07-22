package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义认证类的实现类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    //需要使用SellerService层的方法进行查询商家信息，因此定义SellerService的变量 有参构造或者set get 方法
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了UserDetailsServiceImpl");

        //构建角色列表,创建list集合 使用角色和权限的接口GrantedAuthority作为泛型
        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        //添加角色列表,GrantedAuthority接口的默认实现SimpleGrantedAuthority,
        // 因此获取角色需要创建impleGrantedAuthority的对象
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //根据用户名查询商家信息
        TbSeller seller = sellerService.findOne(username);

        //如果seller不为空且状码为1，则返回用户名，密码，角色列表 到UserDetails中的User对象中
        if (seller !=null && seller.getStatus().equals("1")){
            return new User(username,seller.getPassword(),grantedAuthorities);
        }else {
            return null;
        }


       /* if(seller == null){
            throw  new Exception("该用户尚未注册");
        }
        if(!seller.getStatus().equals("1")){
            throw  new ShopUserAuditFailureException("该用户正在审核中...");
        }

        return new User(username,seller.getPassword(),grantedAuthorities);

*/



    }
}
