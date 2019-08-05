app.service("payService",function ($http) {

    //生成二维码 本地支付订单金额
    this.creatNative=function () {
        return $http.get("pay/creatNative.do");
    }

    //查询支付状态
    this.queryPayStatus=function(out_trade_no){
        return $http.get('pay/queryPayStatus.do?out_trade_no='+out_trade_no);
    }


});