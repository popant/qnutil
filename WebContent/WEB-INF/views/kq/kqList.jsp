<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<head>
	<style scoped>
        #progressBar {
            width: 440px;
            height:30px;
            margin-bottom: 10px;
        }
        .demo-section {
            width: 800px;
            text-align: left;
        }
        .console {
            margin: 10px;
        }
    </style> 
	<meta charset="UTF-8">
	<%@include file="/views/include/pageHeader.jsp" %>
	<script type="text/javascript" src="<%=request.getContextPath()%>/script/func/console.js"></script>
	<script type="text/javascript">
		var progressWinObj;
		//页面载入完毕调用初始化方法
		$(document).ready(function(){
			$("#searchBtn").bind("click",function(){
				goSearch();
			});
			$("tr[name='over']").hover(fnOver, fnOut);
			var window = $("#window"),
					undo = $("#undo")
							.bind("click", function() {
								window.data("kendoWindow").open();
								undo.hide();
							});
			window.hide();
			var onClose = function() {
				undo.show();
			}

			if (!window.data("kendoWindow")) {
				window.kendoWindow({
					width: "600px",
					title: "About Alvar Aalto",
					actions: [
						"Pin",
						"Minimize",
						"Maximize",
						"Close"
					],
					close: onClose
				});
			}
		});
		function fnOver(){
			var td = $(this).find("td:eq(3)");
			var array = td.text().split(",");
			$.each(array, function(name,value){
				if(value != ""){
					$("#"+value).css("background-color","#FFCC00");
				}
			});
		}
		function fnOut(){
			var td = $(this).find("td:eq(3)");
			var array = td.text().split(",");
			$.each(array, function(name,value){
				if(value != ""){
					$("#"+value).removeAttr("style");
				}
			});
		}
		function goSearch(){
// 			var userName = $('#userName').val();
// 			var password = $('#password').val();
// 			var fromDate = $('#fromDate').val();
// 			var endDate = $('#endDate').val();
			var form = $('<form></form>');
			form.attr('action', "<%=request.getContextPath()%>/qnutil/kq/index");
			form.attr('method', 'post');
			form.attr('target', '_self');
			form.append($('#userName'));
			form.append($('#password'));
			form.append($('#fromDate'));
			form.append($('#endDate'));
			form.submit();
// 			window.location = "${pageContext.request.contextPath }/db/kq/index?userName="+userName+"&password="+password+"&fromDate="+fromDate+"&endDate="+endDate;
		}
		function initShowFilterBtn(){
			$(".showFilterBtn1").click(function(){
				var thisObj = this;
				if($(".filter").css("display")!='none'){
					$(".filter").slideUp("fast",function(){
						$(thisObj).find(".k-icon").addClass("k-si-plus").removeClass("k-si-minus");
						$(thisObj).find(".btnText").html('搜索');
					});
				}else{
					$(".filter").slideDown("fast",function(){
						$(thisObj).find(".k-icon").addClass("k-si-minus").removeClass("k-si-plus");
						$(thisObj).find(".btnText").html('搜索');
					});
				}
			});
			
		}
	</script>
</head>
<body class="indexBody">
	<div class="topSetting pageHeader">
		<div id="colorbox" style="z-index:999">
			<div class="color" id="color1"></div>
			<div class="color" id="color2"></div>
			<div class="color" id="color3"></div>
			<div class="color" id="color4"></div>
		</div>
	</div>
	<div id="content" class="container_1 k-content k-block k-shadow">
		<button class="showFilterBtn1 k-button"><span class="k-icon k-si-plus"></span><span class="btnText">搜索</span></button>
		<div style="clear:both"></div>
		<div id="searchDiv" class="filter  k-block k-shadow">
			<div class="formGroup"  >
					用户名:<input  id="userName" name="userName" value="${ userName}"/>
			</div>
			<div class="formGroup"  >
					密码:<input type="password"  id="password" name="password" value="${password }"/>
			</div>
			<div class="formGroup" >
					起始日期:<input  id="fromDate" name="fromDate" value="${fromDate }"/>
			</div>
			<div class="formGroup">
					结束日期:<input  id="endDate" name="endDate" value="${endDate }"/>
			</div>
			<div><span style="color: red">${errorMsg}</span></div>
		</div>
		<div class="searchBtnGroup float">
			<button id="searchBtn" class="searchBtn k-button"><span class="k-icon k-i-search"></span>搜索</button>
			<button id="resetBtn" class="resetBtn k-button"><span class="k-icon k-i-refresh"></span>清除</button>
			<span id="undo"  class="k-button">点击迎娶小惊喜</span>
		</div>
		<div id="window">
			<c:if test="${empty kqDocList}">
				你被骗了，啥都没有！！ 建议你查询后再惊喜。。。。
			</c:if>
			<c:forEach items="${kqDocList}" var="line">
				${line} <br/>
			</c:forEach>
		</div>
		<div style="margin:10px 5px;">
			<table >
				<tr>
					<td width="50%" valign="top">
						<table class="k-grid k-widget k-secondary" style="width: 100%;">
							<tr class="k-header">
								<td>日期</td>
								<td>上班时间</td>
								<td>下班时间</td>
							</tr>
							<c:forEach items="${kqDateMap}" var="entry" varStatus="status"> 
								<c:choose>
									<c:when test="${status.index % 2 == 1}">  
							            <tr class="k-alt" id="${entry.key}" name="mycolor">  
							        </c:when>   
							        <c:otherwise>  
							            <tr id="${entry.key}" name="mycolor">  
							        </c:otherwise> 
						        </c:choose> 
									<td>${entry.key }</td>
									<td><fmt:formatDate value="${entry.value.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td><fmt:formatDate value="${entry.value.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								</tr>
							</c:forEach>
						</table>
					</td>
					<td width="50%" valign="top">
						<table class="k-grid k-widget k-secondary" style="width: 100%;">
							<tr class="k-header">
								<td>类型</td>
								<td>次数</td>
								<td>累计时间</td>
								<td>日期列表</td>
							</tr>
							<c:forEach items="${kqResultMap}" var="entry" varStatus="status">
								<c:choose>
									<c:when test="${status.index % 2 == 1}">  
							            <tr class="k-alt" name="over">  
							        </c:when>   
							        <c:otherwise>  
							            <tr name="over">  
							        </c:otherwise> 
						        </c:choose> 
									<td>${entry.key }</td>
									<td>${entry.value.resultCount }</td>
									<td><fmt:formatNumber type="number" value="${entry.value.accumulationTime/3600000}" maxFractionDigits="2"/></td>
									<td>${entry.value.dateStr }</td>
								</tr>					 
							</c:forEach>  
						</table>
						<div align="left">
							ps:<br/>
							1、考勤时间09:00:59之前的属于正常，不计入轻微迟到<br/>
							2、09:00:59~09:10:59 属于轻微迟到<br/>
							3、09:10:59~10:00:59 属于迟到<br/>
							4、10:00:59 属于旷工<br/>
							5、17:00:00 之前离开的属于旷工<br/>
							6、17:00:00~18:00:00 属于早退<br/>
							7、20:30:00 之后走则开始累积加班时长，从18:00:00开始算起（暂定）<br/>
							8、节假日加班超过一个小时的，开始累积加班时长<br/>
							<del>9、请假、公出等 如果状态是已接受，则合并计算考勤时间。</del><br/>
							10、日常工作餐补标准：40元/日。周末工作餐补标准：30元/日+70补贴，远程：30元/次。法定节假日工作餐补标准：见值班申请表；
						</div>

					</td>
				</tr>
			</table>
		</div>
	</div>
	<script type="text/x-kendo-template" id="gridTBtemplate">
		<div class="toolbar" align="left">
    		<button id="button" class="k-button" onclick="goAdd()"><span class="k-icon k-i-plus"></span>上传文件</button>
    	</div>	
	</script>
</body>
</html>