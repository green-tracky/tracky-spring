package com.example.tracky._core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        String uri = req.getRequestURI();
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        userAgent = userAgent == null ? "" : userAgent;

        String msg = "[로그] ${uri} | IP: ${ip} | UA: ${ua}".replace("${uri}", uri).replace("${ip}", ip).replace("${ua}", userAgent);
        log.info(msg); // 나중에 로그를 파일로 남긴다
        filterChain.doFilter(servletRequest, servletResponse);
    }
}