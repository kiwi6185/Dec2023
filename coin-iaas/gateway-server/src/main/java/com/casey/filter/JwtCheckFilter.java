package com.casey.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HttpHeaders;
import io.netty.util.internal.StringUtil;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${no.require.uris:/admin/login}")   // 放行
    private Set<String> noRequireTokenUris;
    /**
     * 过滤器拦截到用户请求后做啥
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 该接口是否需要token才能访问
        if(!isRequireToken(exchange)) {
            return chain.filter(exchange);  // 不需要token，直接放行
        }
        // 2. 取出用户token
        String token = getUserToken(exchange);
        // 3. 判断用户token是否有效
        if(StringUtil.isNullOrEmpty(token)) {
            return buildNoAuthorizationResult(exchange);
        }
        Boolean hasKey = redisTemplate.hasKey(token);
        if(hasKey != null && hasKey) {
            return chain.filter(exchange);  // token有效，放行
        }
        return buildNoAuthorizationResult(exchange);    // token无效
    }

    /**
     * 给用户响应一个没有token的错误
     * @param exchange
     * @return
     */
    private Mono<Void> buildNoAuthorizationResult(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set("Content-Type", "application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "NO Authorization");
        jsonObject.put("errorMsg", "Token is Null or Error");
        DataBuffer wrap = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
        return response.writeWith(Flux.just(wrap));
    }

    /**
     * 从请求头获取用户token
     * @param exchange
     * @return
     */
    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token == null ? null : token.replace("bearer ", "");
    }

    /**
     * 判断该接口是否需要token
     * @param exchange
     * @return
     */
    private boolean isRequireToken(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if(noRequireTokenUris.contains(path)){
            return false;   // 不需要token
        }
        return Boolean.TRUE;
    }

    /**
     * 拦截器顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
