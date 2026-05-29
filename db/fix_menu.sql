DELETE FROM sys_role_function WHERE MENU_ID = 'oa_leave_menu';
DELETE FROM sys_function WHERE ID = 'oa_leave_menu';
INSERT INTO sys_function (ID, VERSIONS, CREATE_NAME, CREATE_DATE, NAME, PARENT_ID, URL, ICON, ORDER_NO, LEVEL) VALUES ('oa_leave_menu', 0, '管理员', NOW(), '请假管理', '402881ec521bf2b10152eeeba1e10018', 'oa/leave/list', '', 3, 1);
