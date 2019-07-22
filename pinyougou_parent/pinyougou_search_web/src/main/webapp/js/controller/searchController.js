app.controller("searchController",function ($scope, $location,searchSevice) {
//定义搜索对象
    $scope.searchMap={'keywords':'','brand':'','category':'','spec':{},'price':'','pageNum':'1','pageSize':'20',
    'sortField':'','sort':''};

    //搜索
    $scope.search=function () {
        //搜索之前将当前页和记录条数 转化为 int类型
        $scope.searchMap.pageNum=parseInt($scope.searchMap.pageNum);
        $scope.searchMap.pageSize=parseInt( $scope.searchMap.pageSize);
        searchSevice.search($scope.searchMap).success(
            function (response) {
                //返回搜索的结果
                $scope.resultMap=response;
                //调用分页标签的方法
                buildPageLabel();
            }
        )
    };


    //判断搜索的关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        //获取品牌列表
        for (var i = 0; i < $scope.searchMap.brandList.length; i++) {
            //获取品牌在搜索的关键字中的索引,获取的索引大于等于0，表示有品牌
           if ( $scope.searchMap.keywords.indexOf($scope.searchMap.brandList[i].text)>=0){
               return true;
           }else {
               return false;
           }
        }

    }

    //构建分页标签
    buildPageLabel=function(){
        //定义分页标签
        $scope.pageLabel=[];
        //定义首页。总页码，尾页,
        var firstPage=1;
        var totalPages=$scope.resultMap.totalPages;
        var lastPage=totalPages;

        //定义标量表示显示页码的前后是否还有页码
        $scope.firstDot=true;//表示前面有点
        $scope.lastDot=true;//表示后面有点

        //当总页码小于等于5时，显示所有页码 首页 尾页为定义好的值，
        // 总页码>5 就显示部分页码
       if (totalPages>5){
           // 当前页小于3时，显示前五页，
           if ($scope.searchMap.pageNum <= 3) {
               lastPage=5;
               $scope.firstDot=false;//显示前五页，前面无点，后面有
           }else if ($scope.searchMap.pageNum >= totalPages-2) {// 当前页大于等于总页码-2时，显示后5页
               firstPage=totalPages-4;
               $scope.lastDot=false;//显示后5页,前面有点，后面无点
           }else{//以当前页为中心显示5页 pageNum
               firstPage=$scope.searchMap.pageNum-2;
               lastPage=$scope.searchMap.pageNum+2;
               //前后都有点
           }
       }else {
           //当总页码小于5页时，前后都没有点
           $scope.firstDot =false;
           $scope.lastDot=false;
       }
        //循环产生页码，将循环得到的页码push到pageLabel中
        for (var i = firstPage; i <=lastPage ; i++) {
            $scope.pageLabel.push(i);
        }

    };

    //根据页码查询信息
    $scope.queryByPage=function(pageNum){
        //当 当前页<1 或者>总页码时，就return 不执行
        if (pageNum < 1 || pageNum > $scope.resultMap.totalPages) {
            return;
        }
        //根据当前页查询信息，调用搜索方法，当前页 = 页面传的参数
        $scope.searchMap.pageNum=pageNum;
        $scope.search();
    };

    //判断上一页，下一页是否可用
    $scope.isFirstPage=function(){
        //判断当前页是否是第一页
        if ($scope.searchMap.pageNum==1){
            return true;
        }else {
            return false;
        }
    };

    $scope.isEndPage=function(){
        //判断当前页是不是最后一页 即总页码
        if ($scope.searchMap.pageNum==$scope.resultMap.totalPages){
            return true;
        } else {
            return false;
        }
    }

//设置排序规则
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    //添加搜索选项
    $scope.addSearchItem=function (key, value) {
        //如果搜索的是品牌和商品分类， key是brand，category，值也是
        if (key == 'brand' || key == 'category' || key=='price'){
            $scope.searchMap[key]=value;//json对象的key的值 不固定 使用[]获取
        } else {
            //搜索的是规格
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }


    //移除搜索选项
    $scope.removeSearchItem=function (key) {
        //如果搜索的是品牌和商品分类， key是brand，category，值也是
        if (key == 'brand' || key == 'category'|| key=='price'){
            $scope.searchMap[key]='';//json对象的key的值 不固定 使用[]获取
        } else {
            //搜索的是规格，移除
           delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }


    //接受搜索的关键字,让页面一加载就运行这个方法
    $scope.loadKeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];//接收首页传递过来的关键字，将其赋值刚给搜索页面的搜索关键字
        //调用搜索方法
        $scope.search();

    }
})