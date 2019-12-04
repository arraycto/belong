package com.belong.gateway.filters.global;

import com.belong.common.core.constant.Constants;
import com.belong.gateway.config.GatewayContext;
import com.belong.gateway.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;


/**
 * @Description: 全局拦截
 * @Author: fengyu
 * @CreateDate: 2019/9/17 9:33
 * @UpdateUser: fengyu
 * @UpdateDate: 2019/9/17 9:33
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
@Component
public class AllFilter implements GlobalFilter, Ordered {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 过滤的路径
     */
    private static final String[] SWAGGER_WHITE_LIST = {"/v2/api-docs", "/v2/api-docs-ext","/actuator","/test","/wxUserAuth","/db/"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest httpRequest = exchange.getRequest();
        String url = httpRequest.getURI().toString();
        log.info("请求方式：{},地址：{}", httpRequest.getMethod(), httpRequest.getURI().getPath());
        //将path和body缓存到GatewayContext
        //GatewayContext gatewayContext = new GatewayContext();
        //gatewayContext.setPath(httpRequest.getPath().pathWithinApplication().value());
        // 跳过不需要验证的路径
        if (Arrays.asList(SWAGGER_WHITE_LIST).contains(url)) {
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(Constants.AUTHORIZATION);
        //判断是否存在Token
        if (StringUtils.isBlank(token) || !token.startsWith(Constants.TOKEN_TYPE_BEARER)) {
            return ResponseUtil.setUnauthorizedResponse(exchange, 401, "请携带————》" + Constants.AUTHORIZATION);
        }
        exchange.getAttributes().put("startTime", System.currentTimeMillis());
        return returnMono(chain, exchange);
    }

    /**
     * 方法实现说明:计算校验时间
     *
     * @param chain
     * @param exchange
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @throws
     * @author fengyu
     * @date 2019/11/27 10:47
     */
    private Mono<Void> returnMono(GatewayFilterChain chain, ServerWebExchange exchange) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute("startTime");
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                log.info("处理参数耗时：{}ms", executeTime);
                log.info("状态码：{}", Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
            }
        }));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
