package com.ch.web.context;

import com.ch.frame.Globals;
import com.ch.web.exception.WebException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class WebContext {
    private static ThreadLocal<WebContext> cache = new ThreadLocal<>();

    public static WebContext get() {
        WebContext c = cache.get();
        return c;
    }

    public static void set(WebContext ctx) {
        cache.set(ctx);
    }

    public String getSessionId() {
        return sessionId;
    }

    public static void remove() {
        cache.remove();
    }

    //当前会话ID
    private String sessionId;
    //当前会话token
    private String token;
    private HttpServletRequest request;
    //当前域名
    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /*public WebContext(String sid, HttpServletRequest request) {
        this.sessionId = sid;
        if (StringUtils.isBlank(sid)) {
            throw new WebException("Session id is null");
        }
        this.request = request;
    }*/

    public WebContext(String token, HttpServletRequest request) {
        this.token = token;
        if (StringUtils.isBlank(token)) {
            throw new WebException("Token is null");
        }
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 从session中获取
     *
     * @param key
     * @return
     */
    public <T> T getSession(String key) {
        /*SessionMap sessionMap = SessionManager.getGroup().getSessionData(sessionId);
        if (sessionMap != null) {
            return (T) sessionMap.getAttribute(key);
        }
        return null;*/
    	return (T)request.getSession().getAttribute(key);
    }

    public void setSession(String key, Serializable obj) {
        /*SessionMap sessionMap = SessionManager.getGroup().getSessionData(sessionId);
        if (sessionMap != null) {
            sessionMap.setAttribute(key, obj);
            SessionCacheManager.getSessionCache().put(sessionId, sessionMap, 60000);
        }*/
    	request.getSession().setAttribute(key, obj);
    }

    public void removeSession(String key) {
        //SessionManager.getGroup().remove(sessionId, key);
    	request.getSession().removeAttribute(key);
    }

    /**
     * 清空缓存，解决sessionId为空时清除其他用户缓存的问题
     */
    public void clearSession() {
        /*if (StringUtils.isNotBlank(sessionId)) {
            SessionManager.getGroup().clear(sessionId);
        }*/
    	request.getSession().invalidate();
    }


    /**
     * 获取当前操作员的ID
     *
     * @return
     */
    public Long getUserId() {
        return getSession(Globals.USERID);
    }

    /**
     * 获取当前机构编号
     *
     * @return
     */
    public Long getOrgId() {
        return getSession(Globals.ORGID);
    }

    /**
     * 获取所有session数据
     *
     * @return
     */
    /*public SessionMap getSessionData() {
        return SessionManager.getGroup().getSessionData(sessionId);
    }*/
    public Map<String, Object> getAllSessionData(){
    	Enumeration<String> em = request.getSession().getAttributeNames();
    	Map<String, Object> map = new HashMap<>();
    	while(em.hasMoreElements()){
    		String key = em.nextElement();
    		map.put(key, request.getSession().getAttribute(key));
    	}
    	return map;
    }

    /**
     * 获取spring的bean
     *
     * @param cla
     * @return
     */
    public <T> T getBean(Class<T> cla) {
    	return (T)MyDispatchServlet.springcontext.getBean(cla);
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public String getBaseUrl() {
        String baseurl = request.getContextPath();
        if (baseurl == null || baseurl.equals("/")) {
            baseurl = "";
        }
        return baseurl;
    }

	public ServletContext getServletContext() {
		return request.getSession().getServletContext();
	}

	public String getUrl(String url) {
		if(url == null)
			return getBaseUrl();
		if(url.startsWith("http:") || url.startsWith("https:"))
			return url;
		if(!url.startsWith("/"))
			url = "/" + url;
		return getBaseUrl() + url;
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
