package com.mars.iserver.server.model;

import com.mars.common.base.config.MarsConfig;
import com.mars.common.base.config.model.CrossDomainConfig;
import com.mars.common.constant.MarsConstant;
import com.mars.common.util.MarsConfiguration;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 请求处理器的父类
 * 因为成员变量太多，所以放了一部分到这里
 */
public class MarsHttpExchangeModel {

    protected SelectionKey selectionKey;

    protected Selector selector;

    protected SocketChannel socketChannel;

    /**
     * 请求的地址
     */
    protected RequestURI requestURI;

    /**
     * 请求内容
     */
    protected InputStream requestBody;

    /**
     * 要发送的字符串
     */
    protected String sendText;

    /**
     * 响应文件流
     */
    protected byte[] responseBody;

    /**
     * 请求头
     */
    protected HttpHeaders requestHeaders;

    /**
     * 响应头
     */
    protected HttpHeaders responseHeaders;

    /**
     * 请求方法
     */
    protected String requestMethod;

    /**
     * HTTP版面
     */
    protected String httpVersion;

    /**
     * 响应状态
     */
    protected int statusCode;

    /**
     * 构造器
     */
    public MarsHttpExchangeModel(){
        requestHeaders = new HttpHeaders();
        responseHeaders = new HttpHeaders();

        responseHeaders.put(MarsConstant.CONTENT_TYPE, "text/json;charset="+ MarsConstant.ENCODING);
        crossDomain();

        sendText = MarsConstant.VOID;
        statusCode = 200;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * 获取请求路径
     * @return
     */
    public RequestURI getRequestURI() {
        return requestURI;
    }

    /**
     * 设置请求头
     * @param name
     * @param value
     */
    public void setRequestHeader(String name, String value){
        requestHeaders.put(name, value);
    }

    /**
     * 设置响应头
     * @param name
     * @param value
     */
    public void setResponseHeader(String name, String value){
        responseHeaders.put(name, value);
    }

    /**
     * 获取请求头
     * @return
     */
    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * 获取响应头
     * @return
     */
    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * 获取http版本
     * @return
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     * 获取请求方法
     * @return
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * 获取请求内容
     * @return
     */
    public InputStream getRequestBody() {
        return requestBody;
    }

    /**
     * 设置响应文件流
     * @param responseBody
     */
    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * 设置响应文件流
     * @param inputStream
     * @throws Exception
     */
    public void setResponseBody(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while((rc=inputStream.read(buff, 0, 100))>0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        this.responseBody = byteArrayOutputStream.toByteArray();
    }

    /**
     * 设置响应数据
     * @param statusCode
     * @param text
     */
    public void sendText(int statusCode, String text){
        this.statusCode = statusCode;
        this.sendText = text;
    }

    /**
     * 设置跨域
     */
    private void crossDomain() {
        MarsConfig marsConfig = MarsConfiguration.getConfig();
        CrossDomainConfig crossDomainConfig = marsConfig.crossDomainConfig();
        setResponseHeader("Access-Control-Allow-Origin", crossDomainConfig.getOrigin());
        setResponseHeader("Access-Control-Allow-Methods", crossDomainConfig.getMethods());
        setResponseHeader("Access-Control-Max-Age", crossDomainConfig.getMaxAge());
        setResponseHeader("Access-Control-Allow-Headers", crossDomainConfig.getHeaders());
        setResponseHeader("Access-Control-Allow-Credentials", crossDomainConfig.getCredentials());
    }
}
