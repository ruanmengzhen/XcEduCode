app.service("seckillGoodsService",function ($http) {

    //展示所有秒杀商品
    this.findList=function () {
        return $http.get("seckillGoods/findList.do");
    }
});