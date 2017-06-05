package com.device.inspect.config;
//
//import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//
//@Configuration
//public class MvcConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {
//
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
////        registry.addViewController("/admin/login").setViewName("adminlogin");
//        //registry.addViewController("/vender/login.html").setViewName("venderlogin");
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String myExternalFilePath = "file:///E:/project/inspect_web/";
//        registry.addResourceHandler("/**").addResourceLocations(myExternalFilePath);
//        super.addResourceHandlers(registry);
//    }
//
//}

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter{
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new ApiInterceptor()).addPathPatterns("/**");
    }
}