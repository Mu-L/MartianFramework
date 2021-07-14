package com.martian.starter.load;

import com.martian.annotation.MartianScan;
import com.martian.cache.MartianConfigCache;
import com.martian.config.MartianConfig;
import com.martian.config.model.FileUploadConfig;
import com.martian.config.model.RequestConfig;
import io.magician.Magician;
import io.magician.common.event.EventGroup;
import io.magician.tcp.TCPServerConfig;

import java.util.concurrent.Executors;

/**
 * 加载服务
 */
public class LoadServer {

    /**
     * 开始加载
     * @throws Exception
     */
    public static void load() throws Exception {

        /* 获取配置 */
        MartianConfig martianConfig = MartianConfigCache.getMartianConfig();
        FileUploadConfig fileUploadConfig = martianConfig.fileUploadConfig();
        RequestConfig requestConfig = martianConfig.requestConfig();

        /* 创建TCPServer配置 */
        TCPServerConfig tcpServerConfig = new TCPServerConfig();
        tcpServerConfig.setFileSizeMax(fileUploadConfig.getFileSizeMax());
        tcpServerConfig.setSizeMax(fileUploadConfig.getSizeMax());
        tcpServerConfig.setReadSize(requestConfig.getReadSize());

        EventGroup ioEventGroup = new EventGroup(1, Executors.newCachedThreadPool());
        EventGroup workerEventGroup = martianConfig.workerEventGroup();
        
        /* 创建服务 */
        Magician.createTCPServer(ioEventGroup, workerEventGroup)
                .config(tcpServerConfig)
                .scan(getPackage())
                .bind(martianConfig.port(), 1000);
    }

    /**
     * 获取要扫描的包
     * @return
     * @throws Exception
     */
    private static String getPackage() throws Exception {
        MartianScan martianScan = MartianConfigCache.getMartianScan();

        String[] packageList = martianScan.scanPackage();
        if(packageList == null || packageList.length < 1){
            throw new Exception("需要配置MartianScan注解的scanPackage属性");
        }
        StringBuffer packageNames = new StringBuffer();
        for(int i=0; i<packageList.length; i++){
            if(i > 0){
                packageNames.append(",");
            }
            packageNames.append(packageList[i]);
        }
        packageNames.append(",com.martian.starter.handler");
        return packageNames.toString();
    }
}
