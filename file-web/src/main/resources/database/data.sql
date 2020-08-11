-- 初始化用户
insert ignore into user (userId, username, salt, password) value (1, 'admin', 'admin', 'df655ad8d3229f3269fad2a8bab59b6c');
insert ignore into role (roleId, role, description) values (1, 'admin', '管理员'),(2, 'user', '普通用户');
insert ignore into permission (permissionId, permission) values (1, 'admin'),(2, 'user');

delete from user_role where userId = 1 and roleId = 1;
insert into user_role (userId, roleId) value (1, 1);
delete from role_permission where roleId = 1 and permissionId = 1;
insert into role_permission (roleId, permissionId) value (1, 1);