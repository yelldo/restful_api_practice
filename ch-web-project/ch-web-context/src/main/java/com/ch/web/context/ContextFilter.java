package com.ch.web.context;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

public class ContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        //获取或生成sessionid
        HttpServletRequest req = (HttpServletRequest) request;
        //处理模板静态资源，以/tpl模板文件请求
        /*String path = req.getRequestURI();
        String contextpath = req.getContextPath();
        if (contextpath == null || contextpath.equals("/")) {
            contextpath = "";
        }
        path = path.substring(contextpath.length());
        if (path.startsWith("/res/") || path.equals("/index.html")) {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                IOUtils.copy(is, response.getOutputStream());
            } else {
                is = new FileInputStream(new File(req.getSession().getServletContext().getRealPath("") + "/" + path));
                if (is != null) {
                    IOUtils.copy(is, response.getOutputStream());
                }
            }
            return;
        }*/
        //初始化session及上下文对象
        //WebContext ctx = new WebContext(req.getSession().getId(), req);
        String token = request.getParameter("token");
        WebContext ctx = new WebContext(token, req);
        try {
            String host = req.getRemoteHost(); //发出请求的客户机的完整主机名（域名）
            /*host = req.getScheme() + "://" + host;  // http or https
            if (req.getRemotePort() != 80 && host.startsWith("http:")) {
                host = host + ":" + req.getRemotePort(); // 客户机所使用的网络端口号
            } else if (req.getRemotePort() != 443 && host.startsWith("https:")) {
                host = host + ":" + req.getRemotePort();
            }*/
            ctx.setDomain(host);
            WebContext.set(ctx);
            chain.doFilter(request, response);
        } finally {
            //回收
            WebContext.remove();
        }
    }

    /**
     * 日志
     *
     * @param request
     */
    private void log(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        for (String key : map.keySet()) {

        }
    }

    /**
     * 处理SESSION验证失败，转向到登录，考虑AJAX，API，GET，POST之类的请求形式
     *
     * @param request
     * @param response
     */
    private void parseValidateSessionError(HttpServletRequest request, HttpServletResponse response) {
        if (isAjax(request) || isAPI(request)) {
            //输出json
            JSONObject responseJSONObject = new JSONObject();
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                responseJSONObject.put("code", -1);
                responseJSONObject.put("msg", "您没有登录或者长期不没有操作，请重新登录");
                responseJSONObject.put("data", "");
                out = response.getWriter();
                out.append(responseJSONObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } else {
            //转发到授权登录页面
            try {
                response.sendRedirect(request.getContextPath() + "/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 判断ajax请求
     *
     * @param request
     * @return
     */
    private boolean isAjax(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString()));
    }

    /**
     * 判定是否是api请求
     *
     * @param request
     * @return
     */
    private boolean isAPI(HttpServletRequest request) {
        return (request.getHeader("isapi") != null && "true".equals(request.getHeader("isapi").toString()));
    }
}
