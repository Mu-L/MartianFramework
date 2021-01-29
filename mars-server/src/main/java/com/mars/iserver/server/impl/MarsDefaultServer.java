package com.mars.iserver.server.impl;

import com.mars.common.constant.MarsConstant;
import com.mars.common.constant.MarsSpace;
import com.mars.iserver.server.MarsServer;
import com.mars.iserver.server.helper.MarsHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 默认服务（采用NIO）
 */
public class MarsDefaultServer implements MarsServer {

    private Logger log = LoggerFactory.getLogger(MarsDefaultServer.class);

    /**
     * 开启服务
     *
     * @param portNumber
     */
    @Override
    public void start(int portNumber) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(portNumber));

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            /* 标识服务是否已经启动 */
            MarsSpace.getEasySpace().setAttr(MarsConstant.HAS_SERVER_START, "yes");
            log.info("启动成功");

            /* 开始监听 */
            doMonitor(selector);
        } catch (Exception e) {
            log.error("NIO发生异常", e);
        }
    }

    /**
     * 开始注册并监听
     *
     * @param selector
     * @throws Exception
     */
    private void doMonitor(Selector selector) throws Exception {
        while (true) {
            int eventCountTriggered = selector.select();
            if (eventCountTriggered == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();

                if(!selectionKey.isValid()){
                    continue;
                }

                if (selectionKey.isAcceptable()) {
                    MarsHttpHelper.acceptable(selector, selectionKey);
                } else if (selectionKey.isReadable()) {
                    MarsHttpHelper.read(selector, selectionKey);
                } else if(selectionKey.isWritable()){
                    MarsHttpHelper.write(selector, selectionKey);
                }
            }
        }
    }
}
