package com.qmkf.config;

import com.qmkf.utils.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Author：qm
 *
 * @Description：
 */

//1.静态资源映射处理，可以配置以下进行拦截访问路径，让静态资源可以被访问到。
//2.可以将静态资源放在resources中的static中，也可以访问到静态资源

@Configuration
public class WebMvcCongfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/backend/**").addResourceLocations("classpath:/backend/");
//        registry.addResourceHandler("/static/front/**").addResourceLocations("classpath:/front/");
//    }

    //扩展mvc框架的消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter);
    }
}
