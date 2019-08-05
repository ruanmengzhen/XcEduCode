<%--
  Created by IntelliJ IDEA.
  User: RMZ
  Date: 2019/7/22
  Time: 21:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
    <title>一品优购</title>
</head>
<body>

<h1>欢迎来到品优购一楼</h1>

<%=request.getRemoteUser()%>

<a href="http://localhost:9100/cas/logout?service=http://www.baidu.com">退出登录</a>
</body>
</html>
