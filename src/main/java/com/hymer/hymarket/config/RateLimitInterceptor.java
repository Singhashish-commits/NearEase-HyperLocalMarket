package com.hymer.hymarket.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor  implements HandlerInterceptor {
    private final RedissonClient redissonClient;
    @Autowired
    public RateLimitInterceptor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String clientId;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email =  authentication.getName();
            clientId = "user:" + email;

        }
        else {
            clientId = "ip:" + getClientIP(request);
        }
                String limiterKey= "rate_limiter:" + clientId;
                RRateLimiter rateLimiter = redissonClient.getRateLimiter(limiterKey);
                rateLimiter.setRate(RateType.OVERALL, 100, 1, RateIntervalUnit.SECONDS);
                boolean isAllowed = rateLimiter.tryAcquire(1);
        if (!isAllowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return false;
        }
        return true;



    }



    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("x-forwarded-for");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            xfHeader = request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }

}
