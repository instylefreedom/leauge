package lol.example.league.config.auth;

import lol.example.league.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig{

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .headers((headerConfig) -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/**", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/login/*", "/logout/*", "/posts/**", "/comments/**").permitAll()
//                                .requestMatchers("/user/**","/api/user/**","/api/game/**").hasRole(Role.USER.name())
//                                .requestMatchers("/api/user/delete", "/api/log/save/data").hasRole(Role.ADMIN.name())
                                .anyRequest().authenticated()
                )
                .logout((logoutConfig) ->
                        logoutConfig.logoutSuccessUrl("/")
                )
                .oauth2Login(Customizer.withDefaults());
//                .oauth2Login((oauth2) -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(this.customOAuth2UserService)));

        return http.build();
    }

}
