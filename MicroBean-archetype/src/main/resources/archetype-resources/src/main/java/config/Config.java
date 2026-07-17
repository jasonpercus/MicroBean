package ${package}.config;

import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Scope;
import java.time.LocalDateTime;

@Configuration
public class Config {

    @Bean(scope = Scope.PROTOTYPE, name = "currentDateTime")
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
}
