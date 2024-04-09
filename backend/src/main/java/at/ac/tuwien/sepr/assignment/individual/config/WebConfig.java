package at.ac.tuwien.sepr.assignment.individual.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for web-related configurations.
 * This configuration effectively disables CORS; this is helpful during development but a bad idea in production
 */
@Profile("!prod")
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Configures CORS (Cross-Origin Resource Sharing) for all endpoints.
   * This method allows all methods (GET, POST, OPTIONS, HEAD, DELETE, PUT, PATCH) from all origins.
   *
   * @param registry CorsRegistry instance used for configuring CORS mappings.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods("GET", "POST", "OPTIONS", "HEAD", "DELETE", "PUT", "PATCH");
  }
}
