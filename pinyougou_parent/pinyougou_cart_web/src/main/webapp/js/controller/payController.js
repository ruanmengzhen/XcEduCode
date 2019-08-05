app.controller("payController",function ($scope,$location,payService) {
    //本地支付订单金额
    $scope.creatNative=function () {
        payService.creatNative().success(
            function (response) {
                $scope.money=(response.total_fee/100).toFixed(2);//获取订单金额
                $scope.out_trade_no=response.out_trade_no;//获取订单号
                //生成二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });

                //查询订单状态
                queryPayStatus($scope.out_trade_no)
            }
        )
    };


    //查询支付状态
    queryPayStatus=function(out_trade_no){
        payService.queryPayStatus(out_trade_no).success(
            function(response){
                if(response.success){
                    //页面的跳转携带参数，加 # 号
                    location.href="paysuccess.html#?money="+$scope.money;
                }else{
                    if (response.message == '二维码超时') {
                        //再次刷新支付信息
                        $scope.creatNative();
                    }else {
                         location.href="payfail.html";
                    }
                    
                    
                    

                }
            }
        );
    };

    //获取金额
    $scope.getMoney=function () {
       return $location.search()['money'];
    }





});