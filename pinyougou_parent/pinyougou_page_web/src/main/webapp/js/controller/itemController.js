//商品详细页（控制层） 
app.controller('itemController', function ($scope) {
    //数量操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

//定义用户选择的规格
    $scope.secpificationItems={};

    //定义方法 用户选择规格
    $scope.selectSpecification=function (attributeName,attributeValue) {
        //获取用户选择的规格,即name对应的值
        $scope.secpificationItems[attributeName]=attributeValue;
        //根据用户选择的规格信息展示相应的页面
        searchSku();
    };

    //判断选项规格是否被选择
    $scope.isSelected=function (attributeName,attributeValue) {
        //判断页面选择的规格是否是name对应的值
        if ($scope.secpificationItems[attributeName]==attributeValue){
            return true;
        }else {
            return false;
        }
    }

    //加载SKU
    $scope.loadSku=function () {
        $scope.sku=skuList[0];
        //规格信息 就等于 sku中的规格信息，深克隆一个规格，深克隆需要将对象先转为字符串，再将字符串转为json对象
        $scope.secpificationItems=JSON.parse(JSON.stringify($scope.sku.spec))
    }

    /**
     * 根据用户选择的规格展示相应的商品信息，
     * 1.定义查询数据库中SKU信息的方法，查询到sku后将其与用户选择规格信息做比较，如果相同就将查询到的sku赋值给$scopr.sku,
     *2.由于在页面已经用freemarker获取到了数据库的sku信息，直接使用就可以
     *
     */
      //定义查询sku方法
    searchSku=function () {
        //遍历查询到的sku
        for (var i = 0; i < skuList.length; i++) {
            //判断查询到的sku的spec与页面的spec是否相同
            if (matchObject(skuList[i].spec,$scope.secpificationItems)) {//相同
                $scope.sku=skuList[i];
				return;
            }else {//不同
                $scope.sku={'id':0,'title':'-----','price':0}
            }
        }
    }

    //定义方法判断查询到的sku的spec与页面的spec是否相同
   //定义方法判断查询到的sku的spec与页面的spec是否相同
    matchObject=function (map1, map2) {
        if (map1.length == map2.length) {
            for (var k in map1) {
                if (map1[k] != map2[k]) {
                    return false;
                }
            }
        }
        return true;
    }
	
	 //将商品添加到购物车
    $scope.addCat=function () {
        alert('skuid:'+$scope.sku.id);
    }
}); 
