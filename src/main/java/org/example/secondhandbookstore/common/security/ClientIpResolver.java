package org.example.secondhandbookstore.common.security;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
//获取客户端ip
public class ClientIpResolver {
    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final boolean trustForwardedHeaders;

    public ClientIpResolver(
            @Value("${app.client-ip.trust-forwarded-headers:false}") boolean trustForwardedHeaders) {
        this.trustForwardedHeaders = trustForwardedHeaders;
    }
    public String resolve(HttpServletRequest request) {
        if (!trustForwardedHeaders) {
            return request.getRemoteAddr();
        }
        String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER);
        if (forwardedFor == null || forwardedFor.isBlank()) {
            return request.getRemoteAddr();
        }
        // XFF 可能是 "客户端, 代理1, 代理2"，最左边才是原始客户端
        String clientIp = forwardedFor.split(",")[0].trim();
        return clientIp.isEmpty() ? request.getRemoteAddr() : clientIp;
    }
}
