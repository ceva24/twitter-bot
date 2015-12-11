package org.ceva24.twitterbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer

@SpringBootApplication
class Application extends SpringBootServletInitializer {

    // TODO unit tests (test can tweet straight after duplicate error)

    static def main(def args) {

        SpringApplication.run Application, args as String[]
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(Application)
    }
}