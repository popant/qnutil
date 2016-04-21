/**
 * 
 */
(function() {
	"use strict";
	window.KendoSelectorView = KendoSelectorView;
	
	/**
	 * 选择器构造函数
	 */
	function KendoSelectorView(divId) {
		
		this.src = null;
		this.dst = null;
		
		this.divId = divId;
		this.srcId = divId + "LeftTable";
		this.dstId = divId + "RightTable";
		
		this.srcAttr = {};
		this.dstAttr = {};
		
		this.assignButtonTitle = "分配";
		this.unassignButtonTitle = "取消分配";
		
		this.assignButtonId = divId + "AssignBtn";
		this.unassignButtonId = divId + "UnassignBtn";
		
		this.enableDblClickMove = true;
		
	};
	
	KendoSelectorView.prototype.getInnerHTML = function() {
		return "<table>" + 
			"<tr><td width='50%'><div id='" + this.srcId + "'></div></td>" + 
			"<td width='50%'><div id='" + this.dstId + "'></div></td></tr>" +
			"<tr><td><button type='button' id='" + this.assignButtonId + "'>" + this.assignButtonTitle + "</button></td>" + 
			"<td><button type='button' id='" + this.unassignButtonId + "'>" + this.unassignButtonTitle + "</button></td></tr>" +
			"</table>";
	};
	
	/**
	 * 展示选择器
	 */
	KendoSelectorView.prototype.show = function() {
		
		$("#" + this.divId).html(this.getInnerHTML());
		this.src = new KendoTable(this.srcId);
		this.dst = new KendoTable(this.dstId);
		this.src.show(this.srcAttr, this.enableDblClickMove);
		this.dst.show(this.dstAttr, this.enableDblClickMove);
		
		this.src.oppoTable = this.dst;
		this.dst.oppoTable = this.src;
		
		// 绑定分配/取消分配按钮事件
		var _this = this;
		$("#" + this.assignButtonId).bind("click", function() {
			$(_this.src.table).find("input:checkbox[name='rowCheck']:checked").each(function() {
				var rowId = $(this).attr("rowid");
				moveData(_this.src.getDataSource(), _this.dst.getDataSource(), rowId);
			});
			$("#" + _this.divId).find("input:checkbox[name='allCheck']").prop("checked", false);
		});
		
		$("#" + this.unassignButtonId).bind("click", function() {
			$(_this.dst.table).find("input:checkbox[name='rowCheck']:checked").each(function() {
				var rowId = $(this).attr("rowid");
				moveData(_this.dst.getDataSource(), _this.src.getDataSource(), rowId);
			});
			$("#" + _this.divId).find("input:checkbox[name='allCheck']").prop("checked", false);
		});
	};
	
	KendoSelectorView.prototype.reset = function(dataSrc, dataDst) {
		this.src.getDataSource().read(dataSrc ? dataSrc : undefined);
		this.dst.getDataSource().read(dataDst ? dataDst : undefined);
	};
	
	/**
	 * 获取已分配的所有数据
	 * @returns {Array}
	 */
	KendoSelectorView.prototype.getSelectedRows = function() {
		var data = this.dst.getDataSource().data();
		var selectedRows = [];
		for (var i = 0, len = data.length; i < len; i++) {
			selectedRows.push(data[i]);
		}
		return selectedRows;
	};
	
	/**
	 * 获取已分配的所有数据的ID
	 * @returns {Array}
	 */
	KendoSelectorView.prototype.getSelectedIds = function() {
		var data = this.dst.getDataSource().data();
		var selectedIds = [];
		for (var i = 0, len = data.length; i < len; i++) {
			selectedIds.push(data[i][this.dstAttr.primaryKey]);
		}
		return selectedIds;
	};
	
	/**
	 * 单表格构造函数
	 */
	function KendoTable(id) {
		
		// 自身表格，jQuery对象
		this.table = $("#" + id);
		
		// 旁边的表格，KendoTable对象
		this.oppoTable = null;
	};
	
	KendoTable.prototype.getDataSource = function() {
		return this.table.data("kendoGrid").dataSource;
	};
	
	/**
	 * 展示单表格
	 * @param attr
	 */
	KendoTable.prototype.show = function(attr, dblClickRowMove) {
		var _this = this;
		this.table.kendoGrid({
			dataSource : new kendo.data.DataSource({
				transport : {
					read : {
						url : attr.url,
						dataType : "json",
						data : attr.data,
						type : attr.type ? attr.type : "get",
						cache : false
					}
				},
				schema : {
					model : {
						id : attr.primaryKey
					}
				},
				sort : {
					field : attr.sortKey ? attr.sortKey : attr.primaryKey,
					dir : attr.sortMethod ? attr.sortMethod : "asc"		
				},
				pageSize : attr.pageSize ? attr.pageSize : 5
			}),
			pageable : {
				pageSizes : attr.pageSizes ? 
						(Utils.isArray(attr.pageSizes) ? attr.pageSizes : [5, 10, 20]) : false
			},
			height : attr.height ? attr.height : 260,
			columns : addCheckAll(attr.columns, attr.primaryKey),
			selectable : "row",
			change : function(e) {
				var selectedRows = this.select();
				for (var i = 0, len = selectedRows.length; i < len; i++) {
					var input = $(_this.table).find("input:checkbox[name='rowCheck'][rowid='" + this.dataItem(selectedRows[i])[attr.primaryKey] + "']");
					$(input).each(function() {
						$(this).prop("checked", !$(this).prop("checked"));
					});
					
					attr.rowCheckedClass && changeRowColor($(input), attr.rowCheckedClass);
				}
			}
			
		});
		
		// 绑定全选按钮事件
		var _this = this;
		this.table.find("input:checkbox[name='allCheck']").bind("click", function() {
			_this.table.find("input:checkbox[name='rowCheck']").prop("checked", $(this).prop("checked"));
			_this.table.find("input:checkbox[name='rowCheck']").each(function() {
				attr.rowCheckedClass && changeRowColor($(this), attr.rowCheckedClass);
			});
		});
		
		if (dblClickRowMove) {
			// 绑定行双击事件
			this.table.find("table.k-selectable").delegate("tr", "dblclick", function() {
				var rowId = $(this).find("td:eq(0) input:checkbox[name='rowCheck']").attr("rowid");
				moveData(_this.getDataSource(), _this.oppoTable.getDataSource(), rowId);
			});
		}

		
	};
	
	/**
	 * 增加全选复选框
	 */
	function addCheckAll(columns, primaryKey) {
		var checkRow = [{
			field : "", title : "", width : 40,
			headerTemplate : "<input type='checkbox' name='allCheck' />",
			template : function(item) {
				return "<input type='checkbox' name='rowCheck' onclick='return false;' rowid='" + item[primaryKey] + "' />";	 
			}
		}];
		Array.prototype.push.apply(checkRow, columns);
		return checkRow;
	};
	
	/**
	 * 移动数据
	 */
	function moveData(srcDataSource, dstDataSource, dataId) {
		var flag = (dstDataSource.total() == 0);
		var data = srcDataSource.get(dataId);
		dstDataSource.add(data);
		srcDataSource.remove(data);
		flag && dstDataSource.fetch();
	}
	
	/**
	 * 当某行数据被选中/取消时追加/移除某个样式
	 */
	function changeRowColor(rowCheckbox, rowCheckedClass) {
		var tr = rowCheckbox.parent().parent();
		tr.removeClass("k-state-selected");
		if (rowCheckbox.prop("checked")) {
			if (tr.hasClass("k-alt")) {
				tr.removeClass("k-alt");
				tr.addClass("_k-alt");
			}
			tr.addClass(rowCheckedClass);
		} else {
			tr.removeClass(rowCheckedClass);
			if (tr.hasClass("_k-alt")) {
				tr.addClass("k-alt");
			}
		}
	}
	
	var Utils = {
		isArray : function(arr) {
			return Object.prototype.toString.apply(arr) == "[object Array]";
		}
	};
	
})();