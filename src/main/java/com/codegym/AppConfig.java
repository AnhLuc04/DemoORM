package com.codegym;


import com.codegym.service.CustomerService;
import com.codegym.service.iml.SimpleCustomerServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

    @Configuration
    @EnableWebMvc
    @ComponentScan("com.codegym")
    public class AppConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {
        private ApplicationContext appContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            appContext = applicationContext;
        }

        @Bean
        public ViewResolver viewResolver() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(templateEngine());
            return viewResolver;
        }

        @Bean
        public TemplateEngine templateEngine() {
            TemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver());
            return templateEngine;
        }

        @Bean
        public ITemplateResolver templateResolver() {
            SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
            templateResolver.setApplicationContext(appContext);
            templateResolver.setPrefix("/WEB-INF/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode(TemplateMode.HTML);
            return templateResolver;
        }

        @Bean
        public CustomerService customerService() {
            return new SimpleCustomerServiceImpl();
        }

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/sms");
            dataSource.setUsername("root");
            dataSource.setPassword("04032001");
            return dataSource;
        }

        Properties additionalProperties() {
            Properties properties = new Properties();
            properties.setProperty("Hibernate.hbm2ddl.auto", "update");
            properties.setProperty("Hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
            return properties;
        }

        @Bean
        @Qualifier(value = "entityManager")
        public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
            return entityManagerFactory.createEntityManager();
        }

        @Bean
        public FactoryBean<EntityManagerFactory> entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource());
            em.setPackagesToScan("com.codegym.model");

            JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            em.setJpaVendorAdapter(vendorAdapter);

            em.setJpaProperties(additionalProperties());
            return em;
        }
    }

