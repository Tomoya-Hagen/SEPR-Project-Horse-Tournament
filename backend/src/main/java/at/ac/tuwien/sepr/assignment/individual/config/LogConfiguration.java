package at.ac.tuwien.sepr.assignment.individual.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Configuration class for logging within the application.
 * This class defines a filter bean to log incoming requests.
 */
@Configuration
public class LogConfiguration {

  @Bean
  public FilterRegistrationBean<OncePerRequestFilter> logFilter() {
    var reg = new FilterRegistrationBean<OncePerRequestFilter>(new LogFilter());
    reg.addUrlPatterns("/*");
    reg.setName("logFilter");
    reg.setOrder(Ordered.LOWEST_PRECEDENCE);
    return reg;
  }
}
