package com.mac.scp.config.shiro;

import com.mac.scp.domain.Permission;
import com.mac.scp.domain.Role;
import com.mac.scp.domain.UserBean;
import com.mac.scp.service.UserService;
import org.apache.shiro.SecurityUtils;
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
        //设置登录过期时间，永不过期
        SecurityUtils.getSubject().getSession().setTimeout(-1000L);
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());

        UserBean qquserinfo = qqLoginInfoMap.get(username + password);
        SimpleAuthenticationInfo authenticationInfo = null;
        if (qquserinfo != null){ //qq登录
            qqLoginInfoMap.remove(username + password);
            UserBean userInfo = userInfoService.selectUserByopenid(password);

            if (userInfo == null) {
                return null;
            }

            ByteSource byteSourceSalt = ByteSource.Util.bytes(userInfo.getSalt());
            authenticationInfo = new SimpleAuthenticationInfo(
                    userInfo, //用户名
                    userInfo.getQqpassword(), //密码
                    byteSourceSalt,
                    getName()  //realm name
            );
        }else {

            UserBean userInfo = userInfoService.findUserInfoByTelephone(username);

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