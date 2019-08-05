app.controller("seckillGoodsController",function ($scope, seckillGoodsService) {

    //展示所有秒杀商品
    $scope.findList=function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list=response;
            }
        )
    }
})