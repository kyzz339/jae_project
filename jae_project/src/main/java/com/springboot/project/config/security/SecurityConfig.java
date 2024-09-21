package com.springboot.project.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity // Spring Security에 대한 디버깅 모드를 사용하기 위한 어노테이션 (default : false)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception{
		
		httpSecurity.httpBasic().disable()
			.csrf().disable()
			
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT사용으로 세션 사용 X
			.and()
			.authorizeRequests()
			.antMatchers("/sign-in").permitAll()
			.antMatchers("/api/chat/**").authenticated() 
			.antMatchers("/api/product/**").authenticated()
			//허용 주소 추가
			.anyRequest().permitAll()
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider) , 
					UsernamePasswordAuthenticationFilter.class)
			.cors();
			;
	}
	
	@Override
	public void configure(WebSecurity webSecurity) {
		webSecurity.ignoring().antMatchers("/swagger-ui-html");
	}
	
}
