package dev.gnad.SpringModulith.common.config;

import dev.gnad.SpringModulith.common.security.handler.RestAccessDeniedHandler;
import dev.gnad.SpringModulith.common.security.handler.RestAuthenticationEntryPoint;
import dev.gnad.SpringModulith.module.Auth.service.CustomerUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder,
            CustomerUserDetailService userDetailService
    ){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider
    ) {
        // Used by AuthService for email/password login.
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) throws  Exception{
            httpSecurity
                    .csrf(csrf->csrf.disable())
                    // Errors raised in the filter chain must use the same envelope as the controllers.
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler)
                    )
                    .sessionManagement(
                            session -> session.sessionCreationPolicy(
                                    SessionCreationPolicy.STATELESS
                            )
                    )
                    .authenticationProvider(authenticationProvider)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers(
                                    "/api/auth/register",
                                    "/api/auth/login",
                                    "/api/auth/refresh",
                                    "/api/auth/forgot-password",
                                    "/api/auth/verify-otp",
                                    "/api/auth/reset-password",
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**"
                            ).permitAll()
                            .anyRequest().authenticated()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2
                            // Invalid / expired JWT -> 401 (not 403, not 500)
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler)
                            .jwt(jwt -> jwt
                                    .jwtAuthenticationConverter(
                                            jwtAuthenticationConverter
                                    )
                            )
                    );
            ;

            return httpSecurity.build();
    }

}
