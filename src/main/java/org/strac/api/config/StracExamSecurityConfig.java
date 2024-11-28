package org.strac.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.strac.api.filter.GoogleAccessTokenAuthenticationFilter;
import org.strac.service.config.StracExamServiceConfig;
import org.strac.dao.token.GoogleAccessTokenValidatorDao;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@Import({StracExamServiceConfig.class})
public class StracExamSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, GoogleAccessTokenAuthenticationFilter googleAccessTokenAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test").permitAll() // Ensure the exact path is included
                        .requestMatchers(HttpMethod.GET, "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/drive/**").authenticated()
                        .anyRequest().authenticated() // Ensure other endpoints are protected
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(googleAccessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public GoogleAccessTokenAuthenticationFilter googleAccessTokenAuthenticationFilter(GoogleAccessTokenValidatorDao googleAccessTokenValidatorDao) {
        return new GoogleAccessTokenAuthenticationFilter(googleAccessTokenValidatorDao);
    }
}
