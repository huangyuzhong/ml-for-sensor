package com.device.inspect.config;

//import com.device.inspect.config.security.RestAuthenticationEntryPoint;
import com.device.inspect.config.security.stateless.LoginUserService;
import com.device.inspect.config.security.stateless.StatelessLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Autowired
    private DataSource dataSource;



    @Configuration
    @Order(1)
    public static class RestApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

//        @Autowired
//        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

        @Autowired
        private LoginUserService loginUserService;

        protected void configure(HttpSecurity http) throws Exception {

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_PLATFORM");
            roles.add("ROLE_COMPANY");

            http.csrf().disable().headers().cacheControl().and()
                    .antMatcher("/api/**").authorizeRequests()
                    .antMatchers("/api/rest/**").permitAll()
                    .anyRequest().hasAnyRole("ADMIN", "SHOP","WORKER", "CUSTOMER").and()
                    .exceptionHandling().and()
                    .addFilterBefore(new StatelessLoginFilter("/api/rest/login",  loginUserService, authenticationManager(), roles),
                            UsernamePasswordAuthenticationFilter.class)
                    .logout()
                    .permitAll();
        }



//        @Bean
//        @Override
//        public AuthenticationManager authenticationManagerBean() throws Exception {
//            return super.authenticationManagerBean();
//        }
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.userDetailsService(loginUserService).passwordEncoder(new BCryptPasswordEncoder());
//        }
//
//        @Override
//        protected LoginUserService userDetailsService() {
//            return loginUserService;
//        }

    }

}