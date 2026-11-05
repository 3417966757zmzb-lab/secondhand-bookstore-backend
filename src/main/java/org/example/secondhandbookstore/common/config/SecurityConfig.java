package org.example.secondhandbookstore.common.config;


import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.auth.filter.JwtAuthenticationFilter;
import org.example.secondhandbookstore.common.security.RestAccessDeniedHandler;
import org.example.secondhandbookstore.common.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)//默认是关闭的
                .httpBasic(AbstractHttpConfigurer::disable)//默认是关闭的
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/api/categories","/api/book-editions").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/categories","/api/book-editions","/api/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/book-editions/**","/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/login","/api/users/register","/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/book-editions/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
