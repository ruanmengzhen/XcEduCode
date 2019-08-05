app.service("loginService",function ($http) {

    //获取用户名展示到页面
    this.showName=function () {
        return $http.get("../login/name.do")
    }


});