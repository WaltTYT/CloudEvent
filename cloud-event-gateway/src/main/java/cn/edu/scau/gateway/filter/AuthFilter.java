package cn.edu.scau.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String KEY = "scau";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 放行登录和注册请求
        if (path.contains("/user/login") || path.contains("/user/register")) {
            return chain.filter(exchange);
        }

        // 放行静态资源
        if (path.startsWith("/repo/")) {
            return chain.filter(exchange);
        }

        // 验证 Token
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(token);

            // 将 Token 中的用户信息传递给下游服务
            String claims = JWT.decode(token).getClaim("claims").toString();
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Claims", claims)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JWTVerificationException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
