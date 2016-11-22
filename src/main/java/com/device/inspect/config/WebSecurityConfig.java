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


    /**
     * @Order(1),通过此注解使bean的加载顺序得到控制
     */
    @Configuration
    @Order(1)
    public static class RestApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

//        @Autowired
//        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

        @Autowired
        private LoginUserService loginUserService;

        /**
         * 使用HttpSecurity和过滤器来限定用户的角色的登陆
         * @param http
         * @throws Exception
         */
        protected void configure(HttpSecurity http) throws Exception {

            Set<String> roles = new HashSet<>();
            roles.add("SERVICE_MANAGER");
            roles.add("SERVICE_BUSINESS");
            roles.add("FIRM_MANAGER");
            roles.add("FIRM_WORKER");
            roles.add("FIRM_SCIENTIST");

            http.csrf().disable().headers().cacheControl().and()
                    .antMatcher("/api/**").authorizeRequests()
                    .antMatchers("/api/rest/**").permitAll()
                    .anyRequest().hasAnyRole("SERVICE_MANAGER", "SERVICE_BUSINESS","FIRM_MANAGER", "FIRM_WORKER","FIRM_SCIENTIST").and()
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