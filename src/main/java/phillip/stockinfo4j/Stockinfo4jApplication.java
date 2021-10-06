package phillip.stockinfo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ResourceBundle;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class Stockinfo4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(Stockinfo4jApplication.class, args);
    }


}
