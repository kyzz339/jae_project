package com.springboot.project.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.project.config.security.JwtTokenProvider;
import com.springboot.project.data.dto.SignInRequestDto;
import com.springboot.project.data.dto.SignInResultDto;
import com.springboot.project.data.dto.SignUpRequestDto;
import com.springboot.project.data.dto.SignUpResultDto;
import com.springboot.project.data.entity.User;
import com.springboot.project.repository.UserRepository;
import com.springboot.project.service.SignService;

@Service
public class SignServiceImpl implements SignService{

	private final Logger LOGGER = LoggerFactory.getLogger(SignServiceImpl.class);
	
	public UserRepository userRepository;
	public JwtTokenProvider jwtTokenProvider;
	public PasswordEncoder passwordEncoder;
	
	@Autowired
	public SignServiceImpl(UserRepository userRepository , JwtTokenProvider jwtTokenProvider,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public SignUpResultDto signUp(SignUpRequestDto signUpRequestDto) {
		
		LOGGER.info("회원가입");
		
		User user;
		if(signUpRequestDto.getRole().equalsIgnoreCase("admin")) {
			user = User.builder()
					.email(signUpRequestDto.getEmail())
					.name(signUpRequestDto.getName())
					.password(passwordEncoder.encode(signUpRequestDto.getPassword()))
					.roles(Collections.singletonList("ROLE_ADMIN"))
					.build();
		}else {
			user = User.builder()
					.email(signUpRequestDto.getEmail())
					.name(signUpRequestDto.getName())
					.password(passwordEncoder.encode(signUpRequestDto.getPassword()))
					.roles(Collections.singletonList("ROLE_USER"))
					.build();
		}
		user.setReg_dt(LocalDateTime.now());
		User savedUser = userRepository.save(user);
		
		SignUpResultDto signUpResultDto = new SignUpResultDto();
		
		if(!savedUser.getEmail().isEmpty()) {
			signUpResultDto.setSuccess(true);
		}else {
			signUpResultDto.setSuccess(false);
		}
		
		return signUpResultDto;
		
	}
	
	@Override
	public SignInResultDto signIn(SignInRequestDto signInRequestDto) throws RuntimeException{
		
		LOGGER.info("로그인");
		User user = userRepository.findByEmail(signInRequestDto.getEmail());
		
		if(!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
			throw new RuntimeException();
		}
		
		SignInResultDto signInResultDto = SignInResultDto.builder()
				.token(jwtTokenProvider.createToken(String.valueOf(user.getEmail()),
						user.getRoles()))
				.success(true)
				.build();
		return signInResultDto;
	}
	
}
