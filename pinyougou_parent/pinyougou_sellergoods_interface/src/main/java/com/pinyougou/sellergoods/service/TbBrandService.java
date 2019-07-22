package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;
import org.apache.ibatis.annotations.Insert;

import java.util.List;
import java.util.Map;

//品牌类接口
public interface TbBrandService {

//查询所有品牌信息
 public List<TbBrand> findAll();

 //将品牌信息分页展示
 public PageResult findPage(int pageNum, int pageSize);

 //品牌的添加 修改功能，添加修改是否成功需要返回一个结果
  public Result save(TbBrand tbBrand);

  //根据id查询一个品牌
 TbBrand findOne(Long id);

 //根据id进行删除品牌
 void delete(Long[] ids);

 //搜索分页
 public PageResult findPage(TbBrand brand, Integer pageNum, Integer pageSize);

 //查询品牌下拉列表数据
    List<Map> selectOptionList();
}
