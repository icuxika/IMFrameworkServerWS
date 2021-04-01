package com.icuxika.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATTRIBUTE_API_HEADER = "API_HEADER";

    public static Map<String, Integer> HANDSHAKE_MAP = new ConcurrentHashMap<>();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 尝试从请求头中获取相关数据
        ApiHeader apiHeader = resolveRequestParameters(request.getHeaders());
        if (!apiHeader.isValid()) {
            // JavaScript请求，尝试解析请求参数
            apiHeader = resolveJSRequestParameters(request);
            if (!apiHeader.isValid()) {
                return false;
            }
        }

        attributes.put(ATTRIBUTE_API_HEADER, apiHeader);

        // 握手之前
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手之后
    }

    /**
     * 解析一般请求数据
     */
    private ApiHeader resolveRequestParameters(HttpHeaders headers) {
        ApiHeader apiHeader = new ApiHeader();

        String token = headers.getOrEmpty("token").stream().findFirst().orElse(null);
        String clientType = headers.getOrEmpty("clientType").stream().findFirst().orElse(null);
        String userId = headers.getOrEmpty("userId").stream().findFirst().orElse(null);

        if (token != null) {
            apiHeader.setToken(token);
        }

        if (clientType != null) {
            apiHeader.setClientType(Integer.valueOf(clientType));
        }

        if (userId != null) {
            apiHeader.setUserId(Long.valueOf(userId));
        }
        return apiHeader;
    }

    /**
     * 解析JavaScript端传来的请求数据
     */
    private ApiHeader resolveJSRequestParameters(ServerHttpRequest request) {
        ApiHeader apiHeader = new ApiHeader();

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            apiHeader.setToken(httpServletRequest.getParameter("token"));
            apiHeader.setClientType(Integer.valueOf(httpServletRequest.getParameter("clientType")));
            apiHeader.setUserId(Long.valueOf(httpServletRequest.getParameter("userId")));

            // 如果无效尝试从header中获取
            if (!apiHeader.isValid()) {
                apiHeader.setToken(httpServletRequest.getHeader("token"));
                apiHeader.setClientType(Integer.valueOf(httpServletRequest.getHeader("clientType")));
                apiHeader.setUserId(Long.valueOf(httpServletRequest.getHeader("userId")));
            }
        }

        return apiHeader;
    }
}
