package com.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                // 公开接口：查询、搜索、统计、访问记录
                .antMatchers("GET", "/api/projects/**").permitAll()
                .antMatchers("GET", "/api/categories/**").permitAll()
                .antMatchers("POST", "/api/projects/*/visit").permitAll()
                .antMatchers("GET", "/api/projects/stats").permitAll()
                // H2控制台（开发时）
                .antMatchers("/api/h2-console/**").hasRole("ADMIN")
                // 管理接口需要认证
                .antMatchers("POST", "/api/projects/**").hasRole("ADMIN")
                .antMatchers("PUT", "/api/projects/**").hasRole("ADMIN")
                .antMatchers("DELETE", "/api/projects/**").hasRole("ADMIN")
                .antMatchers("POST", "/api/categories/**").hasRole("ADMIN")
                .antMatchers("PUT", "/api/categories/**").hasRole("ADMIN")
                .antMatchers("DELETE", "/api/categories/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
            .httpBasic()
                .and()
            .headers()
                .frameOptions().sameOrigin(); // H2控制台需要

        return http.build();
    }
}
