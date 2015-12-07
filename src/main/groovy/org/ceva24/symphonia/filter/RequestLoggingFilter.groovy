package org.ceva24.symphonia.filter

import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

@Slf4j
class RequestLoggingFilter extends GenericFilterBean {

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        log.info "received request: '${request?.method} ${request?.servletPath}' from '${request?.remoteUser}'"

        chain.doFilter request, response

        def nullSafeReasonPhrase = (response?.status in HttpStatus.values()*.value()) ? " ${HttpStatus.valueOf(response?.status).reasonPhrase}" : ''

        log.info "sending response to '${request?.method} ${request?.servletPath}': '${response?.status}${nullSafeReasonPhrase}'"
    }
}