package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.common.IdWorker;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 增加 从购物车点击结算向 订单中添加 信息
     */
    @Override
    public void add(TbOrder order) {
        //1.从redis中获取用户的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //创建订单号 列表
        List<String> orderIdList=new ArrayList<>();
        //定义订单支付的总金额
        Double total_money = 0.00;

        //遍历购物车列表，拿到每个商家的购物车
        for (Cart cart : cartList) {
            //通过工具类IdWorker 获取oderId
            long orderId = idWorker.nextId();
            //创建新的订单对象
            TbOrder tbOrder = new TbOrder();
            //设置每个购物车对应的订单信息
            tbOrder.setOrderId(orderId);//订单ID
            tbOrder.setUserId(order.getUserId());//用户名
            tbOrder.setPaymentType(order.getPaymentType());//支付类型
            tbOrder.setStatus("1");//状态：未付款
            tbOrder.setCreateTime(new Date());//订单创建日期
            tbOrder.setUpdateTime(new Date());//订单更新日期
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//手机号
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家ID

            //定义总金额的变量
            double money = 0;

            //2.获取购物明细列表 并遍历，
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //设置购物明细表中的id  订单id  和商家id，获取累加的金额：即总合计金额
                Long orderItemId = idWorker.nextId();//购物明细id
                orderItem.setId(orderItemId);
                orderItem.setOrderId(orderId);
                orderItem.setSellerId(cart.getSellerId());
                money += orderItem.getTotalFee().doubleValue();
                //将购物明细信息 添加到数据库
                orderItemMapper.insert(orderItem);

            }

            //设置订单明细上的总金额
            tbOrder.setPayment(new BigDecimal(money));//付款金额

            //将订单信息添加到数据库
            orderMapper.insert(tbOrder);
            orderIdList.add(orderId+"");//将订单号 添加到订单号列表中得到的数据如： [a, b, c]
            total_money += money;//获取总的支付金额
        }

        //判断是否是微信支付，如果是 则向支付日志表中添加一条信息
        if ("1".equals(order.getPaymentType())) {
            //创建支付日志 对象 添加数据
            TbPayLog payLog = new TbPayLog();
            payLog.setOutTradeNo(new IdWorker().nextId() + "");//订单号
            payLog.setCreateTime(new Date());//订单创建时间
            payLog.setTotalFee((long) (total_money*100));//总和金额 分
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");//交易状态 0  未支付

            //将订单号列表 转为String类型 用逗号分隔 去掉前后的中括号 一级逗号后面的空格
            String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号 列表  订单号 即 orderId

            payLog.setPayType("1");//支付类型 微信支付 1

            //给支付日志中添加信息  注入TbpayLogMapper
            payLogMapper.insert(payLog);
            //将支付日志信息 根据 用户名 放入缓存
            redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);

        }


        //3.订单添加成功后删除redis中该用户的购物车列表
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

		/*//测试redis中是否还有cartList
		List<Cart> List = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		for (Cart cart : cartList) {
			System.out.println(cart.getSellerName());
		}*/
    }

    /**
     * 根据用户 从redis中读取支付日志
     * @param userID
     * @return
     */
    @Override
    public TbPayLog searchPayLogFromRedis(String userID) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userID);
        return payLog;

    }


    /**
     * 根据订单号码 修改订单状态 交易流水号，
     * @param out_trade_no
     * @param transaction_id
     */
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
       //1. 修改支付之日中的交易流水号，支付状态，支付时间
        //1.1 查询支付日志
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("1");//支付状态 已支付
        payLog.setPayTime(new Date());//支付时间
        payLog.setTransactionId(transaction_id);//交易流水，支付后 微信返回的
        //修改支付日志信息
        payLogMapper.updateByPrimaryKey(payLog);

        //2. 修改订单信息中的 支付状态
        //2.1 获取订单列表
        String orderList = payLog.getOrderList();
        //将订单号中的逗号切割  转为数组
        String[] orderids = orderList.split(",");
        //2.2遍历  获取订单对象
        for (String orderid : orderids) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderid));
            //修改订单支付状态
            order.setStatus("2");
            //修改订单
            orderMapper.updateByPrimaryKey(order);
        }

        //3.清除缓存中的支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }



}
