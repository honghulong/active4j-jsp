<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
<t:base type="default,select2,icheck,datetimePicker"></t:base>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content animated fadeInRight">
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox float-e-margins">
					<div class="ibox-content">
						<t:formvalid action="oa/leave/save">
							<input type="hidden" name="id" id="id" value="${leave.id }">
							<div class="form-group">
								<label class="col-sm-3 control-label">请假类型*：</label>
								<div class="col-sm-8">
									<t:dictSelect name="leaveType" type="select" typeGroupCode="oa_leave_type" defaultVal="${leave.leaveType}"></t:dictSelect>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">开始时间*：</label>
								<div class="col-sm-8">
									<input id="startTime" name="startTime" type="text" class="form-control layer-date" value="<fmt:formatDate value='${leave.startTime}' pattern='yyyy-MM-dd HH:mm:ss'/>" onclick="laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">结束时间*：</label>
								<div class="col-sm-8">
									<input id="endTime" name="endTime" type="text" class="form-control layer-date" value="<fmt:formatDate value='${leave.endTime}' pattern='yyyy-MM-dd HH:mm:ss'/>" onclick="laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">请假原因：</label>
								<div class="col-sm-8">
									<textarea id="leaveReason" name="leaveReason" class="form-control" rows="4" maxlength="200">${leave.leaveReason}</textarea>
								</div>
							</div>
						</t:formvalid>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
