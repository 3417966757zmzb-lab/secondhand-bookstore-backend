package org.example.secondhandbookstore.common.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.secondhandbookstore.common.context.RequestIdContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
//把当前组件的优先级设为最高（数值最小，最先执行/最先排序)
@Order(Ordered.HIGHEST_PRECEDENCE)
public  class RequestIdFilter extends OncePerRequestFilter {



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().replace("-","");
        RequestIdContext.setRequestId(requestId);
        response.setHeader(RequestIdContext.RESPONSE_HEADER,requestId);
        try {
            filterChain.doFilter(request,response);
        }finally {
            RequestIdContext.removeRequestId();
        }

    }
}
