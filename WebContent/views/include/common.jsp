<script language="javascript">
	var styleUrl = "<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.default.min.css";
	var iframeWindow;
	$(window).on('load',function(){
		setTopSetting();
		$(".menuBtn").kendoMenu();
		initAutoRereshStartup();
		initShowFilterBtn();
		changeStyle();
		setStyle();
		$("#resetBtn").click(function(){
			$(".filter").clearFormValue();
		});
		$(".btnArea").each(function(){
			$(this).showHideNextObj()
		});
	});
	
	$(window).resize(function(){
		setTopSetting();
	});






	//控制顶部栏右边的三个设置框在页面缩小到宽度小于750px时变为MENU下拉菜单
	function setTopSetting(){
		var width = $(".topSetting").width();
		if(width>750){
			$(".topSetting .menuBtn").hide();
			$(".topSetting .setIntervalTimeBox,.topSetting .automaticalRefreshStartupBox,.topSetting .campaignStatus").show();
		}else{
			$(".topSetting .setIntervalTimeBox,.topSetting .automaticalRefreshStartupBox,.topSetting .campaignStatus").hide();
			$(".topSetting .menuBtn").show();
		}
	}


	//初始化顶部栏中的 comboBox 组件
	function initAutoRereshStartup(){
		$("#automaticalRefreshStartupBoxSelect,#automaticalRefreshStartupBoxSelect1").kendoComboBox({
			dataTextField: "text",
			dataValueField: "value",
			dataSource: [
				{ text: "Yes", value: "1" },
				{ text: "No", value: "2" }
			],
			filter: "contains",
			index: 3
		});
	}


	//设置隐藏及显示 filter 按钮
	function initShowFilterBtn(){
		$(".showFilterBtn").click(function(){
			var thisObj = this;
			if($(".filter").css("display")!='none'){
				$(".filter").slideUp("fast",function(){
					$(thisObj).find(".k-icon").addClass("k-si-plus").removeClass("k-si-minus");
					$(thisObj).find(".btnText").html('<fmt:message key="BTN_SHOW_FILTER"/>');
				});
			}else{
				$(".filter").slideDown("fast",function(){
					$(thisObj).find(".k-icon").addClass("k-si-minus").removeClass("k-si-plus");
					$(thisObj).find(".btnText").html('<fmt:message key="BTN_HIDE_FILTER"/>');
				});
			}
		});
	}


	//点击顶部颜色按钮之后，切换整个页面风格，并将风格写入cookie
	function changeStyle(){
		$("#colorbox .color").eq(0).click(function(){
			$("#styleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.blueopal.min.css");
			$("#headStyleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.moonlight.min.css");
			document.cookie = "styleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.blueopal.min.css;path=/";
			document.cookie = "headStyleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.moonlight.min.css;path=/";
			iframeWindow = $("#centerContent").get(0).contentWindow;
			if(styleUrl){
				styleUrl = "<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.blueopal.min.css";
				setStyleUrlInLink();
				iframeWindow.setStyle(styleUrl);
			}
		});
		$("#colorbox .color").eq(1).click(function(){
			$("#styleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.bootstrap.min.css");
			$("#headStyleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.metroblack.min.css");
			document.cookie = "styleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.bootstrap.min.css;path=/";
			document.cookie = "headStyleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.metroblack.min.css;path=/";
			iframeWindow = $("#centerContent").get(0).contentWindow;
			if(styleUrl){
				styleUrl = "<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.bootstrap.min.css";
				setStyleUrlInLink();
				iframeWindow.setStyle(styleUrl);
			}
		});
		$("#colorbox .color").eq(2).click(function(){
			$("#styleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.silver.min.css");
			$("#headStyleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.metroblack.min.css");
			document.cookie = "styleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.silver.min.css;path=/";
			document.cookie = "headStyleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.metroblack.min.css;path=/";
			iframeWindow = $("#centerContent").get(0).contentWindow;
			if(styleUrl){
				styleUrl = "<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.silver.min.css";
				setStyleUrlInLink();
				iframeWindow.setStyle(styleUrl);
			}
		});
		$("#colorbox .color").eq(3).click(function(){
			$("#styleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.default.min.css");
			$("#headStyleSetter").attr("href","<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.default.min.css");
			document.cookie = "styleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.default.min.css;path=/";
			document.cookie = "headStyleUrl=<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.default.min.css;path=/";
			iframeWindow = $("#centerContent").get(0).contentWindow;
			if(styleUrl){
				styleUrl = "<%=request.getContextPath()%>/script/lib/kendoui/styles/kendo.default.min.css";
				setStyleUrlInLink();
				iframeWindow.setStyle(styleUrl);
			}
		});
	}


	//从cookie中读取风格信息，并设置页面风格
	/*function setStyle(){
		var strCookie = document.cookie;
		var cookieArr = strCookie.split(";");
		var cookieJson = {};
		for(var i=0;i<cookieArr.length;i++){
			var key = cookieArr[i].split("=")[0];
			var value = cookieArr[i].split("=")[1];
			cookieJson[key] = value;
		}
		if(cookieJson.styleUrl){
			$("#styleSetter").attr("href",cookieJson.styleUrl);
		}
	}*/
	
	function setStyle(urlParam){
		var strCookie = document.cookie;
		//alert(strCookie);
		var cookieArr = strCookie.split(";");
		var cookieJson = {};
		for(var i=0;i<cookieArr.length;i++){
			var key = cookieArr[i].split("=")[0].replace(/[ ]/g,"");
			var value = cookieArr[i].split("=")[1];
			cookieJson[key] = value;
		}
		var search = window.location.search;
		var searchStr = "";
		if(search.split("?")[1])
			searchStr = search.split("?")[1];
		//alert(searchStr);
		var searchArray = searchStr.split("&");
		var searchObj = {};
		for(var i=0;i<searchArray.length;i++){
			searchObj[searchArray[i].split("=")[0]] = searchArray[i].split("=")[1];
		}
		var url;
		if(cookieJson.styleUrl){
			url = cookieJson.styleUrl;
			if(styleUrl){
				styleUrl = cookieJson.styleUrl;
				setStyleUrlInLink();
				$("#centerContent").attr("src",$("#centerContent").attr("src")+"?styleUrl="+styleUrl);
			}
			headStyleUrl = cookieJson.headStyleUrl;
			$("#headStyleSetter").attr("href",headStyleUrl);
		}
		if(searchObj && searchObj.styleUrl)
			url = searchObj.styleUrl;
		if(urlParam)
			url = urlParam;
		//alert(url);
		$("#styleSetter").attr("href",url);
	}
	
	function setStyleUrlInLink(){
		$("a.k-link").each(function(){
			var href = $(this).attr("href");
			
			var hasParameter = (href.indexOf("?") > 0);
			href = href + (hasParameter ? "&" : "?") + "styleUrl=" + styleUrl;
			
			$(this).attr("href",href);
		});
	};
	
	
	//对指定的 Grid 使用该方法，Grid 将自动适应页面高度，并且在页面 resize 时，以及 filter 收起或展开时自动适应页面高度。
	/* $.fn.autoGridSize = function(customerOffset){
		var obj = $(this);
		setGridSize(obj,customerOffset);
		$(window).resize(function(){
			setGridSize(obj,customerOffset);
		});
		$(".filter").resize(function(){
			setGridSize(obj,customerOffset);
		});
		if($(".filter").length>0 && $(".showFilterBtn").length>0){
			var intervalID = null;
			$(".showFilterBtn").click(function(){
				intervalID = setInterval(function(){
					if($(".filter").css("display")!='none')
						setGridSize(obj,customerOffset);
				},40);
				setTimeout(function(){
					clearInterval(intervalID);
				},1000);
			});
		}
	}
	
	function setGridSize(obj,customerOffset){
		var OFFSET = 85;
		if(customerOffset || customerOffset==0)
			OFFSET = customerOffset;
		var filterHeight = 0;
		if($(".filter").height())
			filterHeight = $(".filter").height() + parseInt($(".filter").css("padding-top")) + parseInt($(".filter").css("padding-bottom")) + parseInt($(".filter").css("margin-bottom")) + parseInt($(".filter").css("margin-top"));
			//alert(parseInt($(".filter").css("padding-bottom")));
		var topSettingHeight = 0;
		if($(".topSetting").height())
			topSettingHeight = $(".topSetting").height();
		var height = document.documentElement.clientHeight - filterHeight - topSettingHeight - OFFSET;
		var offset = obj.height()-obj.find(".k-grid-content").height();
		obj.height(height);
		obj.find(".k-grid-content").height(height-offset);
		obj.find(".k-grid-content").css("min-height",(obj.css("min-height").split("px")[0] - offset)+"px");
	} */
	
	
	//对指定的按钮使用该方法，按钮将控制作为参数传入的元素的显示与隐藏
	$.fn.switchPanel = function(){
		var btn = this;
		$(btn).click(function(){
			if($(btn).find(".k-icon").attr("class").indexOf("k-si-minus")>=0){
				$(btn).find(".k-icon").addClass("k-si-plus").removeClass("k-si-minus");
			}else{
				$(btn).find(".k-icon").addClass("k-si-minus").removeClass("k-si-plus");
			}
		});
	};
	
	$.fn.showHideNextObj = function(show){
		var btn = this;
		var ifShow = true;
		if(show === false)
			ifShow = false;
		$(btn).click(function(){
			if(ifShow == true){
				$(btn).find(".k-icon").addClass("k-si-plus").removeClass("k-si-minus");
				$(btn).next().slideUp("fast",function(){
					ifShow = false;
				});
			}else{
				$(btn).find(".k-icon").addClass("k-si-minus").removeClass("k-si-plus");
				$(btn).next().slideDown("fast",function(){
					ifShow = true;
				});
			}
		});
	}
	
	$.fn.showNextObj = function(){
		var btn = this;
		var ifShow = $(btn).next().css("display");
		if(ifshow == "none"){
			$(btn).trigger("click");
		}
	};
	
	$.fn.colorOdd = function(){
		$(this).find("tr:odd").addClass("k-alt");
	};
</script>
