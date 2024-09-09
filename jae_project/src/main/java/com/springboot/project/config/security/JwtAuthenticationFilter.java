package com.springboot.project.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtTokenProvider jwtTokenProvider;
	
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse ,
			FilterChain filterChain) throws ServletException , IOException{
		
		String token = jwtTokenProvider.resolveToken(servletRequest);
		
		if(token != null && jwtTokenProvider.validateToken(token)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			//SecurityContextHolder -> security 인메모리 세션 저장소
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		filterChain.doFilter(servletRequest, servletResponse);
	}
	
}
