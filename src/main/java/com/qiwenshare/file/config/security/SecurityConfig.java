package com.qiwenshare.file.config.security;

import com.qiwenshare.file.config.security.entrypoint.JwtAuthenticationEntryPoint;
import com.qiwenshare.file.config.security.filter.JwtAuthenticationTokenFilter;
import com.qiwenshare.file.config.security.filter.UrlFilterInvocationSecurityMetadataSource;
import com.qiwenshare.file.config.security.handle.JwtAccessDeniedHandler;
import com.qiwenshare.file.config.security.manager.UrlAccessDecisionManager;
import com.qiwenshare.file.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    JwtAuthenticationTokenFilter jwtRequestFilter; // Jwt拦截器，获取用户的数据和用户拥有的权限
    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;    // 无权访问返回的 JSON 格式数据给前端（否则为 403 html 页面）

    @Autowired
    UserService userService;
    @Autowired
    UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    @Autowired
    UrlAccessDecisionManager urlAccessDecisionManager;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //禁用 CSRF
                .csrf().disable()

                // 授权异常
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 防止iframe 造成跨域
                .and()
                .headers()
                .frameOptions()
                .disable()

                // 不创建会话
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                .and()
                .authorizeRequests()

                // 放行静态资源
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/webSocket/**"
                ).permitAll()


                // 放行swagger
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/*/api-docs").permitAll()
                .antMatchers("/h2-console").permitAll()
                .antMatchers("/h2-console/**").permitAll()

                // 放行文件访问
                .antMatchers(HttpMethod.GET, "/upload/**").permitAll()

                // 放行druid
                .antMatchers("/druid/**").permitAll()

                // 放行OPTIONS请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //允许匿名及登录用户访问
                .antMatchers("/user/register",
                        "/user/login",
                        "/user/checkuserlogininfo",
                        "/filetransfer/downloadfile",
                        "/filetransfer/preview",
                        "/share/sharefileList",
                        "/share/sharetype",
                        "/share/checkextractioncode",
                        "/share/checkendtime",
                        "/error/**").permitAll()

//                .antMatchers(HttpMethod.GET, "/essaysort/**", "/essay/**", "/remark/**", "/user/**").permitAll()
//                .antMatchers("/**").permitAll()
                // 所有请求都需要认证
                .anyRequest().authenticated()
                .withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor());


        // 禁用缓存
        httpSecurity.headers().cacheControl();


        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);//添加JWT身份认证的filter
    }

    /**
     * 自定义 FilterSecurityInterceptor  ObjectPostProcessor 以替换默认配置达到动态权限的目的
     *
     * @return ObjectPostProcessor
     */
    private ObjectPostProcessor<FilterSecurityInterceptor> filterSecurityInterceptorObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                object.setAccessDecisionManager(urlAccessDecisionManager);
                object.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
                return object;
            }
        };
    }

}