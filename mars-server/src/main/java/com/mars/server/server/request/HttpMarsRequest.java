package com.mars.server.server.request;

import com.mars.server.server.request.model.MarsFileUpLoad;
import java.util.*;

/**
 * 请求对象
 * @author yuye
 *
 */
public abstract class HttpMarsRequest {

	/**
	 * 参数
	 */
	private Map<String,List<String>> marsParams;

	/**
	 * json参数
	 */
	private String jsonParam;

	/**
	 * 上传的文件
	 */
	private Map<String, MarsFileUpLoad> files;

	/**
	 * 获取java原生request
	 * @return 原生请求对象
	 */
	public abstract <T> T getNativeRequest(Class<T> cls);

	/**
	 * 添加上传的文件
	 * @param files
	 */
	public void setFiles(Map<String,MarsFileUpLoad> files){
		this.files = files;
	}

	/**
	 * 获取json传参
	 * @return json参数
	 */
	public String getJsonParam() {
		return jsonParam;
	}

	/**
	 * 设置json传参
	 * @param jsonParam
	 */
	public void setJsonParam(String jsonParam) {
		this.jsonParam = jsonParam;
	}

	/**
	 * 获取参数类型
	 * @return 参数类型
	 */
	public abstract String getContentType();

	/**
	 * 设置参数
	 * @param params
	 */
	public void setParams(Map<String, List<String>> params) {
		if(params == null || params.size() < 1){
			return;
		}
		this.marsParams = params;
	}

	/**
	 * 获取请求方法
	 * @return 请求方法
	 */
	public abstract String getMethod();

	/**
	 * 获取要请求的uri
	 * @return 请求方法
	 */
	public abstract String getUrl();
	
	/**
	 * 获取请求头数据
	 * @param key 键
	 * @return 头数据
	 */
	public abstract String getHeader(String key);

	/**
	 * 获取请求的参数集
	 * @return 请求参数
	 */
	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<>();
		if(marsParams == null){
			return params;
		}
		for(String key : marsParams.keySet()){
			List<String> paramsList = marsParams.get(key);
			if(paramsList == null || paramsList.size() < 1){
				continue;
			}
			String[] paramsListToArray = paramsListToArray(paramsList);
			if(paramsListToArray != null && paramsListToArray.length == 1){
				params.put(key,paramsListToArray[0]);
			} else {
				params.put(key,paramsListToArray);
			}
		}
		return params;
	}

	/**
	 * 获取单个请求的参数
	 * @param key 键
	 * @return 请求参数
	 */
	public String getParameter(String key) {
		if(marsParams != null){
			List<String> value = marsParams.get(key);
			if(value != null && value.size() > 0){
				return value.get(0);
			}
		}
		return null;
	}

	/**
	 * 获取单个请求的参数
	 * @param key 键
	 * @return 请求参数
	 */
	public String[] getParameterValues(String key) {
		if(marsParams != null) {
			List<String> paramsList = marsParams.get(key);
			return paramsListToArray(paramsList);
		}
		return null;
	}

	/**
	 * 获取请求的文件
	 * @return 文件列表
	 */
	public Map<String, MarsFileUpLoad> getFiles() throws Exception {
		return files;
	}

	/**
	 * 获取单个请求的文件
	 *
	 * @param name 名称
	 * @return 单个文件
	 */
	public MarsFileUpLoad getFile(String name) throws Exception {
		if (files != null){
			return files.get(name);
		}
		return null;
	}
	
	/**
	 * 获取客户端InetSocketAddress
	 * @return inetSocketAddress
	 */
	public abstract String getInetSocketAddress();

	/**
	 * 参数集合转String[]
	 * @param paramsList
	 * @return
	 */
	private String[] paramsListToArray(List<String> paramsList){
		if(paramsList == null || paramsList.size() < 1){
			return null;
		}
		String[] paramsArray = new String[paramsList.size()];
		return paramsList.toArray(paramsArray);
	}
}
