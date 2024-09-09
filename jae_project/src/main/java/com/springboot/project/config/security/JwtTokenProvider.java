package com.springboot.project.config.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.springboot.project.service.impl.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
	private final CustomUserDetailsService customUserDetailsService;
	
	@Value("${springboot.jwt.secret}")
	private String secretKey = "secretKey";
	private final long tokenValidMillisecond = 1000L * 60 * 60; // 1시간 토큰 유효시간
	
	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
	}
	
	public String createToken(String email , List<String> roles) {
		
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("roles", roles);
		
		Date now = new Date();
		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + tokenValidMillisecond))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
		
		return token;
		
	}
	
	public Authentication getAuthentication(String token) {
		
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getUsername(token));

		return new UsernamePasswordAuthenticationToken(userDetails, "",
				userDetails.getAuthorities());
	}
	
	public String getUsername(String token) {
		
		String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody() 
				.getSubject();
		return info;
	}
	
	public String resolveToken(HttpServletRequest request) {
		//return request.getHeader("X-AUTH-TOKEN");
		String bearerToken = request.getHeader("Authorization");
	    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	        return bearerToken.substring(7); // "Bearer " 이후의 토큰 값 반환
	    }
	    return null;
	}
	
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		}catch (Exception e) {
			return false;
		}
	}
	
}
