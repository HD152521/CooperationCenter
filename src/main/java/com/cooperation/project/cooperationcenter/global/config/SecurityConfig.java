package com.cooperation.project.cooperationcenter.global.config;

import com.cooperation.project.cooperationcenter.global.filter.AuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final SecurityContextRepositoryImpl securityContextRepository;

    private final AuthenticationTokenFilter authenticationTokenFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // 접근 권한 설정
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(HttpMethod.OPTIONS,"/**/*").permitAll() //preflight요청 허용함.
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() //preflight요청 허용함.

                                //note static 해제
                                .requestMatchers("/css/**","/plugins/**","/js/**").permitAll()
                                //fixme 임시용임 밑에는
                                .requestMatchers("/v3/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/swagger-resources/**",
                                        "/api-test/**").permitAll()

                                //note 일반 사용자 페이지
                                .requestMatchers("/","/home", "/member/signup","/member/login",
                                        "/member/login","/member/logout", "/admin/login","/agency/list").permitAll()
                                .requestMatchers("/api/v1/member/**","/api/v1/file/img/**").permitAll()
                                .requestMatchers("/school/**","/api/v1/file/school/**").permitAll()
                                .requestMatchers("/check/**","/member/password/**","/api/v1/member/reset/**").permitAll()
                                .requestMatchers("/api/v1/tencent/**","/api/v1/agency/region").permitAll()
                                .requestMatchers("/api/v1/admin/login").permitAll()



                                //note 로그인한 사용자
                                .requestMatchers("/survey/log/detail/**").authenticated()

                                //note admin 페이지
                                .requestMatchers("/admin/login").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/admin/**","/api/v1/survey/admin/**").hasRole("ADMIN")
                                .requestMatchers("/survey/make","/survey/edit/**","/survey/log/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .securityContext((securityContext) -> {
                    securityContext
                            .securityContextRepository(securityContextRepository.securityContextRepository())
                            .requireExplicitSave(false);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(LogoutConfigurer::permitAll)
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
            
        //todo 구글 로그인 로직 추가

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //cors추가한내용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        configuration.addAllowedOriginPattern("http://172.30.1.70:8081");
        configuration.addAllowedOriginPattern("http://localhost:8081");
        configuration.addAllowedOriginPattern("https://11680c706486.ngrok-free.app");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
