package com.active4j.hr.oa.leave.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.active4j.hr.oa.leave.dao.LeaveDao;
import com.active4j.hr.oa.leave.entity.LeaveEntity;
import com.active4j.hr.oa.leave.service.LeaveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service("leaveService")
@Transactional
public class LeaveServiceImpl extends ServiceImpl<LeaveDao, LeaveEntity> implements LeaveService {

}
