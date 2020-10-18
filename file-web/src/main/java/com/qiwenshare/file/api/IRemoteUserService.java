package com.qiwenshare.file.api;

import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.domain.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "qiwen-auth") // 服务名
public interface IRemoteUserService {

    @PostMapping("/user/userlogin")
    RestResult<UserBean> userLogin(UserBean userBean);

    @GetMapping("/user/checkuserlogininfo")
    RestResult<UserBean> checkUserLoginInfo(@RequestParam(value = "token") String token);

    @PostMapping("/user/userlogout")
    RestResult<String> userLogout(@RequestParam("token") String token);

    @PostMapping("/user/adduser")
    RestResult<String> addUser(UserBean userBean);
}
