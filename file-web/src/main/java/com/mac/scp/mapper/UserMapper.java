package com.mac.scp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mac.scp.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 *
 * @author WeiHongBin
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
