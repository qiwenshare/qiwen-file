package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.SysParam;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2021/12/30 14:54
 */
public interface ISysParamService  extends IService<SysParam> {
    String getValue(String key);
}
