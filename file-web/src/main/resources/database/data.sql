-- 初始化用户
insert ignore into user (userId, username, salt, password) value (1, 'admin', 'admin', 'df655ad8d3229f3269fad2a8bab59b6c');
insert ignore into role (roleid, role, description) values (1, 'admin', '管理员'),(2, 'user', '普通用户');
insert ignore into permission (permissionid, permission) values (1, 'admin'),(2, 'user');

delete from user_role where userid = 1 and roleid = 1;
insert into user_role (userid, roleid) value (1, 1);
delete from role_permission where roleid = 1 and permissionid = 1;
insert into role_permission (roleid, permissionid) value (1, 1);