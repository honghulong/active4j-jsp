-- 请假类型字典
INSERT INTO `sys_dic` (`ID`, `CREATE_DATE`, `CREATE_NAME`, `UPDATE_DATE`, `UPDATE_NAME`, `CODE`, `NAME`, `VERSIONS`, `MEMO`) VALUES
('oa_leave_type', NOW(), '管理员', NULL, NULL, 'oa_leave_type', '请假类型', 0, NULL);

INSERT INTO `sys_dic_value` (`ID`, `CREATE_DATE`, `CREATE_NAME`, `UPDATE_DATE`, `UPDATE_NAME`, `VALUE`, `LABEL`, `PARENT_ID`, `VERSIONS`, `MEMO`) VALUES
('oa_leave_type_1', NOW(), '管理员', NULL, NULL, 'sick', '病假', 'oa_leave_type', 0, NULL),
('oa_leave_type_2', NOW(), '管理员', NULL, NULL, 'personal', '事假', 'oa_leave_type', 0, NULL),
('oa_leave_type_3', NOW(), '管理员', NULL, NULL, 'maternity', '产假', 'oa_leave_type', 0, NULL),
('oa_leave_type_4', NOW(), '管理员', NULL, NULL, 'offshift', '调休', 'oa_leave_type', 0, NULL);

-- 请假审批状态字典
INSERT INTO `sys_dic` (`ID`, `CREATE_DATE`, `CREATE_NAME`, `UPDATE_DATE`, `UPDATE_NAME`, `CODE`, `NAME`, `VERSIONS`, `MEMO`) VALUES
('oa_leave_status', NOW(), '管理员', NULL, NULL, 'oa_leave_status', '请假审批状态', 0, NULL);

INSERT INTO `sys_dic_value` (`ID`, `CREATE_DATE`, `CREATE_NAME`, `UPDATE_DATE`, `UPDATE_NAME`, `VALUE`, `LABEL`, `PARENT_ID`, `VERSIONS`, `MEMO`) VALUES
('oa_leave_status_0', NOW(), '管理员', NULL, NULL, '0', '刚提交', 'oa_leave_status', 0, NULL),
('oa_leave_status_1', NOW(), '管理员', NULL, NULL, '1', '审核通过', 'oa_leave_status', 0, NULL),
('oa_leave_status_2', NOW(), '管理员', NULL, NULL, '2', '被退回', 'oa_leave_status', 0, NULL),
('oa_leave_status_3', NOW(), '管理员', NULL, NULL, '3', '已销假', 'oa_leave_status', 0, NULL);

-- 请假管理菜单（父菜单：常用功能）
INSERT INTO `sys_function` (`ID`, `VERSIONS`, `CREATE_NAME`, `CREATE_DATE`, `UPDATE_NAME`, `UPDATE_DATE`, `NAME`, `PARENT_ID`, `URL`, `ICON`, `ORDER_NO`, `MEMO`, `LEVEL`) VALUES
('oa_leave_menu', 0, '管理员', NOW(), NULL, NULL, '请假管理', '402881ec521bf2b10152eeeba1e10018', 'oa/leave/list', '', 3, NULL, 1);
