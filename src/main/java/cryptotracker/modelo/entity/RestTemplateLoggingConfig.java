package cryptotracker.modelo.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class RestTemplateLoggingConfig {

    @Bean
    @Primary
    public RestTemplate loggingRestTemplate() {
        RestTemplate rt = new RestTemplate();
        rt.setInterceptors(Collections.singletonList(loggingInterceptor()));
        return rt;
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            System.out.println("===== RestTemplate Request =====");
            System.out.println("URI:     " + request.getURI());
            System.out.println("Method:  " + request.getMethod());
            System.out.println("Headers: " + request.getHeaders());
            System.out.println("Request body:");
            System.out.println(new String(body, StandardCharsets.UTF_8));
            System.out.println("================================");
            return execution.execute(request, body);
        };
    }
}
