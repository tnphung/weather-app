package weatherapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Value("${openweathermap.url}")
    private String weatherUrl;

    @Bean
    public WebClient webClient() {

        WebClient webClient = WebClient.builder()
                                       .baseUrl(weatherUrl)
                                       .defaultCookie("cookie-name", "cookie-value")
                                       .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                       .build();
        return webClient;
    }
}
