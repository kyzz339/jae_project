package com.springboot.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.data.dto.SignInRequestDto;
import com.springboot.project.data.dto.SignInResultDto;
import com.springboot.project.data.dto.SignUpRequestDto;
import com.springboot.project.data.dto.SignUpResultDto;
import com.springboot.project.service.SignService;

@RestController
@RequestMapping("/api/sign")
public class SignController {

	private final Logger LOGGER = LoggerFactory.getLogger(SignController.class);
	private final SignService signService;
	
	@Autowired
	public SignController(SignService signService) {
		this.signService = signService;
	}
	
	@PostMapping("/sign-up")
	public ResponseEntity<SignUpResultDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
		
		try {
            SignUpResultDto signUpResultDto = signService.signUp(signUpRequestDto);
            return new ResponseEntity<>(signUpResultDto, HttpStatus.OK); 
        } catch (Exception e) {
            LOGGER.error("회원가입 실패", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
        }
		
	}
	
	@PostMapping("/sign-in")
	public ResponseEntity<SignInResultDto> signIn(@RequestBody SignInRequestDto signInRequestDto) throws RuntimeException{
		
		try {
            SignInResultDto signInResultDto = signService.signIn(signInRequestDto);
            LOGGER.info("로그인 성공");
            return new ResponseEntity<>(signInResultDto, HttpStatus.OK); // 성공 시 200 OK 반환
        } catch (RuntimeException e) {
            LOGGER.error("로그인 실패", e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 실패 시 401 Unauthorized 반환
        }
	}
		
	
	
	
}
