	<script type="text/javascript">
		var dataSource = null;
		var entList = ${entJo};
		//当前选中的ent
		var currentEnt;
		var currEntId;
		
		$(document).ready(function(){
			$("#searchBtn").bind("click",query);
			//$("#searchDiv").kendoValidator();
			$("#entIdQuery").html("");
			for(var i=0;i<entList.length;i++){
				var list = entList[i];
				$("#entIdQuery").append($('<option value="'+entList[i].enterpriseId+'" listId="'+i+'">'+entList[i].enterpriseId+'</option>'));
			}
			$("#entIdQuery").kendoDropDownList();
			ifShowEntIdQ();
		});
		
		function ifShowEntIdQ(){
			if(${bigEntFlag}){
				$("#entIdQuery").parent().parent().show();
			}else{
				$("#entIdQuery").parent().parent().hide();
			}
		}
		
		function query() {
			dataSource.read();
		}
		
		//为了简便封装的控件初始化方法.
		//参数:控件对象,控件ID，控件名，后台丢上来的json数据列表，第一个选项的text(值为空)
		function initSelect(id, name, itemList, firstText, requirdMsg) {
			//因为kendoUI没有提供下拉列表的动态变更，这里实现起来比较麻烦
			//a. 判断合作伙伴的下拉控件 是否已经被框架初始化出父元素，初始化了就删除父元素
			var $select = $('#'+id);
			if($select.parent(".k-combobox").length>0){
				if (requirdMsg){
					$select.parent(".k-combobox").after('<select id="'+ id +'" name="'+ name +'" required validationMessage="'+ requirdMsg +'"></select>').remove();
				} else {
					$select.parent(".k-combobox").after('<select id="'+ id +'" name="'+ name +'"></select>').remove();
				}
				
			}
			//b.注意：因为前面已经把原来的对象删除重写，所以这里需要根据ID重新取一次控件。
			$select = $('#'+id);
			//c. 重写控件的html选项
			if (firstText){
				$select.html("<option value=''>"+ firstText +"</option>");
			}
			for(var i=0;i<itemList.length;i++){
				$select.append('<option value="'+itemList[i].value+'">'+itemList[i].text + '</option>');
			}
			//d. 初始化为Kendo控件
			$select.kendoComboBox();
		}
		
		function initSelect1(selectTypeName, id, itemList, firstText) {
			var $select = $('#'+id);
			if($select.data(selectTypeName) != undefined){
				$select.data(selectTypeName).destroy();
			}
			$select.empty();
			//重写控件的html选项
			if (firstText){
				$select.html("<option value=''>"+ firstText +"</option>");
			}
			for(var i=0;i<itemList.length;i++){
				$select.append('<option value="'+itemList[i].value+'">'+itemList[i].text + '</option>');
			}
			if ("kendoDropDownList" == selectTypeName){
				$select.kendoDropDownList();
			} else {
				$select.kendoComboBox();
			}
		}
		
		function clearSelect(selectTypeName, id) {
			var $select = $('#'+id);
			if($select.data("kendoDropDownList") != undefined){
				$select.data("kendoDropDownList").destroy();
			}
			if($select.data("kendoComboBox") != undefined){
				$select.data("kendoComboBox").destroy();
			}
			$select.empty();
		}
		
		function initSelectDDL(id, name, postUrl, firstRow, txtField, valueField, param) {
			//因为kendoUI没有提供下拉列表的动态变更，这里实现起来比较麻烦
			var $select = $('#'+id);
			if($select.data("kendoDropDownList") != undefined){
				$select.data("kendoDropDownList").destroy();
			}
			$select.empty();
			
			$select = $('#'+id);
			$select.kendoDropDownList({
				autoBind : false,
				optionLabel : firstRow,
				dataTextField : txtField,
				dataValueField : valueField,
				dataSource : {
					serverFiltering : true,
					transport : {
						read : {
							dataType : "json",
							url : postUrl,
							type : "post",
							data : param
						}
					},
					schema : {
						data : "rows"
					}
				}
			}).data("kendoDropDownList");
			$select.data("kendoDropDownList").value("");
		}
		
		//没有第一行
		function initSelectDDLY(id, name, postUrl, txtField, valueField, param) {
			//因为kendoUI没有提供下拉列表的动态变更，这里实现起来比较麻烦
			var $select = $('#'+id);
			if($select.data("kendoDropDownList") != undefined){
				$select.data("kendoDropDownList").destroy();
			}
			$select.empty();
			
			$select = $('#'+id);
			$select.kendoDropDownList({
				autoBind : true,
				dataTextField : txtField,
				dataValueField : valueField,
				dataSource : {
					serverFiltering : true,
					transport : {
						read : {
							dataType : "json",
							url : postUrl,
							type : "post",
							data : param
						}
					},
					schema : {
						data : "rows"
					}
				}
			}).data("kendoDropDownList");
			$select.data("kendoDropDownList").select(0);
			
		}
	</script>