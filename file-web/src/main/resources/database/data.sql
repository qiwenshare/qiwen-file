-- 初始化文章来源
INSERT ignore INTO essaysource (essaySourceId, essaySourceName) VALUES (1, '原创'),(2, '转载');
-- 初始化文章分类
delete from essaysort where essaysortid in (1, 2, 3, 4, 5, 6, 7, 8, 9);
INSERT INTO essaysort (essaysortid, essaysortName) VALUES (1,'程序人生'), (2, '前端'), (3, '数据库'), (4, 'Java'),
(5, '大数据/云计算'),(6, 'Python'),(7, '系统部署和维护'),(8, '数据结构与算法'),(9, '其他');
-- 初始化用户
insert ignore into user (userId, username, salt, password) value (1, 'admin', 'admin', 'df655ad8d3229f3269fad2a8bab59b6c');
insert ignore into role (roleid, role, description) values (1, 'admin', '管理员'),(2, 'user', '普通用户');
insert ignore into permission (permissionid, permission) values (1, 'admin'),(2, 'user');

delete from user_role where userid = 1 and roleid = 1;
insert into user_role (userid, roleid) value (1, 1);
delete from role_permission where roleid = 1 and permissionid = 1;
insert into role_permission (roleid, permissionid) value (1, 1);