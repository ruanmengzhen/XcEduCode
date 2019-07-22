package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加  页面点击新增规格时需要新增两部分内容，一个是增加的规格内容，一个是增加的规格选项的内容，
	 * 即一个是specification类中的数据，一个是specificationOptio类中的数据
	 */
	@Override
	public void add(Specification specification) {
		//获取规格
		TbSpecification tbSpecification = specification.getTbSpecification();
		//调用规格的dao层方法添加规格
		specificationMapper.insert(tbSpecification);
		//Long id = tbSpecification.getId();
		//获取规格选项行
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		//遍历规格选项行
		for (TbSpecificationOption option : specificationOptionList) {
			//System.out.println(option);
			//设置规格选项行的spec_id值=规格的id值,
			option.setSpecId(tbSpecification.getId());
			//调用规格选项行的dao层添加方法逐个添加option  注入SpecificationOptionMapper
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//获取规格
		TbSpecification tbSpecification = specification.getTbSpecification();
		//调用规格的dao层方法添加规格
		specificationMapper.updateByPrimaryKey(tbSpecification);
		//根据id删除规格选项行
		//spec_id=id;构建查询条件
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(tbSpecification.getId());
		//删除所有的选项规格
		specificationOptionMapper.deleteByExample(example);
		//获取规格选项行
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		//遍历规格选项行
		for (TbSpecificationOption option : specificationOptionList) {
			//System.out.println(option);
			//设置规格选项行的spec_id值=规格的id值,
			option.setSpecId(tbSpecification.getId());
			//调用规格选项行的dao层添加方法逐个添加option  注入SpecificationOptionMapper
			specificationOptionMapper.insert(option);
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//根据id查询规格内容
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//查询规格选项表
		//spec_id=id;构建查询条件
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example);

		//创建组合实体类
		Specification spec = new Specification();
		//将查询到的规格和规格选项设置到组合实体类中
		spec.setTbSpecification(tbSpecification);
		spec.setSpecificationOptionList(optionList);
		return spec;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			//根据id删除规格选项行
			//spec_id=id;构建查询条件
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			//删除所有的选项规格
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	//查询规格下拉列表数据
	@Override
	public List<Map> selectOptionList() {
		List<Map> list=new ArrayList<Map>();
		//查询所有
		List<TbSpecification> specifications = specificationMapper.selectByExample(null);
		for (TbSpecification spec : specifications) {
			HashMap map = new HashMap();
			map.put("id",spec.getId());
			map.put("text",spec.getSpecName());
			list.add(map);
		}
		return list;
	}
	
}
