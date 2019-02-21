package com.peterpan.weixin.pushmessage;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class PeterPanWeixinPushMessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeterPanWeixinPushMessageApplication.class, args);
	}
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("com.peterpan.weixin.pushmessage"))              
          .paths(PathSelectors.any())            //PathSelectors.ant("/api/*")               
          .build()
          .apiInfo(apiInfo());                                           
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfo(
          "Peter Pan Weixin API", 
          "Push Message API", 
          "v1.0", 
          "panjianlong13@gmail.com", 
          new Contact("Peter Pan", "https://blog.csdn.net/panjianlongWUHAN", "panjianlong13@gmail.com"), 
          "License of API", "API license URL", Collections.emptyList());
   }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
