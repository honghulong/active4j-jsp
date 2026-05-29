<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
<t:base type="default,select2,jqgrid,datetimePicker,laydate"></t:base>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content animated fadeInRight">
		<div class="row">
			<div class="col-sm-12">
				<div class="row">
					<div class="col-sm-12" id="searchGroupId">
					</div>
				</div>
				<div class="ibox">
					<div class="ibox-content">
						<div id="jqGrid_wrapper" class="jqGrid_wrapper"></div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<t:datagrid actionUrl="oa/leave/datagrid" tableContentId="jqGrid_wrapper" searchGroupId="searchGroupId" fit="true" caption="请假管理" name="table_list_leave" pageSize="20" sortName="createDate" sortOrder="desc">
		<t:dgCol name="id" label="编号" hidden="true" key="true" width="20"></t:dgCol>
		<t:dgCol name="createName" label="请假人" width="80" query="true"></t:dgCol>
		<t:dgCol name="leaveType" label="请假类型" width="80" dictionary="oa_leave_type" query="true"></t:dgCol>
		<t:dgCol name="startTime" label="开始时间" width="140" datefmt="yyyy-MM-dd HH:mm:ss" query="true" queryModel="group" datePlugin="laydate"></t:dgCol>
		<t:dgCol name="endTime" label="结束时间" width="140" datefmt="yyyy-MM-dd HH:mm:ss"></t:dgCol>
		<t:dgCol name="leaveReason" label="请假原因" width="200"></t:dgCol>
		<t:dgCol name="leaveStatus" label="审批状态" width="80" dictionary="oa_leave_status" query="true"></t:dgCol>
		<t:dgCol name="cancelTime" label="销假时间" width="140" datefmt="yyyy-MM-dd HH:mm:ss"></t:dgCol>
		<t:dgCol name="createDate" label="创建时间" width="140" datefmt="yyyy-MM-dd HH:mm:ss"></t:dgCol>
		<t:dgCol name="opt" label="操作" width="200"></t:dgCol>
		<t:dgDelOpt label="删除" url="oa/leave/del?id={id}" operationCode="oa:leave:del"/>
		<t:dgToolBar url="oa/leave/addorupdate" type="add" width="60%" operationCode="oa:leave:add"></t:dgToolBar>
		<t:dgToolBar url="oa/leave/addorupdate" type="edit" width="60%" operationCode="oa:leave:edit"></t:dgToolBar>
		<t:dgToolBar label="销假" icon="fa fa-check" url="oa/leave/cancel" type="define" funName="doCancel" operationCode="oa:leave:cancel"></t:dgToolBar>
	</t:datagrid>

<script type="text/javascript">
	function doCancel() {
		var rowId = $('#table_list_leave').jqGrid('getGridParam','selrow');
		if(!rowId) {
			qhAlert('请选择要销假的记录');
			return;
		}
		qhConfirm("你确定要销假吗?", function(index) {
			parent.layer.close(index);
			$.post("oa/leave/cancel", {id : rowId}, function(data){
				if(data.success) {
					qhTipSuccess(data.msg);
					reloadTable('table_list_leave');
				}else {
					qhTipWarning(data.msg);
				}
			});
		}, function() {
		});
	}
</script>
</body>
</html>
