package com.springboot.project.service;

import com.springboot.project.data.dto.SignInRequestDto;
import com.springboot.project.data.dto.SignInResultDto;
import com.springboot.project.data.dto.SignUpRequestDto;
import com.springboot.project.data.dto.SignUpResultDto;

public interface SignService {

	SignUpResultDto signUp(SignUpRequestDto signUpRequestDto);
	
	SignInResultDto signIn(SignInRequestDto signInRequestDto)throws RuntimeException;
	
}
