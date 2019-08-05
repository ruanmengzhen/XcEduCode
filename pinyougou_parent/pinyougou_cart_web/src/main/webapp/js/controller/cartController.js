app.controller("cartController", function ($scope, cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                //获取合计数
                $scope.totalValue = cartService.sum($scope.cartList);

            }
        )
    };


    //将商品添加到购物车
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {//添加成功，刷新列表，展示所有的购物车列表信息
                    $scope.findCartList();
                } else {//添加失败
                    return response.message;
                }
            }
        )
    };

    //获取用户地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;



                //设置默认地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    //当默认值为1时表示 默认
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }


            }
        )
    };

    //选择地址
    $scope.SelectAddress = function (address) {
        $scope.address = address;
    };

    //是否选择该地址
    $scope.isSelectedAddress = function (address) {
        if (address == $scope.address) {
            return true;
        } else {
            return false;
        }
    };

    //定义订单对象
    $scope.order = {paymentType: "1"};

    //定义方法 选择支付方式
    $scope.selectPayTyoe = function (type) {
        $scope.order.paymentType = type;
    };

    //保存订单
    $scope.saveOrder = function () {
        //将地址，电话，联系人 传给后端，设置给order
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;

        cartService.saveOrder($scope.order).success(
            function (response) {

                if (response.success){//添加成功，跳转到支付页面
                    //当支付方式为在线支付时，条状到支付页面，
                    if ($scope.order.paymentType=='1'){

                        location.href='pay.html';
                    } else {
                        //当支付方式选择货到付款时，跳转到成功页面
                        location.href = 'paysuccess.html';
                    }
                }else{
                    //失败给出提示信息
                    alert(response.message);
                }
            }
        )
    }

});