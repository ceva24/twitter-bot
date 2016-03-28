package uk.co.ceva24.twitterbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value('${security.basic.realm}')
    def realm

    @Override
    protected void configure(HttpSecurity http) {

        http.authorizeRequests().anyRequest().fullyAuthenticated().and().httpBasic().realmName realm

        http.csrf().disable().headers().frameOptions().disable()
    }
}