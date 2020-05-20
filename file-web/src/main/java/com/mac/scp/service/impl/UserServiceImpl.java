package com.mac.scp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mac.scp.entity.User;
import com.mac.scp.mapper.UserMapper;
import com.mac.scp.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务实现类
 *
 * @author WeiHongBin
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
