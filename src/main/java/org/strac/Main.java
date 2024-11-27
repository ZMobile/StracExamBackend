package org.strac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.strac.api.config.StracExamSecurityConfig;
import org.strac.dao.config.StracExamDaoConfig;
import org.strac.service.config.StracExamServiceConfig;

@ComponentScan(
        basePackageClasses = {StracExamDaoConfig.class, StracExamServiceConfig.class, StracExamSecurityConfig.class},
        basePackages = {"org.strac.api.controller"}
)
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}