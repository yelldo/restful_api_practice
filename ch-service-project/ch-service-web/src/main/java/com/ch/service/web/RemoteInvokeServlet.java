package com.ch.service.web;

import com.ch.frame.util.SerializeUtils;
import com.ch.service.context.ServiceContext;
import com.ch.service.util.ServiceExcepiton;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RemoteInvokeServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Log log = LogFactory.getLog(this.getClass());
    private ApplicationContext springctx = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // config.getServletContext() 取到的是：
        /*<context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:/prod/service_spring.xml</param-value>
        </context-param>*/
        springctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        ServiceContext ctx = new ServiceContext();
        Map<String, Object> result = new HashMap<>();
        try {
            ServiceContext.set(ctx);
            Object obj = SerializeUtils.hessianDeserialize(req.getInputStream());
            if (!(obj instanceof Map)) {
                throw new ServiceException("请求参数类型不正确");
            }
            Map<String, Object> param = (Map<String, Object>) obj;
            //检验请求中是否带有token
            String token = (String) param.get("token");
            if (StringUtils.isBlank(token)) {
                throw new ServiceException("token为空");
            }
            // TODO 检查参数中是否带有时间戳
            /*if (!SessionManager.getGroup().validateToken(sid)) {
                throw new ServiceException("无效会话");
            }*/
            ctx.setToken(token);
            //类
            Class port = Class.forName((String) param.get("class"));
            Class[] types = (Class[]) param.get("paramtypes");
            Method method = port.getMethod((String) param.get("method"), types);
            Object[] args = (Object[]) param.get("args");
            log.debug("Begion invoke service " + ((String) param.get("class")) + "." + ((String) param.get("method"))
                + ", args:" + ArrayUtils.toString(args, "[]"));
            Object target = springctx.getBean(port);
            Object robj = method.invoke(target, args);
            result.put("result", robj);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", getErrorMessage(e));
            log.error("Invoke remote service error", e);
        } finally {
            ServiceContext.remove();
            SerializeUtils.hessianSerialize(result, resp.getOutputStream());
        }
    }

    private String getErrorMessage(Throwable e) {
        if (e instanceof ServiceExcepiton) {
            return ((ServiceExcepiton) e).getMessage();
        } else if (e.getCause() != null) {
            return getErrorMessage(e.getCause());
        }
        return e.getMessage();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
