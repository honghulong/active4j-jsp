-- 1. 创建"业务管理"一级菜单
INSERT INTO sys_function (ID, VERSIONS, CREATE_NAME, CREATE_DATE, UPDATE_NAME, UPDATE_DATE, NAME, PARENT_ID, URL, ICON, ORDER_NO, MEMO, LEVEL) VALUES
('biz_menu_root', 0, '管理员', NOW(), NULL, NULL, '业务管理', NULL, '', '', 2, NULL, 0);

-- 2. 将"请假管理"从系统管理移到业务管理下
UPDATE sys_function SET PARENT_ID = 'biz_menu_root' WHERE ID = 'oa_leave_menu';
