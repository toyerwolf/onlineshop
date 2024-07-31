//package com.example.springsecurity.config;
//
//import com.example.springsecurity.security.JwtAuthenticationEntryPoint;
//import com.example.springsecurity.security.JwtAuthenticationFilter;
//import com.example.springsecurity.service.impl.CustomOAuth2UserService;
//import com.example.springsecurity.service.impl.CustomerUserDetailsService;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.BeanIds;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//
//import java.util.List;
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(
//        jsr250Enabled = true,
//        securedEnabled = true,
//        prePostEnabled = true
//)
//@AllArgsConstructor
//public class SecurityConfigWithGoogle {
//
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final CustomerUserDetailsService userDetailsService;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CustomOAuth2UserService customOAuth2UserService;
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//
//        var authManager = authManagerBuilder.build();
//        http.authenticationManager(authManager);
//
//        http.csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(request -> {
//                    var corsConfiguration = new CorsConfiguration();
//                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
//                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                    corsConfiguration.setAllowedHeaders(List.of("*"));
//                    corsConfiguration.setAllowCredentials(true);
//                    return corsConfiguration;
//                }))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/swagger-resources/**",
//                                "/swagger-ui/index.html",
//                                "/webjars/**",
//                                "/auth/**",
//                                "/registration/**",
//                                "/login/oauth2/**",
//                                "/oauth2/authorization/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//                .httpBasic(basic -> basic.authenticationEntryPoint(jwtAuthenticationEntryPoint))
//                .exceptionHandling(Customizer.withDefaults())
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .oauth2Login(oauth2 -> oauth2.loginPage("/oauth2/authorization/google")
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService)
//                        )
//                        .successHandler((request, response, authentication) -> {
//                            response.sendRedirect("/");
//                        })
//                        .failureHandler((request, response, exception) -> {
//                            response.sendRedirect("/login?error");
//                        })
//                );
//
//        return http.build();
//    }
//
//}
//
