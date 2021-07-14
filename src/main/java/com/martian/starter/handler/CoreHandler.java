package com.martian.starter.handler;

import com.magician.web.MagicianWeb;
import com.martian.cache.MartianConfigCache;
import com.martian.config.MartianConfig;
import com.martian.config.model.CrossDomainConfig;
import io.magician.common.annotation.TCPHandler;
import io.magician.tcp.codec.impl.http.request.MagicianRequest;
import io.magician.tcp.handler.TCPBaseHandler;

/**
 * 核心控制器
 */
@TCPHandler(path = "/")
public class CoreHandler implements TCPBaseHandler<MagicianRequest> {

    /**
     * 配置
     */
    private MartianConfig martianConfig = MartianConfigCache.getMartianConfig();


    @Override
    public void request(MagicianRequest magicianRequest) {

        CrossDomainConfig crossDomainConfig = martianConfig.crossDomainConfig();

        magicianRequest.getResponse().setResponseHeader("Access-Control-Allow-Origin", crossDomainConfig.getOrigin());
        magicianRequest.getResponse().setResponseHeader("Access-Control-Allow-Methods", crossDomainConfig.getMethods());
        magicianRequest.getResponse().setResponseHeader("Access-Control-Max-Age", crossDomainConfig.getMaxAge());
        magicianRequest.getResponse().setResponseHeader("Access-Control-Allow-Headers", crossDomainConfig.getHeaders());
        magicianRequest.getResponse().setResponseHeader("Access-Control-Allow-Credentials", crossDomainConfig.getCredentials());

        MagicianWeb.createWeb().request(magicianRequest);
    }
}
