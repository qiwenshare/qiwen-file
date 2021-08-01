insert ignore into role (roleId, role, description) values (1, 'admin', '管理员'),(2, 'user', '普通用户');
insert ignore into permission (permissionId, permission) values (1, 'admin'),(2, 'user');

insert ignore into sysparam (sysParamId, sysParamKey, sysParamValue, sysParamDesc) values (1, 'totalStorageSize', '1024', '总存储大小（单位M）');