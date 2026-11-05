package org.example.secondhandbookstore.common.context;

import org.slf4j.MDC;

public final class RequestIdContext {
    public final static String MDC_KEY = "requestId";
    public final static String RESPONSE_HEADER = "X-Request-Id";

    //空参防止实例化
    private RequestIdContext() {

    }
    public static void setRequestId(String requestId) {
        MDC.put(MDC_KEY, requestId);
    }

    public static String getRequestId() {
        return MDC.get(MDC_KEY);
    }

    public static void removeRequestId() {
        MDC.remove(MDC_KEY);
    }
}
