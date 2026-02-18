package com.example.employeeManagement.SecurityConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup").permitAll()
                        .requestMatchers("/admin/create").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hr/**").hasAnyRole("HR", "ADMIN")
                        .requestMatchers("/employee/**").hasAnyRole("ADMIN","HR","EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true).failureUrl("/login?error=true").permitAll())
                        .logout(logout -> logout
                                .logoutSuccessUrl("/login?logout=true"));
        return httpSecurity.build();
    }
}
