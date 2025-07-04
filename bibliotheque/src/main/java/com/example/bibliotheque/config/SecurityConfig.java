package com.example.bibliotheque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final UserDetailsService userDetailsService;
        private final CustomAuthenticationSuccessHandler successHandler;

        public SecurityConfig(UserDetailsService userDetailsService,
                        CustomAuthenticationSuccessHandler successHandler) {
                this.userDetailsService = userDetailsService;
                this.successHandler = successHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/connexion", "/inscription", "/error",
                                                                "/resources/**", "/css/**", "/js/**")
                                                .permitAll()
                                                .requestMatchers("/reservations/gestion/**", "/homeAdmin")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers("/home", "/livres", "/prets/**", "/reabonnement",
                                                                "/reservations/nouveau")
                                                .hasAuthority("ROLE_ADHERENT")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/connexion")
                                                .loginProcessingUrl("/connexion")
                                                .successHandler(successHandler) // Use custom success handler
                                                .failureUrl("/connexion?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/deconnexion")
                                                .logoutSuccessUrl("/connexion?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/connexion"))
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(
                                                                new LoginUrlAuthenticationEntryPoint("/connexion")));
                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance(); // Consider using BCryptPasswordEncoder in production
        }
}