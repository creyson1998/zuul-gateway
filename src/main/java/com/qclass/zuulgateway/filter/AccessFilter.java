package com.qclass.zuulgateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.qclass.authcenter.modle.AuthenUser;
import com.qclass.authcenter.rpc.CheckTokenService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by fanwenwen on 2018/6/5.
 */
@Service
public class AccessFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

    @Autowired
    private FilterConfig filterConfig;

    @Autowired
    private CheckTokenService verifyTokenService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        //获取可以跳过路由的路径
        String ignoresParam = filterConfig.getIgnores();
        String[] ignoreArray = ignoresParam.split(",");
        //对输入的路径进行处理，拿去验证中心去进行权限判断
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String template = request.getRequestURI();
        String[] uriArray = template.split("/");
        String server =  uriArray[1];
        StringBuffer stringBuffer = new StringBuffer();
        for(int j = 2;j < uriArray.length; j++){
            stringBuffer.append("/");
            stringBuffer.append(uriArray[j]);
        }
        String url = stringBuffer.toString();

        //打印用户请求日志
        log.info("=========="+String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));
        //先进行token筛选
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("token")){
                    token = cookie.getValue();
                    break;
                }
            }
        }
        //标志是否是不需验证的URL
        boolean flag = false;
        for(int i = 0;i < ignoreArray.length; i++){
            if(request.getRequestURL().toString().contains(ignoreArray[i])){
                flag = true;
            }
        }
        AuthenUser authenUser = null;
        if(!flag && token == null){
            log.info("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("token is empty");
            return null;
        }
        else if(!flag && token != null){
            try {
                   authenUser = verifyTokenService.test(token,url,server);
                   if(authenUser.getAuthStatus() != 1){
                       log.info("filter =========失败" + authenUser.getAuthMessage());
                       ctx.setSendZuulResponse(false);
                       ctx.setResponseStatusCode(401);
                       ctx.setResponseBody(authenUser.getAuthMessage());
                       return null;
                    }
                }
             catch (Exception e) {
                 log.info("filter =========验证token失败");
                 ctx.setSendZuulResponse(false);
                 ctx.setResponseStatusCode(401);
                 ctx.setResponseBody("系统出现异常");
                 return null;
            }
        }
        //验证成功，进行放行
        ctx.setSendZuulResponse(true);// 对该请求进行路由
        ctx.setResponseStatusCode(200);
        log.info("access token ok");
        if(authenUser != null){

            log.info(authenUser.toString());
        }
        return null;
    }

}

