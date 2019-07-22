app.service("searchSevice",function ($http) {

    //页面的搜索按钮
    this.search=function (searchMap) {
        return $http.post("ItemSearch/search.do",searchMap)
    }

})