package com.mac.scp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mac.scp.domain.UserBean;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserBean> {

}
