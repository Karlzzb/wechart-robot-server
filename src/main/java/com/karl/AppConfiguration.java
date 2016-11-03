package com.karl;

import java.util.ResourceBundle;

import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.karl.fx.SpringFXMLLoader;
import com.karl.fx.StageManager;
import com.karl.service.WebWechat;

@Configuration
@EntityScan(basePackages = {"com.karl.db.domain"})
@EnableJpaRepositories(basePackages = {"com.karl.db.repositories"})
@EnableTransactionManagement
@Import({
    DispatcherServletAutoConfiguration.class,
    EmbeddedServletContainerAutoConfiguration.class,
    ErrorMvcAutoConfiguration.class,
    HttpEncodingAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class,
    JacksonAutoConfiguration.class,
    ServerPropertiesAutoConfiguration.class,
    PropertyPlaceholderAutoConfiguration.class,
    ThymeleafAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
})
public class AppConfiguration {
    @Autowired
    SpringFXMLLoader springFXMLLoader;
    
    @Autowired
    WebWechat webWechat;
    
    
//    @Autowired
//    @Lazy(value = true)
//    PcClient pcClient;

    @Bean
    @Lazy(value = true)
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("Bundle");
    }

    @Bean
    @Lazy(value = true)
    // stage only created after Spring context bootstrap
    public StageManager stageManager(Stage stage) {
        return new StageManager(stage, springFXMLLoader, webWechat);
    }
    
//    @Bean
//    ServletRegistrationBean h2servletRegistration() {
//     	WebServlet h2Web = new WebServlet();
//       	ServletRegistrationBean registrationBean = new ServletRegistrationBean(h2Web);
//    	registrationBean.addUrlMappings("/console/*");
//    	return registrationBean;
//    }

}
