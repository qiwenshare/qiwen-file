package com.mac.scp.config.shiro;

import com.alibaba.fastjson.JSON;
import com.mac.scp.domain.Permission;
import com.mac.scp.domain.Role;
import com.mac.scp.domain.UserBean;
import com.mac.scp.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private UserService userInfoService;

    public static Map<String, UserBean> qqLoginInfoMap = new HashMap<>();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserBean userInfo = (UserBean) principals.getPrimaryPrincipal();
        for (Role role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
            for (Permission p : role.getPermissions()) {
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        //String tempsalt = PasswordUtil.getSaltValue();
        //String newPassword = new SimpleHash("MD5", password, tempsalt, 1024).toHex();
        UserBean qquserinfo = qqLoginInfoMap.get(username + password);
        SimpleAuthenticationInfo authenticationInfo = null;
        if (qquserinfo != null){ //qq登录
            System.out.println(JSON.toJSONString(qquserinfo));
//            UserBean userInfo = new UserBean();
//            userInfo.setUsername(qquserinfo.getNickname());
//            userInfo.setAddrprovince(qquserinfo.getProvince());
//            userInfo.setAddrcity(qquserinfo.getCity());
//            userInfo.setBirthday(qquserinfo.getYear());
//            userInfo.setSex(qquserinfo.getGender());
//            ByteSource byteSourceSalt = ByteSource.Util.bytes(tempsalt);
//            authenticationInfo = new SimpleAuthenticationInfo(
//                    userInfo, //用户名
//                    newPassword, //密码
//                    byteSourceSalt,
//                    getName()  //realm name
//            );
            UserBean userInfo = userInfoService.selectUserByopenid(password);
            System.out.println("----->>userInfo=" + userInfo);
            if (userInfo == null) {
                return null;
            }

            ByteSource byteSourceSalt = ByteSource.Util.bytes(userInfo.getSalt());
            authenticationInfo = new SimpleAuthenticationInfo(
                    userInfo, //用户名
                    userInfo.getPassword(), //密码
                    byteSourceSalt,
                    getName()  //realm name
            );
        }else {

            UserBean userInfo = userInfoService.findUserInfoByName(username);
            System.out.println("----->>userInfo=" + userInfo);
            if (userInfo == null) {
                return null;
            }

            ByteSource byteSourceSalt = ByteSource.Util.bytes(userInfo.getSalt());
            authenticationInfo = new SimpleAuthenticationInfo(
                    userInfo, //用户名
                    userInfo.getPassword(), //密码
                    byteSourceSalt,
                    getName()  //realm name
            );
        }


        //mailService.sendLoginSafeMail(userInfo); //登录邮件

        return authenticationInfo;
    }




}