package com.matchingapp.config;

import com.matchingapp.repository.UserRepository;
import com.matchingapp.repository.NurseryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/register/**", "/css/**", "/js/**").permitAll() // Allow public access
                .requestMatchers("/admin/**").hasRole("ADMIN") // Admin specific paths
                .requestMatchers("/nursery/**").hasRole("NURSERY") // Nursery specific paths
                .requestMatchers("/nurse/**").hasRole("NURSE") // Nurse specific paths
                .anyRequest().authenticated() // All other requests require authentication
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard after successful login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, NurseryRepository nurseryRepository) {
        return email -> {
            // Try to find in users (nurses)
            return userRepository.findByEmail(email)
                    .map(user -> org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .authorities("ROLE_" + user.getRole())
                            .build())
                    .or(() -> nurseryRepository.findByEmail(email)
                            .map(nursery -> org.springframework.security.core.userdetails.User.builder()
                                    .username(nursery.getEmail())
                                    .password(nursery.getPassword())
                                    .authorities("ROLE_NURSERY")
                                    .build()))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // For development/learning purposes only
    }
}
