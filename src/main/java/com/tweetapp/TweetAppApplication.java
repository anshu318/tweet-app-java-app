package com.tweetapp;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class TweetAppApplication {

	
    public static void main(String[] args) {
        log.info("Initialization");
        TimeZone.setDefault(TimeZone.getDefault());
        SpringApplication.run(TweetAppApplication.class, args);
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/v1.0/**").allowedOrigins("*")
              .allowedMethods("*");
        }
      };
    }

}
