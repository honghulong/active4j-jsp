package com.active4j.hr.oa.leave.entity;

import java.util.Date;

import com.active4j.hr.common.entity.BaseEntity;
import com.active4j.hr.core.annotation.QueryField;
import com.active4j.hr.core.query.QueryCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@TableName("oa_leave")
@Getter
@Setter
public class LeaveEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("USER_ID")
	private String userId;

	@TableField("LEAVE_TYPE")
	@QueryField(queryColumn="LEAVE_TYPE", condition=QueryCondition.eq)
	private String leaveType;

	@TableField("START_TIME")
	@QueryField(queryColumn="START_TIME", condition=QueryCondition.range)
	private Date startTime;

	@TableField("END_TIME")
	private Date endTime;

	@TableField("LEAVE_REASON")
	private String leaveReason;

	@TableField("LEAVE_STATUS")
	@QueryField(queryColumn="LEAVE_STATUS", condition=QueryCondition.eq)
	private String leaveStatus;

	@TableField("CANCEL_TIME")
	private Date cancelTime;

}
