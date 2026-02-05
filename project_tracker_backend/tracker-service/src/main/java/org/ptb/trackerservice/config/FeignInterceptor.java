package org.ptb.trackerservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authorizationHeader = attributes.getRequest().getHeader("Authorization");
            if (authorizationHeader != null) {
                // Forward the JWT token to the next service
                template.header("Authorization", authorizationHeader);
            }
        }
    }
}
