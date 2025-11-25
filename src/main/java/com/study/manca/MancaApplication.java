package com.study.manca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MancaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MancaApplication.class, args);
    }

}
