package com.tedi.growthin.backend.configuration

import com.tedi.growthin.backend.filters.JwtDecoderFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

//TODO: disable login page (will be implemented in frontend -> talk with auth server to get jwt etc..)
//      find out how logout will be implemented (as tokens can't be directly invalidated)
//      also if a user is locked with an active token he can access resource server until jwt expires (and then he can't login)

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
class ResourceServerConfiguration {

    @Autowired
    JwtUriConfiguration jwtUriConfiguration

    @Autowired
    JwtDecoderFilter jwtDecoderFilter

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/**").fullyAuthenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtDecoderFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
        return http.build()
    }
}