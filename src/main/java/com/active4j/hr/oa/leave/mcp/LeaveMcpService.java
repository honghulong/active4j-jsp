package com.active4j.hr.oa.leave.mcp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.active4j.hr.oa.leave.entity.LeaveEntity;
import com.active4j.hr.oa.leave.service.LeaveService;
import com.active4j.hr.system.entity.SysUserEntity;
import com.active4j.hr.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LeaveMcpService {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private SysUserService sysUserService;

    public Map<String, Object> list(String userName, String leaveType, String leaveStatus, int page, int pageSize) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            QueryWrapper<LeaveEntity> queryWrapper = new QueryWrapper<LeaveEntity>();
            if (StringUtils.isNotEmpty(leaveType)) {
                queryWrapper.eq("LEAVE_TYPE", leaveType);
            }
            if (StringUtils.isNotEmpty(leaveStatus)) {
                queryWrapper.eq("LEAVE_STATUS", leaveStatus);
            }
            if (StringUtils.isNotEmpty(userName)) {
                QueryWrapper<SysUserEntity> userQuery = new QueryWrapper<SysUserEntity>();
                userQuery.like("REAL_NAME", userName);
                List<SysUserEntity> users = sysUserService.list(userQuery);
                if (null != users && users.size() > 0) {
                    String[] userIds = users.stream().map(SysUserEntity::getId).toArray(String[]::new);
                    queryWrapper.in("USER_ID", (Object[]) userIds);
                } else {
                    queryWrapper.eq("USER_ID", "-1");
                }
            }
            queryWrapper.orderByDesc("CREATE_DATE");
            IPage<LeaveEntity> leavePage = leaveService.page(new Page<LeaveEntity>(page, pageSize), queryWrapper);
            result.put("success", true);
            result.put("data", leavePage.getRecords());
            result.put("total", leavePage.getTotal());
            result.put("page", leavePage.getCurrent());
            result.put("pageSize", leavePage.getSize());
        } catch (Exception e) {
            log.error("查询请假列表失败", e);
            result.put("success", false);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> myList(String userId, String leaveType, String leaveStatus, int page, int pageSize) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            QueryWrapper<LeaveEntity> queryWrapper = new QueryWrapper<LeaveEntity>();
            if (StringUtils.isNotEmpty(userId)) {
                queryWrapper.eq("USER_ID", userId);
            }
            if (StringUtils.isNotEmpty(leaveType)) {
                queryWrapper.eq("LEAVE_TYPE", leaveType);
            }
            if (StringUtils.isNotEmpty(leaveStatus)) {
                queryWrapper.eq("LEAVE_STATUS", leaveStatus);
            }
            queryWrapper.orderByDesc("CREATE_DATE");
            IPage<LeaveEntity> leavePage = leaveService.page(new Page<LeaveEntity>(page, pageSize), queryWrapper);
            result.put("success", true);
            result.put("data", leavePage.getRecords());
            result.put("total", leavePage.getTotal());
            result.put("page", leavePage.getCurrent());
            result.put("pageSize", leavePage.getSize());
        } catch (Exception e) {
            log.error("查询我的请假失败", e);
            result.put("success", false);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> detail(String id) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            LeaveEntity leave = leaveService.getById(id);
            if (null == leave) {
                result.put("success", false);
                result.put("msg", "请假记录不存在");
                return result;
            }
            result.put("success", true);
            result.put("data", leave);
        } catch (Exception e) {
            log.error("查询请假详情失败", e);
            result.put("success", false);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> add(String userId, String leaveType, Date startTime, Date endTime, String leaveReason) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (StringUtils.isEmpty(leaveType)) {
                result.put("success", false);
                result.put("msg", "请假类型不能为空");
                return result;
            }
            if (null == startTime) {
                result.put("success", false);
                result.put("msg", "请假开始时间不能为空");
                return result;
            }
            if (null == endTime) {
                result.put("success", false);
                result.put("msg", "请假结束时间不能为空");
                return result;
            }
            if (StringUtils.isEmpty(userId)) {
                result.put("success", false);
                result.put("msg", "用户ID不能为空");
                return result;
            }
            LeaveEntity leave = new LeaveEntity();
            leave.setUserId(userId);
            leave.setLeaveType(leaveType);
            leave.setStartTime(startTime);
            leave.setEndTime(endTime);
            leave.setLeaveReason(leaveReason);
            leave.setLeaveStatus("0");
            leaveService.save(leave);
            result.put("success", true);
            result.put("msg", "新增成功");
            result.put("data", leave);
        } catch (Exception e) {
            log.error("新增请假失败", e);
            result.put("success", false);
            result.put("msg", "新增失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> update(String id, String leaveType, Date startTime, Date endTime, String leaveReason) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (StringUtils.isEmpty(id)) {
                result.put("success", false);
                result.put("msg", "ID不能为空");
                return result;
            }
            LeaveEntity tmp = leaveService.getById(id);
            if (null == tmp) {
                result.put("success", false);
                result.put("msg", "请假记录不存在");
                return result;
            }
            if (StringUtils.isNotEmpty(leaveType)) {
                tmp.setLeaveType(leaveType);
            }
            if (null != startTime) {
                tmp.setStartTime(startTime);
            }
            if (null != endTime) {
                tmp.setEndTime(endTime);
            }
            if (StringUtils.isNotEmpty(leaveReason)) {
                tmp.setLeaveReason(leaveReason);
            }
            leaveService.saveOrUpdate(tmp);
            result.put("success", true);
            result.put("msg", "更新成功");
        } catch (Exception e) {
            log.error("更新请假失败", e);
            result.put("success", false);
            result.put("msg", "更新失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> delete(String id) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (StringUtils.isEmpty(id)) {
                result.put("success", false);
                result.put("msg", "ID不能为空");
                return result;
            }
            leaveService.removeById(id);
            result.put("success", true);
            result.put("msg", "删除成功");
        } catch (Exception e) {
            log.error("删除请假失败", e);
            result.put("success", false);
            result.put("msg", "删除失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> cancel(String id) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (StringUtils.isEmpty(id)) {
                result.put("success", false);
                result.put("msg", "ID不能为空");
                return result;
            }
            LeaveEntity leave = leaveService.getById(id);
            if (null == leave) {
                result.put("success", false);
                result.put("msg", "请假记录不存在");
                return result;
            }
            leave.setLeaveStatus("3");
            leave.setCancelTime(new Date());
            leaveService.saveOrUpdate(leave);
            result.put("success", true);
            result.put("msg", "销假成功");
        } catch (Exception e) {
            log.error("销假失败", e);
            result.put("success", false);
            result.put("msg", "销假失败：" + e.getMessage());
        }
        return result;
    }
}
