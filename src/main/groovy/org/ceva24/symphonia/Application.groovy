package org.ceva24.symphonia

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes
import org.springframework.boot.autoconfigure.web.ErrorAttributes
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.web.context.request.RequestAttributes

@SpringBootApplication
class Application extends SpringBootServletInitializer {

    static def main(def args) {

        SpringApplication.run Application, args as String[]
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(Application)
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
}