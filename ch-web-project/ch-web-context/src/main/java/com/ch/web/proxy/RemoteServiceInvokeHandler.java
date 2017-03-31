package com.ch.web.proxy;

import com.ch.frame.conf.ConfigHelper;
import com.ch.frame.util.SerializeUtils;
import com.ch.web.context.WebContext;
import com.ch.web.exception.WebException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RemoteServiceInvokeHandler implements InvocationHandler {
	protected Log log = LogFactory.getLog(this.getClass());
	private Class portType;
	private static CloseableHttpClient httpclient = null;
	private static synchronized CloseableHttpClient getClient(){
		if(httpclient == null){
			try {
				PoolingHttpClientConnectionManager pool =
						new PoolingHttpClientConnectionManager();
				pool.setMaxTotal(400);
				URL url = new URL(getRemoteUrl());
				pool.setMaxPerRoute(new HttpRoute(new HttpHost(url.getHost())), 200);
				httpclient = HttpClientBuilder.create().disableCookieManagement()
						.disableAuthCaching()
						.disableAutomaticRetries()						
						.setConnectionManager(pool).build();
			} catch (Exception e) {
				throw new WebException("Init http client error", e);
			}
		}
		return httpclient;
	}
	private static String getRemoteUrl(){
		return "http://" + ConfigHelper.getProp("service").get("serviceHost") + "/remote";
	}
	private CloseableHttpClient myclient;
	public RemoteServiceInvokeHandler(Class portType) {
		this.portType = portType;
		this.myclient = getClient();
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		WebContext ctx = WebContext.get();
		if(ctx == null){
			throw new WebException("not found web context in local thread");
		}
		if("toString".equals(method.getName())){
			return proxy;
		}
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("class", portType.getName());
		obj.put("method", method.getName());
		obj.put("args", args);
		obj.put("paramtypes", method.getParameterTypes());
		//obj.put("sid", ctx.getSessionId());
		obj.put("token", ctx.getToken());
		//调用远程接口
		
		HttpPost postMethod = new HttpPost(getRemoteUrl());
		ByteArrayEntity entry = new ByteArrayEntity(SerializeUtils.hessianSerialize(obj));
		postMethod.setEntity(entry);
		CloseableHttpResponse response = null;
		try {
			//long start = System.currentTimeMillis();
			response = myclient.execute(postMethod);
			//log.debug("execute "+method.getName()+":" + (System.currentTimeMillis()-start));
			int num = response.getStatusLine().getStatusCode();
			if(num >= 200 && num < 300){
				// 获取二进制的byte流
				Object r = SerializeUtils.hessianDeserialize(response.getEntity().getContent());
				if(r instanceof Map){
					Map rr = (Map)r;
					//{success:true/false,error:xxx,result:xxx}
					boolean success = (Boolean)rr.get("success");
					if(!success){
						String error = (String)rr.get("error");
						throw new WebException(StringUtils.isBlank(error)?"调用远程业务接口时，未知错误":error);
					}
					return rr.get("result");
				} else {
					throw new RemoteInvokeException("远程接口返回的数据不是有效的对象");
				}
			}else if(num == 404){
				throw new RemoteInvokeException("业务服务器地址不存在，当前地址" + getRemoteUrl());
			}else{
				throw new RemoteInvokeException("请求业务服务器失败，返回" + num);
			}
		} catch (Exception e) {
			log.error("Execute remote method error", e);
			throw e;
		} finally {
			postMethod.releaseConnection();
		}
	}
}