app.controller("contentController",function ($scope,contentService) {
    $scope.findCategoryId=function (categoryId) {
        $scope.contentList=[];//定义广告集合
        contentService.findCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    }

    //定义搜索跳转的页面 给搜索页面传递参数
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});