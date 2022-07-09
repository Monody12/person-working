<%--
  Created by IntelliJ IDEA.
  User: monody
  Date: 2022/5/15
  Time: 13:48
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <!--禁用缓存-->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <title>文件列表</title>
    <style>
        tr {
            height: <c:choose><c:when test="${page_path.device.charAt(0)=='d'}">50</c:when><c:otherwise>100</c:otherwise></c:choose>px
        }

        td {
            text-align: center;
        }

        button{
            width: 100px;
            height: 80px;
            margin-left: 50px;
        }
    </style>
</head>
<jsp:useBean id="page_path" scope="request" class="com.example.netdisk.entity.vo.PagePath"/>
<jsp:useBean id="folder_list" scope="request" type="java.util.List"/>
<jsp:useBean id="file_list" scope="request" type="java.util.List"/>

<body>
    <h2><span id="username">${page_path.username}</span> 的个人文件</h2>
    <h3>文件夹路径：<span id="folderPath">${page_path.folderPath}</span></h3>

    <button type="button" onclick="upload()">上传文件</button>

    <p>
        <span style="background-color: #F0E68C">进入文件夹</span>
        <span style="background-color: #98FB98">下载文件</span>
        <span style="background-color: #87CEEB">预览文件</span>

    </p>

    <table border="1">
        <tr>
            <th>name</th>
            <th>type</th>
            <th>detail</th>
            <th>size</th>
            <th>modified date</th>
        </tr>

        <c:forEach var="folder" items="${folder_list}" varStatus="folder_status" step="1">
            <tr>
                    <%-- 文件夹名 | 进入该文件夹 --%>
                <td style="background-color: #F0E68C"
                    onclick="location.assign('./index?username=${page_path.username}&folderPath=${folder.path}${folder.name}/')">${folder.name}</td>
                <td>folder</td>
                <td>${folder.detail}</td>
                <td></td>
                <td>${folder.updateTime}</td>
            </tr>
        </c:forEach>


        <c:forEach var="file" items="${file_list}" varStatus="file_status" step="1">
            <tr>
                    <%-- 文件名 | 下载文件 --%>
                <td style="background-color: #98FB98"
                    onclick="location.assign('download/${file.id}?isDownload=true')">${file.name}</td>
                    <%-- 文件名称 | （可选）预览文件 --%>
                <td <c:if
                        test="${file.type.equals('document')||file.type.equals('photo')||file.type.equals('music')||file.type.equals('video')}">
                    style="background-color: #87CEEB" onclick="window.open('download/${file.id}?isDownload=false')"
                </c:if>>${file.type}</td>
                <td>${file.detail}</td>
                <td>${file.size} Bytes</td>
                <td>${file.updateTime}</td>
            </tr>
        </c:forEach>

    </table>

</body>
<script type="text/javascript">
    function upload() {
        var username = document.getElementById("username").innerText
        var folderPath = document.getElementById("folderPath").innerText
        localStorage.setItem("username",username)
        localStorage.setItem("folderPath",folderPath)
        window.open('upload.html','_blank','width=500px,height=500px')
    }
</script>

</html>
