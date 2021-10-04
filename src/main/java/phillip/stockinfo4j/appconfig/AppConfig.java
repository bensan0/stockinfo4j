package phillip.stockinfo4j.appconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ResourceBundle;

/**
 * 需要初始化的Bean
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public ResourceBundle resource(){
        ResourceBundle resource = ResourceBundle.getBundle("setting");//test為屬性檔名，放在包com.mmq下，如果是放在src下，直接用test即可
        return resource;
    }
}
