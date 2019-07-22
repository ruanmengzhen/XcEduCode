//控制层
app.controller('goodsController' ,function($scope,$controller ,$location  ,goodsService,itemCatService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        goodsService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //分页
    $scope.findPage=function(page,rows){
        goodsService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

   /* //查询实体
    $scope.findOne=function(id){
        goodsService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }*/


    $scope.entity={tbGoods:{},goodsDesc:{},itemList:[]};
//查询实体
    $scope.findOne=function(){
        //定义变量接受跳转页面携带的参数id
        var id=$location.search()["id"];//$location：是angularJs的内置服务用来传递页面跳转的参数
        if (id == null) {
            return;
        }


        goodsService.findOne(id).success(
            function(response){
                $scope.entity= response;
                //读取富文本编辑器的内容
                editor.html($scope.entity.goodsDesc.introduction);
                //读取图片列表
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                //读取扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //读取规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //读取SKU列表
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    };

    ////根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        //定义一个变量用来接收读取的规格
        var item=$scope.entity.goodsDesc.specificationItems;
        //定义变量接受 是否能根据key 获取到key的值
        var object=$scope.searchObjectByKey(item,"attributeName",specName);
        if (object!=null && object.attributeValue.indexOf(optionName)>=0){
            return true;
        } else {
            return false;
        }
    };


    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        goodsService.dele( $scope.selectIds ).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds=[];
                }
            }
        );
    }

    $scope.searchEntity={};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        goodsService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };


    $scope.itemCatList=[];//商品分类列表
    //查询商品分类列表
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );

    }
    // $scope.status=['未审核','已审核','审核未通过','已关闭'];
    //更新状态
    $scope.updateStatus=function(status){
        goodsService.updateStatus( $scope.selectIds ,status).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新页面
                    $scope.selectIds=[];
                }else{
                    alert(response.message);
                }
            }
        );
    }

});
