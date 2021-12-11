insert ignore into user (userId, username, telephone, salt, password, available) values (1, 'admin', 'admin', 'admin', 'df655ad8d3229f3269fad2a8bab59b6c', 1);

INSERT ignore INTO `role` (`roleid`, `available`, `description`, `roleName`, `createTime`, `createUserId`, `modifyTime`, `modifyUserId`) VALUES (1, 1, '超级管理员', '超级管理员', NULL, NULL, '2021-11-10 20:46:06', NULL);
INSERT ignore INTO `role` (`roleid`, `available`, `description`, `roleName`, `createTime`, `createUserId`, `modifyTime`, `modifyUserId`) VALUES (2, 1, '普通用户', '普通用户', NULL, NULL, NULL, NULL);

insert ignore into sysparam (sysParamId, sysParamKey, sysParamValue, sysParamDesc) values (1, 'totalStorageSize', '1024', '总存储大小（单位M）');