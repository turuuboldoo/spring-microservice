package com.turbo.gallery.config

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration
import dev.miku.r2dbc.mysql.MySqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.turbo.gallery"])
class DatabaseConfig {
    @Bean
    fun factory(): ConnectionFactory {
        return MySqlConnectionFactory.from(
            MySqlConnectionConfiguration.builder()
                .host("localhost")
                .port(8090)
                .database("gallery")
                .username("root")
                .password("secret")
                .build()
        )
    }
}
