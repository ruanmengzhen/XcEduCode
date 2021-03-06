package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
//import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
//import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Destination;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){
		if (page<=0){
			page=1;
		}
		if (rows<=0){
			rows=10;
		}
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		//使用SecurityContextHolder动态获取商家id
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getTbGoods().setSellerId(name);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//校验当前是否是当前商家Id
		//获取根据商品id查询到的商品
		Goods findOneGoods = goodsService.findOne(goods.getTbGoods().getId());//
		//获取当前商家id
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		//如果不是当前商家，提示非法操作
		if (!sellerId.equals(findOneGoods.getTbGoods().getSellerId()) || !sellerId.equals(goods.getTbGoods().getSellerId())){
			return new Result(false,"非法操作");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除,
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}


	/*@Reference
	private ItemSearchService itemSearchService;*/
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		if (page<=0){
			page=1;
		}
		if (rows<=0){
			rows=10;
		}
		//使用SecurityContextHolder动态获取商家id
		//String name = SecurityContextHolder.getContext().getAuthentication().getName();
		//goods.setSellerId(name);
		return goodsService.findPage(goods, page, rows);		
	}


	@Autowired
	private Destination queueSolrDestination;//用于发送solr导入的消息
	@Autowired
	private JmsTemplate jmsTemplate;//发MQ消息
	@Autowired
	private Destination topicPsgeDestination;//用户静态页面的生成
	/**
	 * 更改状态码
	 * @param ids 商品id
	 * @param status 状态
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status){
		try {
			goodsService.updateStatus(ids, status);
			//根据商品id 和 商品状态 查询SKU
			if ("1".equals(status)){//审核通过，增量更新
				List<TbItem> itemList = goodsService.findByGoodsIdAndStatus(ids, status);
				if (itemList!=null && itemList.size()>0){
					//调用itemSearchService中的导入数据的方法，则，在此工程中注入search_interface的依赖
					//itemSearchService.importList(itemList);

					
					//sku是json 转为字符串
					final String jsonString = JSON.toJSONString(itemList);
					//发送消息
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});

				}else {
					System.out.println("没有数据明细");
				}
			}


			//静态页面生成
			for (final Long id : ids) {
				//itemPageService.getItemHtml(id);

				//发送消息
				jmsTemplate.send(topicPsgeDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(id+"");
					}
				});

			}



			return new Result(true, "修改状态码成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改状态码失败");
		}
	}

	/*@Reference(timeout=40000)//放置超时
	private ItemPageService itemPageService;
	*//**
	 * 生成商品详情的静态页面
	 * @param goodsId
	 *//*
	@RequestMapping("/getHtml")
	public void getHtml(Long goodsId){
		itemPageService.getItemHtml(goodsId);
	}*/

}
