$.fn.getFormValue = function(){
	var valueObj = {};
	$(this).find("input[type='text'],input[type='hidden'],input[type='password'],input[type='textarea'],textarea,input:not([type])").not(".k-input,.k-content.k-raw-content").each(function(){
		var name = $(this).attr("name");
		var value = $(this).val();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-editable-area>textarea.k-content.k-raw-content").each(function(){
		var editor = $(this).data("kendoEditor");
		var name = $(this).attr("name");
		var value = editor.value();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-combobox>input,.k-combobox>select").each(function(){
		var combobox = $(this).data("kendoComboBox");
		var name = $(this).attr("name");
		var value = combobox.value();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-dropdown>input,.k-dropdown>select").each(function(){
		if($(this).is(".k-formatting")){
		
		}else{
			var dropdown = $(this).data("kendoDropDownList");
			var name = $(this).attr("name");
			var value = dropdown.value();
			if(!valueObj[name])
				valueObj[name] = value;
			else{
				valueObj[name] = valueObj[name] + "," + value;
			}
		}
	});
	$(this).find(".k-numerictextbox>.k-numeric-wrap").each(function(){
		var numerictextbox = $(this).children("input").eq(1).data("kendoNumericTextBox");
		var name = $(this).children("input").eq(1).attr("name");
		var value = numerictextbox.value();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-datetimepicker>.k-picker-wrap>input").each(function(){
		var datetimepicker = $(this).data("kendoDateTimePicker");
		var name = $(this).attr("name");
		var value = $(this).val();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-datepicker>.k-picker-wrap>input").each(function(){
		var datepicker = $(this).data("kendoDatePicker");
		var name = $(this).attr("name");
		var value = $(this).val();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-autocomplete>.k-input").each(function(){
		var autoComplete = $(this).data("kendoAutoComplete");
		var name = $(this).attr("name");
		var value = $(this).val();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	$(this).find(".k-multiselect>select").each(function(){
		var multiselect = $(this).data("kendoMultiSelect");
		var name = $(this).attr("name");
		var value = multiselect.value();
		if(!valueObj[name])
			valueObj[name] = value;
		else{
			valueObj[name] = valueObj[name] + "," + value;
		}
	});
	return valueObj;
};

$.fn.setFormValue = function(nameValueObj,obj){
	var thisObject = this;
	for(var index in nameValueObj){
		var object;
		if(obj && obj.not){
			object = $(this).find('[name="'+index+'"]').not(obj.not);
		}else{
			object = $(this).find('[name="'+index+'"]');
		}
		object.each(function(){
			if($(this).is("input")){
				if($(this).data("role")=="dropdownlist"){
					$(this).data("kendoDropDownList").value(nameValueObj[index]);
				}else if($(this).data("role")=="combobox"){
					$(this).data("kendoComboBox").value(nameValueObj[index]);
				}else if($(this).data("role")=="multiselect"){
					$(this).data("kendoMultiSelect").value(nameValueObj[index]);
				}else if($(this).data("role")=="numerictextbox"){
					$(this).data("kendoNumericTextBox").value(nameValueObj[index]);
				}else if($(this).data("role")=="datepicker"){
					$(this).data("kendoDatePicker").value(nameValueObj[index]);
				}else if($(this).data("role")=="datetimepicker"){
					$(this).data("kendoDateTimePicker").value(nameValueObj[index]);
				}else if($(this).data("role")=="editor"){
					$(this).data("kendoEditor").value(nameValueObj[index]);
				}else if($(this).data("role")=="autocomplete"){
					$(this).data("kendoAutoComplete").value(nameValueObj[index]);
				}else{
					$(this).val(nameValueObj[index]);
				}
			}else if($(this).is("select")){
				if($(this).data("role")=="dropdownlist"){
					$(this).data("kendoDropDownList").value(nameValueObj[index]);
				}else if($(this).data("role")=="combobox"){
					$(this).data("kendoComboBox").value(nameValueObj[index]);
				}else if($(this).data("role")=="multiselect"){
					$(this).data("kendoMultiSelect").value(nameValueObj[index]);
				}else if($(this).data("role")=="numerictextbox"){
					$(this).data("kendoNumericTextBox").value(nameValueObj[index]);
				}else if($(this).data("role")=="datepicker"){
					$(this).data("kendoDatePicker").value(nameValueObj[index]);
				}else if($(this).data("role")=="datetimepicker"){
					$(this).data("kendoDateTimePicker").value(nameValueObj[index]);
				}else if($(this).data("role")=="editor"){
					$(this).data("kendoEditor").value(nameValueObj[index]);
				}else{
					$(this).val(nameValueObj[index]);
				}
			}else if($(this).is("textarea")){
				if($(this).data("role")=="editor"){
					$(this).data("kendoEditor").value(nameValueObj[index]);
				}else{
					$(this).val(nameValueObj[index]);
				}
			}
		});
	}
};

$.fn.clearFormValue = function(obj){
	var nameValueObj = $(this).getFormValue();
	for(var index in nameValueObj){
		nameValueObj[index] = "";
	}
	$(this).setFormValue(nameValueObj,obj);
	var object;
	if(obj && obj.not){
		object = $(this).find("input,select,textarea").not("[name],"+obj.not);
	}else{
		object = $(this).find("input,select,textarea");
	}
	object.each(function(){
		if($(this).is("input")){
				if($(this).data("role")=="dropdownlist"){
					$(this).data("kendoDropDownList").value("");
				}else if($(this).data("role")=="combobox"){
					$(this).data("kendoComboBox").value("");
				}else if($(this).data("role")=="multiselect"){
					$(this).data("kendoMultiSelect").value("");
				}else if($(this).data("role")=="numerictextbox"){
					$(this).data("kendoNumericTextBox").value("");
				}else if($(this).data("role")=="datepicker"){
					$(this).data("kendoDatePicker").value("");
				}else if($(this).data("role")=="datetimepicker"){
					$(this).data("kendoDateTimePicker").value("");
				}else if($(this).data("role")=="autocomplete"){
					$(this).data("kendoAutoComplete").value("");
				}else{
					var type = $(this).attr("type");
					if(type != "radio" && type != "checkbox"){
						$(this).val("");
					}
				}
			}else if($(this).is("select")){
				if($(this).data("role")=="dropdownlist"){
					$(this).data("kendoDropDownList").value("");
				}else if($(this).data("role")=="combobox"){
					$(this).data("kendoComboBox").value("");
				}else if($(this).data("role")=="multiselect"){
					$(this).data("kendoMultiSelect").value("");
				}else if($(this).data("role")=="numerictextbox"){
					$(this).data("kendoNumericTextBox").value("");
				}else if($(this).data("role")=="datepicker"){
					$(this).data("kendoDatePicker").value("");
				}else if($(this).data("role")=="datetimepicker"){
					$(this).data("kendoDateTimePicker").value("");
				}else{
					$(this).val("");
				}
			}else if($(this).is("textarea")){
				if($(this).data("role")=="editor"){
					$(this).data("kendoEditor").value("");
				}else{
					$(this).val("");
				}
			}
	});
};
/*=====================================================================================
 * easy-bootstrap-errorMsgPanel v2.0
 * 
 * @author:niyq
 * @date:2013/08/22
 * @dependce:jquery
 *=====================================================================================*/
 !function($){
	
	"use strict";

	var ErrorMsgPanel = function(element,dataOptions){
		var thisObject = this;
		this.$element = $(element);
		this.dataOptions = $.extend({}, $.fn.errorMsgPanel.defaults, dataOptions);
		this.msg = this.dataOptions.msg;
		if(this.dataOptions && (this.dataOptions.autoClose || this.dataOptions.autoClose==false))
			this.autoClose = this.dataOptions.autoClose;
		else
			this.autoClose = $.fn.errorMsgPanel.defaults.autoClose;
		if(this.dataOptions && this.dataOptions.autoCloseDelay)
			this.autoCloseDelay = this.dataOptions.autoCloseDelay;
		else
			this.autoCloseDelay = $.fn.errorMsgPanel.defaults.autoCloseDelay;
		if(this.dataOptions && this.dataOptions.iconType)
			this.iconType = this.dataOptions.iconType;
		else
			this.iconType = null;
		this.init();
	};
	
	ErrorMsgPanel.prototype.init = function(){
		var thisObject = this;
		if(this.iconType == "success")
			this.$element.html('<span class="msgText"><span class="successIcon">√</span>'+thisObject.msg+'</span>');
		else
			this.$element.html('<span class="msgText"><span class="errorIcon">!</span>'+thisObject.msg+'</span>');
		this.$element.prepend($("<div class='topBar'></div>").append($('<span class="closePanelBtn">x</span>').click(function(){
			thisObject.$element.remove();
		})));
		this.setWidth(document.documentElement.clientWidth);
		$(window).resize(function(){
			thisObject.setWidth(document.documentElement.clientWidth);
		});
		this.offset = 0 - this.$element.height() - 60;
		this.$element.css('bottom',thisObject.offset+"px");
		this.show();
	};
	
	ErrorMsgPanel.prototype.show = function(){
		var thisObject = this;
		this.$element.animate({bottom:0},500,"",function(){
			if(thisObject.autoClose == true){
				thisObject.timeoutID = setTimeout(function(){
					thisObject.hide();
				},thisObject.autoCloseDelay);
			}
		});
	};
	
	ErrorMsgPanel.prototype.hide = function(){
		var thisObject = this;
		this.offset = 0 - this.$element.height() - 60;
		this.$element.animate({bottom:thisObject.offset+"px"},500,"",function(){
			thisObject.remove();
		});
	}
	
	ErrorMsgPanel.prototype.remove = function(){
		this.$element.remove();
	};
	
	ErrorMsgPanel.prototype.setWidth = function(widthParam){
		this.$element.width(widthParam);
	};
	
	ErrorMsgPanel.prototype.setHeight = function(heightParam){
		this.$element.height(heightParam);
	};
	
	$.errorMsgPanel = function(msg,autoClose,autoCloseDelay,iconType){
		var obj;
		$("body").append(obj = $('<div class="errorMsgPanel"></div>'));
		var option = {msg:msg};
		if(autoClose || autoClose==false)
			option.autoClose = autoClose;
		if(autoCloseDelay)
			option.autoCloseDelay = autoCloseDelay;
		if(iconType)
			option.iconType = iconType;
		obj.errorMsgPanel(option);
	};
	
	$.successMsgPanel = function(msg,autoClose,autoCloseDelay){
		$.errorMsgPanel(msg,autoClose,autoCloseDelay,"success");
	};
	
	$.fn.errorMsgPanel = function (option,param) {
		var result = null;
	    var thisObj = this.each(function () {
	    	var $this = $(this)
	        	, thisObject = $this.data('errorMsgPanel')
	        	, dataOptions = typeof option == 'object' && option;
	    	if(typeof option == 'string' ){
	    		result = thisObject[option](param);
	    	}else{
				if(!thisObject){
					$this.data('errorMsgPanel', (thisObject = new ErrorMsgPanel(this, dataOptions)));
				}else{
					$this.errorMsgPanel('reInit',dataOptions);
				}
	    	}
	    });
	    if(typeof option == 'string' )return result;
	    return thisObj;
	};
	
	$.fn.errorMsgPanel.defaults = {
		autoClose:true,
		autoCloseDelay:5000
	};
	
}(window.jQuery);

/*=====================================================================================
 * easy-bootstrap-fixedToolBar v2.0
 * 
 * @author:niyq
 * @date:2013/08/22
 * @dependce:jquery
 *=====================================================================================*/
 !function($){
	
	"use strict";

	var FixedToolBar = function(element,dataOptions){
		var thisObject = this;
		this.$element = $(element);
		this.dataOptions = $.extend({}, $.fn.fixedToolBar.defaults, dataOptions);
		this.buttons = this.dataOptions.buttons;
		this.timeoutID = null;
		this.ifShow = false;
		this.init();
	};
	
	FixedToolBar.prototype.init = function(){
		var thisObject = this;
		this.$element.append($('<div class="panelCtrlGroup"></div>'));
		this.$element.append($('<div class="objContainer"></div>'));
		this.panelCtrlGroupObj = this.$element.children(".panelCtrlGroup");                                           //panelCtrlGroupObj
		this.objContainerObj = this.$element.children(".objContainer");                                           //objContainerObj
		this.panelCtrlGroupObj.append($('<div class="panelCtrlBtn"></div>'));
		this.panelCtrlBtnObj = this.panelCtrlGroupObj.find(".panelCtrlBtn");                                          //panelCtrlBtnObj
		this.panelCtrlBtnObj.append($('<span></span>'));
		this.insertBtn();
		this.setPosition();
		this.setAction();
		$(window).resize(function(){
			thisObject.setPosition();
		});
	};
	
	FixedToolBar.prototype.setAction = function(){
		var thisObject = this;
		this.$element.mouseover(function(){
			clearTimeout(thisObject.timeoutID);
			thisObject.show();
		});
		this.panelCtrlBtnObj.mouseover(function(){
			clearTimeout(thisObject.timeoutID);
			thisObject.show();
		});
		this.$element.mouseout(function(){
			thisObject.timeoutID = setTimeout(function(){
				thisObject.hide();
			},300);
		});
	};
	
	FixedToolBar.prototype.show = function(){
		var thisObject = this;
		if(this.ifShow == false){
			this.$element.animate({right:"-2px"},200,"",function(){
				thisObject.ifShow = true;
				thisObject.$element.find(".panelCtrlBtn").css({marginRight:"1px"});
				thisObject.panelCtrlBtnObj.css({paddingLeft:'9px'});
			});
		}
	};
	
	FixedToolBar.prototype.hide = function(){
		var thisObject = this;
		var offset = 0 - this.$element.width() - 20;
		if(this.ifShow == true){
			this.$element.animate({right:offset},200,"",function(){
				thisObject.ifShow = false;
				thisObject.$element.find(".panelCtrlBtn").css({marginRight:"2px"});
				thisObject.panelCtrlBtnObj.css({paddingLeft:'8px'});
			});
		}
	};
	
	FixedToolBar.prototype.insertBtn = function(){
		var thisObject = this;
		for(var i=0;i<this.buttons.length;i++){
			var type = this.buttons[i];
			switch(type){
				case "toTop":
					this.toTop();
				break;
			}
		}
		if(this.objContainerObj.children("div").length<4){
			this.$element.css({maxWidth:'53px'});
		}else{
			this.$element.css({maxWidth:'105px'});
		}
	};
	
	FixedToolBar.prototype.toTop = function(){
		this.objContainerObj.append($('<div class="toTopBtn"></div>').click(function(){
			scroll(0,0);
		}));
	};
	
	FixedToolBar.prototype.setPosition = function(){
		var thisObject = this;
		var windowHeight = document.documentElement.clientHeight;
		var thisHeight = this.$element.height();
		var top = (windowHeight-thisHeight)/2-10;
		var right = 0-(this.$element.width()+20);
		this.$element.css({right:right,top:top});
		var btnTop = (this.$element.height()+16)/2 - this.panelCtrlBtnObj.height()/2 - 10;
		this.panelCtrlBtnObj.css({top:btnTop});
	}
	
	$.fixedToolBar = function(msg,autoClose,autoCloseDelay){
		var obj;
		$("body").append(obj = $('<div class="fixedToolBar"></div>'));
		var option = {};
		obj.fixedToolBar(option);
	};
	
	$.fn.fixedToolBar = function (option,param) {
		var result = null;
	    var thisObj = this.each(function () {
	    	var $this = $(this)
	        	, thisObject = $this.data('fixedToolBar')
	        	, dataOptions = typeof option == 'object' && option;
	    	if(typeof option == 'string' ){
	    		result = thisObject[option](param);
	    	}else{
				if(!thisObject){
					$this.data('fixedToolBar', (thisObject = new FixedToolBar(this, dataOptions)));
				}else{
					$this.fixedToolBar('reInit',dataOptions);
				}
	    	}
	    });
	    if(typeof option == 'string' )return result;
	    return thisObj;
	};
	
	$.fn.fixedToolBar.defaults = {
		buttons:['toTop']
	};
}(window.jQuery);
/*=====================================================================================
 * easy-bootstrap-clearValidSpan v2.0
 * 
 * @author:niyq
 * @date:2013/08/22
 * @dependce:jquery
 *=====================================================================================*/
$.fn.clearValidInfo = function(){
	$(this).find(".k-tooltip-validation").hide();
};

$.clearValidInfo = function(){
	$(".k-tooltip-validation").hide();
};
/*=====================================================================================
 * jquery - rebind
 * 
 * @author:niyq
 * @date:2013/08/22
 * @dependce:jquery
 *=====================================================================================*/
 $.fn.rebind = function(str,func){
	$(this).unbind(str);
	$(this).bind(str,func);
 };
 /*=====================================================================================
  * easy-bootstrap-autoCompleteAt v2.0
  * 
  * Atauthor:niyq
  * Atdate:2013/08/22
  * Atdependce:jquery
  *=====================================================================================*/
 
 Array.prototype.in_array = function(e) {  
	 for(i=0;i<this.length;i++) {  
		 if(this[i] == e)  
		  return true;  
	 }  
	 return false;  
 }  
 
  !function($){
 	
 	"use strict";

 	var AutoCompleteAt = function(element,dataOptions){
 		var thisObject = this;
 		this.$element = $(element);
 		this.dataOptions = $.extend({}, $.fn.autoCompleteAt.defaults, dataOptions);
 		this.keypressListener = false;
 		this.keydownListener = false;
 		this.boxShow = false;
 		this.role = this.$element.data("role");
 		if(this.role == 'editor'){
 			this.initInEditor();
 		}else{
 			this.init();
 		}
 	};
 	
 	AutoCompleteAt.prototype.hideAutoCompleteAtBoxForEditor = function(){
 		var that = this;
 		that.autoCompleteAtBox.hide();
 		$(that.editor).unbind("keydown");
 		if(that.bindFunc)
 			$(that.editor).keydown(that.bindFunc);
 		$(that.editor).keydown(function(e){
 			var key = e.which||e.keyCode;
 			if(key == 16){
 				that.keypressListener = true;
 			}
 		});
 	};
 	
 	AutoCompleteAt.prototype.showAutoCompleteAtBoxForEditor = function(){
 		var that = this;
 		that.autoCompleteAtBox.css("max-height",that.autoCompleteAtBoxMaxHeight);
 		var top = that.$element.prev().offset().top + $(that.win.document.getElementById("insertAt")).offset().top - $(that.win).scrollTop() + $(that.win.document.getElementById("insertAt")).height();
 		if(top - $(window).scrollTop() + that.autoCompleteAtBox.height()<document.documentElement.clientHeight){
 			that.autoCompleteAtBox.slideDown('fast');
 		}else if(top - $(window).scrollTop() >= that.autoCompleteAtBox.height()){
 			top = top - $(that.win.document.getElementById("insertAt")).height();
 			that.autoCompleteAtBox.css({top:top});
 			var endTop = top - that.autoCompleteAtBox.height();
 			var height = that.autoCompleteAtBox.height();
 			that.autoCompleteAtBox.height(1);
 			that.autoCompleteAtBox.show();
 			that.autoCompleteAtBox.animate({top:endTop,height:height},'fast');
 		}else{
 			var maxHeight = document.documentElement.clientHeight - (top - $(window).scrollTop()) - 12;
 			that.autoCompleteAtBox.css("max-height",maxHeight);
 			that.autoCompleteAtBox.slideDown('fast');
 		}
 	};
 	
 	AutoCompleteAt.prototype.initInEditor = function(){
 		var that = this;
 		that.win = that.$element.prev().get(0).contentWindow;
 		that.editor = that.win.document.getElementsByTagName("body")[0];
 		var dataSource = this.dataOptions.dataSource;
 		that.$element.closest("table.k-editor").after(that.autoCompleteAtBox = $('<div class="autoCompleteAtBox k-block k-shadow" style="display:none;"></div>'));
 		$(that.editor).click(function(){
 			$(that.win.document.getElementById("insertAt")).before("@");
			$(that.win.document.getElementById("insertAt")).remove();
 			that.hideAutoCompleteAtBoxForEditor();
 		});
 		for(var i = 0;i<dataSource.length;i++){
 			that.autoCompleteAtBox.append($('<span class="autoCompleteAtOption">'+dataSource[i]+'</span>').mouseover(function(){
 				that.autoCompleteAtBox.find(".k-state-hover").removeClass("k-state-hover");
 				$(this).addClass("k-state-hover");
 			}).click(function(){
 				var value = $(this).html();
 				var str = "[@"+value+"]";
 				$(that.win.document.getElementById("insertAt")).before(str);
 				$(that.win.document.getElementById("insertAt")).remove();
 				that.editor.focus();
 				that.hideAutoCompleteAtBoxForEditor();
 			}));
 		}
 		that.autoCompleteAtBoxMaxHeight = that.autoCompleteAtBox.css("max-height");
 		$(window).resize(function(){
 			that.setBoxPositionForEditor();
 		});
 		$(that.editor).keypress(function(e){
 			var key = e.which||e.keyCode;
 			if(that.keypressListener == true){
 				if(key == 64){
 					that.bindFunc = $._data($(that.editor)[0], 'events').keydown[0].handler;
 					$(that.editor).unbind("keydown");
 					$(that.editor).keydown(function(e){
 						var key = e.which||e.keyCode;
 						if(key == 16){
 							that.keypressListener = true;
 						}
 						if(that.autoCompleteAtBox.css("display") != 'none'){
 							if(key == 38){
 								var obj = that.autoCompleteAtBox.find(".autoCompleteAtOption.k-state-hover");
 								if(obj.length <= 0){
 									that.autoCompleteAtBox.find(".autoCompleteAtOption").eq(that.autoCompleteAtBox.find(".autoCompleteAtOption").length-1).trigger('mouseover');
 								}else if(obj.prev().length>0){
 									obj.prev().trigger("mouseover");
 								}
 								return false;
 							}else if(key == 40){
 								var obj = that.autoCompleteAtBox.find(".autoCompleteAtOption.k-state-hover");
 								if(obj.length <= 0){
 									that.autoCompleteAtBox.find(".autoCompleteAtOption").eq(0).trigger('mouseover');
 								}else if(obj.next().length>0){
 									obj.next().trigger("mouseover");
 								}
 								return false;
 							}else if(key == 13){
 								that.autoCompleteAtBox.find(".autoCompleteAtOption.k-state-hover").trigger("click");
 								that.hideAutoCompleteAtBoxForEditor();
 								return false;
 							}else{
 								$(that.win.document.getElementById("insertAt")).before("@").remove();
 								that.hideAutoCompleteAtBoxForEditor();
 							}
 						}else{
 							if(that.win.document.getElementById("insertAt")){
 								var node = that.win.document.getElementById("insertAt");
 								$(node).before("@");
 								node.parentNode.removeChild(node);
 							}
 						}
 					});
 					if(that.win.document.getElementById("insertAt")){
 						var node = that.win.document.getElementById("insertAt");
 						$(node).before("@");
 						node.parentNode.removeChild(node);
 					}
 					that.insertHtmlAtCursor('<span id="insertAt">@</span>');
 					that.setBoxPositionForEditor();
 					that.showAutoCompleteAtBoxForEditor();
 					that.autoCompleteAtBox.find(".autoCompleteAtOption").eq(0).trigger("mouseover");
 					return false;
 				}
 			}
 		}).keydown(function(e){
 			var key = e.which||e.keyCode;
 			if(key == 16){
 				that.keypressListener = true;
 			}
 		}).keyup(function(e){
 			var key = e.which||e.keyCode;
 			if(key == 16){
 				that.keypressListener = false;
 			}
 		});
 	};
 	
 	AutoCompleteAt.prototype.setBoxPositionForEditor = function(){
 		var that = this;
 		try{
 			var left = that.$element.prev().offset().left + $(that.win.document.getElementById("insertAt")).offset().left +  $(that.win.document.getElementById("insertAt")).width();
 	 		if(left + that.autoCompleteAtBox.width() >= document.documentElement.clientWidth)
 	 			left = left - that.autoCompleteAtBox.width();
 	 		var top = that.$element.prev().offset().top + $(that.win.document.getElementById("insertAt")).offset().top - $(that.win).scrollTop() + $(that.win.document.getElementById("insertAt")).height();
 	 		that.autoCompleteAtBox.css({top:top,left:left});
 		}catch(e){
 			
 		}
 	};
 	
 	AutoCompleteAt.prototype.insertHtmlAtCursor = function(html){
 		var that = this;
 		var range, node;
 		if (that.win.getSelection && that.win.getSelection().getRangeAt) {
 			var selection = that.win.getSelection();
 			range = selection.getRangeAt(0);
 			node = range.createContextualFragment(html);
 			range.insertNode(node);
 			var tmp = that.win.document.getElementById("insertAt").firstChild;
 			range.setStart(tmp, 1);
 			range.setEnd(tmp, 1);
 			selection.removeAllRanges();
 			selection.addRange(range);
 		} else if (that.win.document.selection && that.win.document.selection.createRange) {
 			var range = that.win.document.selection.createRange();
 			range.pasteHTML(html);
 			range.moveToElementText(that.win.document.getElementById("insertAt"));
 			range.moveStart("character");
 			range.select();
 		}
 	};
 	
 	AutoCompleteAt.prototype.insertNodeAtCursor = function(node){
 		var range, html;
 		if (that.win.getSelection && that.win.getSelection().getRangeAt) {
 			range = that.win.getSelection().getRangeAt(0);
 			range.insertNode(node);
 		} else if (that.win.document.selection && that.win.document.selection.createRange) {
 			range = that.win.document.selection.createRange();
 			html = (node.nodeType == 3) ? node.data : node.outerHTML;
 			range.pasteHTML(html);
 		}
 	}
 	
 	AutoCompleteAt.prototype.hideAutoCompleteAtBox = function(){
 		var that = this;
 		that.autoCompleteAtBox.hide();
 		that.boxShow = false;
 	};
 	
 	AutoCompleteAt.prototype.init = function(){
 		var that = this;
 		that.$element.addClass("autoCompleteAt");
 		that.$element.wrap($('<div class="autoCompleteAtContainer"></div>'));
 		var dataSource = this.dataOptions.dataSource;
 		that.$element.parent().append(that.autoCompleteAtBox = $('<div class="autoCompleteAtBox k-block k-shadow" style="display:none;"></div>'));
 		for(var i = 0;i<dataSource.length;i++){
 			that.autoCompleteAtBox.append($('<span class="autoCompleteAtOption">'+dataSource[i]+'</span>').mouseover(function(){
 				that.autoCompleteAtBox.find(".k-state-hover").removeClass("k-state-hover");
 				$(this).addClass("k-state-hover");
 			}).click(function(){
 				var value = $(this).html();
 				var str = "[@"+value+"]";
 				var textIndex = that.$element.get(0).selectionStart;
 				var textOld = that.$element.val();
 				var textNew = textOld.substring(0,textIndex - 1) + str + textOld.substring(textIndex,textOld.length);
 				that.$element.val(textNew);
 				that.hideAutoCompleteAtBox();
 				that.$element.get(0).focus();
 				var textIndexEnd = textIndex + str.length - 1;
 				if(navigator.userAgent.indexOf("MSIE") > -1){
 					var range = document.selection.createRange();
 					var textRange = that.$element.get(0).createTextRange();
 					textRange.moveStart('character',textIndex - 1);
 					if(that.dataOptions.selectInsert == true)
 						textRange.moveEnd('character',textIndexEnd);
 					textRange.collapse();
 					textRange.select();
 				}else{
 					if(that.dataOptions.selectInsert == true)
 						that.$element.get(0).setSelectionRange(textIndex - 1,textIndexEnd);
 					else
 						that.$element.get(0).setSelectionRange(textIndexEnd,textIndexEnd);
 				}
 			}));
 		}
 		that.$element.click(function(){
 			that.hideAutoCompleteAtBox();
 		});
 		that.$element.after(that.textareaCopy = $('<div class="textareaCopy"><span class="textareaCursor">|</span></div>'));
 		that.textareaCopy.css("line-height",that.$element.css("line-height"));
 		$(window).on("load",function(){
 			that.setTextareaCopyPosition();
 		});
 		that.$element.keydown(function(e){
 			that.keyDownEvent = e;
 		});
 		that.$element.keydown(function(e){
 			var key = e.which||e.keyCode;
 			if(key == 16){
 				that.keypressListener = true;
 			}
 			if(that.boxShow == true){
 				if(key == 38){
 					var obj = that.autoCompleteAtBox.find(".k-state-hover").prev();
 					if(obj.length > 0){
 						obj.trigger("mouseover");
 					}else if(that.autoCompleteAtBox.find(".k-state-hover").length <= 0){
 						that.autoCompleteAtBox.find(".autoCompleteAtOption").eq(that.autoCompleteAtBox.find(".autoCompleteAtOption").length-1).trigger("mouseover");
 					}
 					return false;
 				}else if(key == 40){
 					var obj = that.autoCompleteAtBox.find(".k-state-hover").next();
 					if(obj.length > 0){
 						obj.trigger("mouseover");
 					}else if(that.autoCompleteAtBox.find(".k-state-hover").length <= 0){
 						that.autoCompleteAtBox.find(".autoCompleteAtOption").eq(0).trigger("mouseover");
 					}
 					return false;
 				}else if(key == 13){
 					that.autoCompleteAtBox.find(".k-state-hover").trigger("click");
 					return false;
 				}else{
 					that.hideAutoCompleteAtBox();
 				}
 			}
 		}).keyup(function(e){
 			var key = e.which||e.keyCode;
 			if(key == 16){
 				that.keypressListener = false;
 			}
 		}).keypress(function(e){
 			if(that.keypressListener == true){
 				var key = e.which||e.keyCode;
 				if(key == 64){
 					var textIndex;
 					if(navigator.userAgent.indexOf("MSIE") > -1) { // IE
 						that.$element.get(0).focus();
 						var range = document.selection.createRange();
 						var range2 = range.duplicate();
 						range2.moveToElementText(that.$element.get(0));
 						textIndex = -1;
 						while(range2.inRange(range)){
 							range2.moveStart('character');
 							textIndex++;
 						}
 					} else {
 						textIndex = that.$element.get(0).selectionStart;
 					}
 					var string = that.$element.val().substring(0,textIndex).replace(/\n/g,"</br>");
 					that.textareaCopy.html(string+"@<span class='textareaCursor'>|</span>");
 					that.setBoxPosition();
 					that.showAutoCompleteAtBox();
 					that.autoCompleteAtBox.find(".autoCompleteAtOption").eq(0).trigger("mouseover");
 				}
 			}
 		});
 	};
 	
 	AutoCompleteAt.prototype.showAutoCompleteAtBox = function(){
 		var that = this;
 		var top = that.$element.parent().find(".textareaCursor").offset().top - that.$element.parent().offset().top - that.$element.scrollTop() + 16;
 		if(document.documentElement.clientHeight - (top + that.$element.parent().offset().top - $(window).scrollTop()) > that.autoCompleteAtBox.height()){
 			that.autoCompleteAtBox.slideDown('fast',function(){
 				that.boxShow = true;
 			});
 		}else if( top + that.$element.parent().offset().top - $(window).scrollTop() > that.autoCompleteAtBox.height()){
 			var top = top - 16;
 			that.autoCompleteAtBox.css("top",top);
 			var height = that.autoCompleteAtBox.height() + parseInt(that.autoCompleteAtBox.css("padding-top")) + parseInt(that.autoCompleteAtBox.css("padding-bottom"));
 			var endTop = top - height;
 			that.autoCompleteAtBox.height(1);
 			that.autoCompleteAtBox.show();
 			that.autoCompleteAtBox.animate({top:endTop,height:height},'fast',"",function(){
 				that.boxShow = true;
 			});
 		}else{
 			that.autoCompleteAtBox.slideDown('fast',function(){
 				that.boxShow = true;
 			});
 		}
 	};
 	
 	AutoCompleteAt.prototype.setTextareaCopyPosition = function(){
 		var that = this;
 		var OFFSET = 1;
 		if(navigator.userAgent.toLowerCase().indexOf("firefox")>-1)
 			OFFSET = 2;
 		if(navigator.userAgent.toLowerCase().indexOf("chrome")>-1)
 			OFFSET = 7;
		if(navigator.userAgent.toLowerCase().indexOf("msie")>-1)
			OFFSET = 7;
 		var that = this;
 		var height = that.$element.height();
 		var width = that.$element.get(0).scrollWidth-OFFSET;
 		that.textareaCopy.css({position:'absolute',left:0,top:1,height:height,width:width});
 	};
 	
 	AutoCompleteAt.prototype.setBoxPosition = function(){
 		var that = this;
 		var top = that.$element.parent().find(".textareaCursor").offset().top - that.$element.parent().offset().top - that.$element.scrollTop() + 16;
 		var left = that.$element.parent().find(".textareaCursor").offset().left - that.$element.parent().offset().left;
 		if(left<=5){
 			left = that.$element.width()+5;
 			top = top - parseInt(that.$element.css("line-height"));
 		}
 		if(that.$element.parent().find(".textareaCursor").offset().left + that.autoCompleteAtBox.width() >= document.documentElement.clientWidth)
 			left = left - that.autoCompleteAtBox.width();
 		that.autoCompleteAtBox.css({top:top,left:left});
 	};
 	
 	$.fn.autoCompleteAt = function (option,param) {
 		var result = null;
 	    var thisObj = this.each(function () {
 	    	var $this = $(this)
 	        	, thisObject = $this.data('autoCompleteAt')
 	        	, dataOptions = typeof option == 'object' && option;
 	    	if(typeof option == 'string' ){
 	    		result = thisObject[option](param);
 	    	}else{
 				if(!thisObject){
 					$this.data('autoCompleteAt', (thisObject = new AutoCompleteAt(this, dataOptions)));
 				}else{
 					$this.autoCompleteAt('reInit',dataOptions);
 				}
 	    	}
 	    });
 	    if(typeof option == 'string' )return result;
 	    return thisObj;
 	};
 	
 	$.fn.autoCompleteAt.defaults = {
 		selectInsert:false
 	};
 	
 }(window.jQuery);
 /*=====================================================================================
  * easy-bootstrap-multiDatePicker v2.0
  * 
  * Atauthor:niyq
  * Atdate:2013/08/22
  * Atdependce:jquery
  *=====================================================================================*/
  !function($){
 	
 	"use strict";

 	var MultiDatePicker = function(element,dataOptions){
 		var thisObject = this;
 		this.$element = $(element);
 		this.dataOptions = $.extend({}, $.fn.multiDatePicker.defaults, dataOptions);
 		this.keypressListener = false;
 		this.keydownListener = false;
 		this.boxShow = false;
 		this.role = this.$element.data("role");
 		this.init();
 	};
	
	MultiDatePicker.prototype.init = function(){
		var that = this;
		that.$element.addClass("multiDatePickerBox").addClass("k-block");
		that.$element.append(that.headBar=$('<div class="multiDatePickerHead"></div>'));
		that.headBar.append(that.datePicker = $('<input/>'));
		if(that.dataOptions.format){
			that.datePicker.kendoDatePicker({
				format:that.dataOptions.format
			});
		}else{
			that.datePicker.kendoDatePicker();
		}
		that.headBar.append(that.addBtn = $('<div class="k-button" style="margin-left:20px;padding:1px 2px 2px;" title="添加当前日期"><span class="k-icon k-si-plus"></span></div>'));
		that.headBar.append(that.dltBtn = $('<div class="k-button" style="margin-left:3px;padding:1px 2px 2px;" title="删除选中日期"><span class="k-icon k-si-minus"></span></div>'));
		that.headBar.append(that.clrBtn = $('<div class="k-button" style="margin-left:3px;padding:1px 2px 2px;" title="清空日期"><span class="k-icon k-si-cancel"></span></div>'));
		that.$element.append($('<div class="k-block" style="border-radius:0;margin:3px;display:block;resize:none;background-color:white;background:white;" disabled="true"></div>'));
		that.textarea = that.$element.find(".k-block").height(that.$element.height()-41);
		that.addBtn.click(function(){
			that.addDate();
		});
		that.dltBtn.click(function(){
			that.dltDate();
		});
		that.clrBtn.click(function(){
			that.clrDate();
		});
	};
	
	MultiDatePicker.prototype.addDate = function(){
		var that = this;
		var value = that.datePicker.val();
		var html = that.textarea.html();
		if(html.indexOf(value) >= 0){
			return;
		}
		if(value){
			if(that.textarea.find(".valueOption").length > 0){
				that.textarea.append('<span class="comma">|</span>').append($('<span class="valueOption">'+value+'</span>').click(function(){
					that.select(this);
				}));
			}else{
				that.textarea.append($('<span class="valueOption">'+value+'</span>').click(function(){
					that.select(this);
				}));
			}
		}
	};
	
	MultiDatePicker.prototype.dltDate = function(){
		var that = this;
		if(that.textarea.find(".k-state-selected").length > 0){
			that.textarea.find(".k-state-selected").each(function(){
				if($(this).prev().attr("class") == "comma"){
					$(this).prev().remove();
				}else if($(this).next().attr("class") == "comma"){
					$(this).next().remove();
				}
				$(this).remove();
			});
		}else{
			that.textarea.find(".valueOption").eq(that.textarea.find(".valueOption").length-1).each(function(){
				if($(this).prev().attr("class") == "comma"){
					$(this).prev().remove();
				}else if($(this).next().attr("class") == "comma"){
					$(this).next().remove();
				}
				$(this).remove();
			});
		}
	};
	
	MultiDatePicker.prototype.clrDate = function(){
		var that = this;
		that.textarea.html("");
	};
	
	MultiDatePicker.prototype.select = function(obj){
		var that = this;
		that.$element.find(".k-state-selected").not(obj).removeClass("k-state-selected");
		$(obj).addClass("k-state-selected");
	};
	
	MultiDatePicker.prototype.value = function(value){
		var that = this;
		if(!value && value != ""){
			var gotValue = "";
			that.textarea.find(".valueOption").each(function(){
				if(gotValue == ""){
					gotValue = $(this).html();
				}else{
					gotValue = gotValue + "|" + $(this).html();
				}
			});
			return gotValue;
		}else{
			if(typeof value == "string"){
				value = value.split(",");
			}
			var html = "";
			for(var i = 0;i < value.length;i++){
				if(i==0){
					html = '<span class="valueOption">' + value[i] + '</span>';
				}else{
					html = html + '<span class="comma">|</span><span class="valueOption">' + value[i] + '</span>';
				}
			}
			that.textarea.html(html);
		}
	};
 	
 	$.fn.multiDatePicker = function (option,param) {
 		var result = null;
 	    var thisObj = this.each(function () {
 	    	var $this = $(this)
 	        	, thisObject = $this.data('multiDatePicker')
 	        	, dataOptions = typeof option == 'object' && option;
 	    	if(typeof option == 'string' ){
 	    		result = thisObject[option](param);
 	    	}else{
 				if(!thisObject){
 					$this.data('multiDatePicker', (thisObject = new MultiDatePicker(this, dataOptions)));
 				}else{
 					$this.multiDatePicker('reInit',dataOptions);
 				}
 	    	}
 	    });
 	    if(typeof option == 'string' )return result;
 	    return thisObj;
 	};
 	
 	$.fn.multiDatePicker.defaults = {
 		selectInsert:false,
		format:"yyyy-MM-dd"
 	};
 	
 }(window.jQuery);
 