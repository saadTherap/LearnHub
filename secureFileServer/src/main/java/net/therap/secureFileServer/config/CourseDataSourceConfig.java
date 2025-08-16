package net.therap.secureFileServer.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "courseEntityManagerFactory",
        transactionManagerRef = "courseTransactionManager",
        basePackages = {"net.therap.secureFileServer.repository.course"}
)
public class CourseDataSourceConfig {

    @Bean(name = "courseDataSourceProperties")
    @ConfigurationProperties("spring.datasource.course")
    public DataSourceProperties courseDataSourceProperties() {

        return new DataSourceProperties();
    }

    @Bean(name = "courseDataSource")
    public DataSource courseDataSource(
            @Qualifier("courseDataSourceProperties") DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "courseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean courseEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("courseDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("net.therap.secureFileServer.entity.course")
                .persistenceUnit("course")
                .build();
    }

    @Bean(name = "courseTransactionManager")
    public PlatformTransactionManager courseTransactionManager(
            @Qualifier("courseEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
