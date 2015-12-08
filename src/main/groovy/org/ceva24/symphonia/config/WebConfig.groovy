package org.ceva24.symphonia.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes
import org.springframework.boot.autoconfigure.web.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        configurer.ignoreAcceptHeader true
    }

    @Bean
    ErrorAttributes errorAttributes() {

        return new DefaultErrorAttributes() {

            @Override
            Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {

                def attributes = super.getErrorAttributes requestAttributes, includeStackTrace
                attributes.remove 'exception'

                return attributes
            }
        }
    }

    @Bean
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

        def converter = new MappingJackson2HttpMessageConverter()

        def mapper = new ObjectMapper()
        mapper.configure SerializationFeature.INDENT_OUTPUT, true
        mapper.configure SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false

        converter.setObjectMapper mapper

        return converter
    }
}