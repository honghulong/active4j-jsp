package com.active4j.hr.oa.leave.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.active4j.hr.base.controller.BaseController;
import com.active4j.hr.core.annotation.Log;
import com.active4j.hr.core.beanutil.MyBeanUtils;
import com.active4j.hr.core.model.AjaxJson;
import com.active4j.hr.core.model.LogType;
import com.active4j.hr.core.query.QueryUtils;
import com.active4j.hr.core.shiro.ShiroUtils;
import com.active4j.hr.core.util.ListUtils;
import com.active4j.hr.core.util.ResponseUtil;
import com.active4j.hr.core.web.tag.model.DataGrid;
import com.active4j.hr.oa.leave.entity.LeaveEntity;
import com.active4j.hr.oa.leave.service.LeaveService;
import com.active4j.hr.system.entity.SysUserEntity;
import com.active4j.hr.system.service.SysUserService;
import com.active4j.hr.system.util.SystemUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/oa/leave")
@Slf4j
public class LeaveController extends BaseController {

	@Autowired
	private LeaveService leaveService;

	@Autowired
	private SysUserService sysUserService;

	@RequestMapping("/list")
	public ModelAndView list() {
		ModelAndView view = new ModelAndView("oa/leave/leavelist");
		List<SysUserEntity> lstUsers = sysUserService.list();
		view.addObject("usersReplace", ListUtils.listToReplaceStr(lstUsers, "realName", "id"));
		return view;
	}

	@RequestMapping("/datagrid")
	public void datagrid(LeaveEntity leaveEntity, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		QueryWrapper<LeaveEntity> queryWrapper = QueryUtils.installQueryWrapper(leaveEntity, request.getParameterMap(), dataGrid);
		queryWrapper.eq("USER_ID", ShiroUtils.getSessionUserId());
		IPage<LeaveEntity> lstResult = leaveService.page(new Page<LeaveEntity>(dataGrid.getPage(), dataGrid.getRows()), queryWrapper);
		ResponseUtil.writeJson(response, dataGrid, lstResult);
	}

	@RequestMapping("/addorupdate")
	public ModelAndView addorupdate(LeaveEntity leaveEntity, HttpServletRequest req) {
		ModelAndView view = new ModelAndView("oa/leave/leave");
		if (StringUtils.isEmpty(leaveEntity.getId())) {
			leaveEntity = new LeaveEntity();
			view.addObject("leave", leaveEntity);
		} else {
			leaveEntity = leaveService.getById(leaveEntity.getId());
			view.addObject("leave", leaveEntity);
		}
		return view;
	}

	@RequestMapping("/save")
	@ResponseBody
	@Log(type = LogType.save, name = "保存请假信息", memo = "新增或编辑保存了请假信息")
	public AjaxJson save(LeaveEntity leaveEntity, HttpServletRequest req) {
		AjaxJson j = new AjaxJson();
		try {
			if (StringUtils.isEmpty(leaveEntity.getLeaveType())) {
				j.setSuccess(false);
				j.setMsg("请假类型不能为空");
				return j;
			}
			if (null == leaveEntity.getStartTime()) {
				j.setSuccess(false);
				j.setMsg("请假开始时间不能为空");
				return j;
			}
			if (null == leaveEntity.getEndTime()) {
				j.setSuccess(false);
				j.setMsg("请假结束时间不能为空");
				return j;
			}
			if (leaveEntity.getEndTime().before(leaveEntity.getStartTime())) {
				j.setSuccess(false);
				j.setMsg("请假结束时间不能早于开始时间");
				return j;
			}

			if (StringUtils.isEmpty(leaveEntity.getId())) {
				leaveEntity.setUserId(ShiroUtils.getSessionUserId());
				leaveEntity.setLeaveStatus("0");
				leaveService.save(leaveEntity);
			} else {
				LeaveEntity tmp = leaveService.getById(leaveEntity.getId());
				MyBeanUtils.copyBeanNotNull2Bean(leaveEntity, tmp);
				leaveService.saveOrUpdate(tmp);
			}
		} catch (Exception e) {
			log.error("保存请假信息出错，错误信息：{}", e.getMessage());
			j.setSuccess(false);
			j.setMsg("保存请假信息错误");
			e.printStackTrace();
		}
		return j;
	}

	@RequestMapping("/del")
	@ResponseBody
	@Log(type = LogType.del, name = "删除请假信息", memo = "删除了请假信息")
	public AjaxJson del(String id, HttpServletRequest req) {
		AjaxJson j = new AjaxJson();
		try {
			if (StringUtils.isEmpty(id)) {
				j.setSuccess(false);
				j.setMsg("请选择需要删除的请假信息");
				return j;
			}
			leaveService.removeById(id);
		} catch (Exception e) {
			log.error("删除请假信息出错，错误信息：{}", e.getMessage());
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}

	@RequestMapping("/cancel")
	@ResponseBody
	@Log(type = LogType.save, name = "销假", memo = "销假操作")
	public AjaxJson cancel(String id, HttpServletRequest req) {
		AjaxJson j = new AjaxJson();
		try {
			if (StringUtils.isEmpty(id)) {
				j.setSuccess(false);
				j.setMsg("请选择需要销假的记录");
				return j;
			}
			LeaveEntity leave = leaveService.getById(id);
			if (null != leave) {
				leave.setLeaveStatus("3");
				leave.setCancelTime(new java.util.Date());
				leaveService.saveOrUpdate(leave);
			}
		} catch (Exception e) {
			log.error("销假操作出错，错误信息：{}", e.getMessage());
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}
}
