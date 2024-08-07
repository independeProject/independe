package community.independe.security.config;

import community.independe.security.exception.JwtAccessDeniedHandler;
import community.independe.security.exception.JwtAuthenticationEntryPoint;
import community.independe.security.filter.CorsFilter;
import community.independe.security.filter.JwtAuthorizationMacFilter;
import community.independe.security.handler.OAuth2AuthenticationFailureHandler;
import community.independe.security.handler.OAuth2AuthenticationSuccessHandler;
import community.independe.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import community.independe.security.service.oauth2.CustomOAuth2UserService;
import community.independe.util.JwtTokenVerifier;
import community.independe.util.UrlList;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.headers().frameOptions().disable();

        // stomp 사용을 위한 cors 적용
        http.cors().configurationSource(corsConfigurationSource());

//        http.exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
//                        .accessDeniedHandler(jwtAccessDeniedHandler());

        http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, UrlList.getBlackGetList()).authenticated()
                .requestMatchers(HttpMethod.POST, UrlList.getBlackPostList()).authenticated()
                .requestMatchers(HttpMethod.PUT, UrlList.getBlackPutList()).authenticated()
                .requestMatchers(HttpMethod.DELETE, UrlList.getBlackDeleteList()).authenticated()
                .requestMatchers(HttpMethod.GET, UrlList.getWhiteGetList()).permitAll()
                .requestMatchers(HttpMethod.POST, UrlList.getWhitePostList()).permitAll()
                .anyRequest().authenticated();

//        http.authorizeHttpRequests()
//                        .anyRequest().permitAll();

        http.oauth2Login()
                .redirectionEndpoint()
                    .baseUri("/oauth2/login/oauth2/code/*")
                        .and()
                            .userInfoEndpoint()
                                .userService(customOAuth2UserService)
                                    .and()
                                        .authorizationEndpoint()
                                            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                                                .and()
                                                    .successHandler(oAuth2AuthenticationSuccessHandler);

        http.addFilterBefore(jwtAuthorizationMacFilter(), UsernamePasswordAuthenticationFilter.class);

        http.userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public JwtAuthorizationMacFilter jwtAuthorizationMacFilter() {
        return new JwtAuthorizationMacFilter(jwtTokenVerifier);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Authentication Manger
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        // frontend domain
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://www.independe.co.kr");
        config.addAllowedOrigin("https://independe.co.kr");
        config.addAllowedOrigin("https://api.independe.co.kr");
        config.addAllowedOrigin("https://192.168.0.6:5000");
        config.addAllowedOrigin("https://192.168.0.6:8080");
        config.addAllowedOrigin("http://52.79.249.47:8081");
//        config.addAllowedOrigin("https://api.independe.co.kr");
//        config.addAllowedOrigin("http://localhost:8081");
//        config.addAllowedOrigin("*");
        // credential true 해야 채팅 가능
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }
}
