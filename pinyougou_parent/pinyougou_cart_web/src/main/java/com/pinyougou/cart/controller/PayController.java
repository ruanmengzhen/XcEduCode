package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WinxinPayService;

import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//支付的空值层
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WinxinPayService winxinPayService;
    @Reference
    private OrderService orderService;

    /**
     * 生成微信支付的二维码
     * @return
     */
    @RequestMapping("/creatNative")
    public Map creatNative(){
       /* //订单号和金额需要从日志文件中获取
        //前期测试暂时用死值固定代替
        long out_trade_no = new IdWorker().nextId();*/

       //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //调用orderService 中的查询日志的方法 获取 订单号 和订单支付金额
        TbPayLog payLog = orderService.searchPayLogFromRedis(username);

        //当能查询到支付日志时
        if (payLog!=null ){

            //调用微信支付的服务层 生成二维码的方法
            return winxinPayService.creatNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        }else {//查询不到时 返回空集合
            return new HashMap();
        }

    }

    /**
     * 根据订单号查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        //定义循环次数，
        int x=0;
        while (true){
            Map<String,String> map = winxinPayService.queryPayStatus(out_trade_no);
            if (map==null){
                result =new Result(false,"支付有误");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")){
                result = new Result(true,"支付成功");

                //当调用微信支付接口成功返回支付结果后 修改 订单状态 交易流水 等

                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));


                break;
            }


            try {
                //休眠
                //Thread.sleep(3000);

                //休眠3秒 之后重新执行
               TimeUnit.SECONDS.sleep(3);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退出循环，设置时间为5分钟
            x++;
            if (x>=4){//一分钟执行20次
                result= new Result(false,"二维码超时");
                break;
            }


        }



        return result;
    }
}
