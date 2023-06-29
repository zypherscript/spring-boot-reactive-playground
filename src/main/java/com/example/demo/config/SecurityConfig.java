package com.example.demo.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http
        .csrf(CsrfSpec::disable)
        .authorizeExchange(authorizeExchanges ->
            authorizeExchanges.anyExchange().authenticated())
        .httpBasic(withDefaults())
        .formLogin(withDefaults());
    return http.build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user = User.builder()
        .username("user")
        .password("{noop}pwd")
        .roles("USER")
        .build();
    return new MapReactiveUserDetailsService(user);
  }
}
