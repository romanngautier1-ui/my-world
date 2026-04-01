package com.app.myworld.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.myworld.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/error", "/error/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/reset-password-link").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/articles").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/articles/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/articles/{id}/neighbors").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/books").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/books/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/mails/send").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/mails/receive").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/mails/sendWithAttachment").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:4200}") String[] allowedOrigins,
            @Value("${app.cors.allowed-methods:GET,POST,PATCH,DELETE,OPTIONS}") String[] allowedMethods,
            @Value("${app.cors.allowed-headers:*}") String[] allowedHeaders,
            @Value("${app.cors.allow-credentials:true}") boolean allowCredentials
    ) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedMethods(List.of(allowedMethods));
        config.setAllowedHeaders(List.of(allowedHeaders));
        config.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
