app.service("cartService", function ($http) {
    //查询购物车列表
    this.findCartList = function () {
        return $http.get("../cart/findCartList.do")
    };

    //购物车数量的增减功能，定义方法 添加商品到购物车
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get("../cart/addGoodsToCartList.do?itemId=" + itemId + "&num=" + num)
    };


    //定义方法进行求和,数据需要从购物车列表中获取
    this.sum = function (cartList) {
        //定义对象存储商品数量总计和价钱总计
        var totalValue = {totalNum: 0, totalMoney: 0.00};
        //遍历购物车列表获取商品数量和 价钱总计
        for (var i = 0; i < cartList.length; i++) {
            //获取购物车明细列表
            var orderItemList = cartList[i].orderItemList;
            //遍历购物车明细列表
            for (var j = 0; j < orderItemList.length; j++) {
                totalValue.totalNum += orderItemList[j].num;
                totalValue.totalMoney += orderItemList[j].totalFee;
                //alert("totalValue.totalNum: "+totalValue.totalNum)
                //alert("totalValue.totalMoney："+totalValue.totalMoney)
            }
        }
        return totalValue;
    };

    //获取用户地址列表
    this.findAddressList=function () {
        return $http.get("address/findAddressByUserId.do")
    };

    //添加订单信息
    this.addOrder=function () {
       return $http.get('order/add.do')
    };


    //保存订单
    this.saveOrder=function (order) {
        return $http.post('order/add.do',order);
    }
});