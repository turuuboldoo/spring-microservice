package com.turbo.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableEurekaClient
@SpringBootApplication
class GatewayApplication

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun filterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        httpSecurity
            .csrf()
            .disable()
            .authorizeExchange { exchange ->
                exchange.pathMatchers("/eureka/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            .oauth2ResourceServer { serverSpec ->
                serverSpec.jwt()
            }

        return httpSecurity.build()
    }
}
