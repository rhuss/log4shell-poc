package knativecon2023.zerotrust;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@SpringBootApplication
public class WebApp extends HandlerInterceptorAdapter implements WebMvcConfigurer {

    public static final Logger logger = LogManager.getLogger(WebApp.class);

    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }

    // Interceptor used for logging HTTP access:
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
         // Innocent log statement that might trigger the exploit with a carefully prepared user agent header
        logger.info("User-agent: " + request.getHeader("User-Agent"));
        logger.info("Request: " + request.getRequestURI());
        return true;
    }

    // Add the interceptor for logging the requests
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    // Add resource handler so that static files are not cached by the browser
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache());
    }


}
