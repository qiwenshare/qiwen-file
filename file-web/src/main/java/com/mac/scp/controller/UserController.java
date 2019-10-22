package com.mac.scp.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.codehaus.jackson.type.TypeReference;
//import org.springframework.stereotype.Controller;
import com.mac.common.cbb.RestResult;
import com.mac.common.domain.TableData;
import com.mac.common.domain.TableQueryBean;
import com.mac.scp.api.IUserService;

import com.mac.scp.domain.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户控制类
 *
 * @author ma116
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    IUserService userService;

    public static final int TEXT = 4;

    /**
     * 当前模块
     */
    public static final String CURRENT_MODULE = "用户管理";

    /**
     * 用户注册
     *
     * @return
     */
    //@MyLog(operation = "用户注册", module = CURRENT_MODULE)
    @RequestMapping("/userregister")
    @ResponseBody
    public ModelAndView userRegister() {
        ModelAndView mv = new ModelAndView("/user/userRegister.html");
        return mv;
    }

    @RequestMapping("/adduser")
    @ResponseBody
    public String addUser(UserBean userBean) {
        RestResult<String> result = userService.registerUser(userBean);

        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }



    /**
     * 用户登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/userlogin")
    @ResponseBody
    public RestResult<UserBean> userLogin(HttpServletRequest request, Map<String, Object> map) {
        RestResult<UserBean> result = new RestResult<UserBean>();

        System.out.println("HomeController.login()");
        // 登录失败从request中获取shiro处理的异常信息。
        //        // shiroLoginFailure:就是shiro异常类的全类名.
        String exception = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("exception=" + exception);
        String msg = "";
        if (exception != null) { //登录失败
            if (UnknownAccountException.class.getName().equals(exception)) {
                msg = "账号不存在";
            } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
                msg = "密码不正确";
            } else if ("kaptchaValidateFailed".equals(exception)) {
                msg = "验证码错误";
            } else {
                msg = exception;
            }
            result.setSuccess(false);
            result.setErrorMessage(msg);
        }
        else{
            //登录成功
            result.setSuccess(true);
        }

        return result;
    }

    /**
     * 检查用户登录信息
     *
     * @return
     */
    @RequestMapping("/checkuserlogininfo")
    @ResponseBody
    public String checkUserLoginInfo(HttpServletRequest request) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        if (sessionUserBean != null) {
            UserBean userInfo = userService.getUserInfoById(sessionUserBean.getUserId());
            restResult.setData(userInfo);
            restResult.setSuccess(true);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户暂未登录");
        }
        return JSON.toJSONString(restResult);
    }

    /**
     * 得到用户信息通过id
     *
     * @param userid
     * @return
     */
    @RequestMapping("/getuserinfobyid")
    @ResponseBody
    public String getUserInfoById(int userid) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();

        UserBean userBean = userService.getUserInfoById(userid);
        if (userBean == null) {
            restResult.setSuccess(false);
            restResult.setErrorCode("100001");
            restResult.setErrorMessage("用户不存在！");
        } else {
            restResult.setSuccess(true);
            restResult.setData(userBean);
        }
        String resultJson = JSON.toJSONString(restResult);
        return resultJson;
    }


    /**
     * 修改用戶信息
     *
     * @param userBean
     * @return
     */
    @RequestMapping("/updateuserinfo")
    @ResponseBody
    public RestResult<String> updateUserInfo(HttpServletRequest request, UserBean userBean) {
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        userBean.setUserId(sessionUserBean.getUserId());
        RestResult<String> restResult = userService.updateUserInfo(userBean);


        return restResult;
    }

    /**
     * 得到所有的用户
     *
     * @return
     */
    @RequestMapping("/selectalluserlist")
    @ResponseBody
    public String selectAllUserList(TableQueryBean tableQueryBean) {

        TableData<List<UserBean>> miniuiTableData = new TableData<List<UserBean>>();

        String resultJson = "";

        List<UserBean> userList = userService.selectUserList(tableQueryBean);
        int userCount = userService.selectUserCountByCondition(tableQueryBean);
        miniuiTableData.setData(userList);
        miniuiTableData.setCount(userCount);


        resultJson = JSON.toJSONString(miniuiTableData);
        return resultJson;
    }

    /**
     * 只获取管理员用户
     *
     * @return
     */
    @RequestMapping("/selectadminuserlist")
    @ResponseBody
    public String selectAdminUserList() {

        TableData<List<UserBean>> miniuiTableData = new TableData<List<UserBean>>();

        String resultJson = "";

        List<UserBean> userList = userService.selectAdminUserList();

        miniuiTableData.setData(userList);

        resultJson = JSON.toJSONString(miniuiTableData);
        return resultJson;
    }

    /**
     * 得到所有的角色
     *
     * @return
     */
    @RequestMapping("/selectrolelist")
    @ResponseBody
    public TableData<List<Role>> selectRoleList() {
        TableData<List<Role>> miniuiTableData = new TableData<List<Role>>();
        List<Role> result = userService.selectRoleList();
        miniuiTableData.setData(result);
        return miniuiTableData;
    }

    /**
     * 得到所有的权限
     *
     * @return
     */
    @RequestMapping("/selectpermissionlist")
    @ResponseBody
    public TableData<List<Permission>> selectPermissionList(TableQueryBean tableQueryBean) {
        TableData<List<Permission>> miniuiTableData = new TableData<List<Permission>>();
        List<Permission> result = userService.selectPermissionList(tableQueryBean);
        miniuiTableData.setData(result);
        return miniuiTableData;
    }

}
