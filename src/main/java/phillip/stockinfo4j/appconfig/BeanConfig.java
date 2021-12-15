package phillip.stockinfo4j.appconfig;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;

/**
 * 需要初始化的Bean
 */
@Configuration
public class BeanConfig {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean(name = "yyyyMMddFormatter")
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyyMMdd");
    }

    @Component
    @ConfigurationProperties(prefix = "setting")
    @PropertySources({
            @PropertySource("classpath:setting.properties"),
            //外部化設定時用
            @PropertySource(value = "file:config/setting.properties", ignoreResourceNotFound = true)

    })
    @Data
    public class Setting {
        private String TWSEStockDailyUrl;
        private String TWSECorpDailyUrl;
        private String TPEXStockDailyUrl;
        private String TPEXCorpDailyUrl;
        private String DistributionUrl;
        private String TempDir;
        private String StockOtherInfoUrl;
        private String StockTPEXListedUrl;
        private String StockTWSEListedUrl;
        private String StockEmergingListedUrl;
    }
}
