package com.springboot.project.config.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


//https://spring.io/guides/gs/messaging-stomp-websocket참고
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer{
	
	@Value("${frountIp}") 
    private String frountIp;

    @Value("${frountPort}") 
    private String frountPort;
	
	 @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
		 	//해당 경로로 SimpleBroker를 등록.
	        // SimpleBroker는 해당하는 경로를 SUBSCRIBE하는 Client에게 메세지를 전달하는 간단한 작업을 수행
		 	config.enableSimpleBroker("/sub");
	        // Client 에서 SEND 요청을 처리
	        config.setApplicationDestinationPrefixes("/pub");
	    }

	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        registry.addEndpoint("/stomp/chat")
	        //.setAllowedOriginPatterns("http://localhost:3000")  //프론트 도메인 , 포트 허용
	        .setAllowedOriginPatterns(frountIp , frountIp +":"+frountPort)
            .withSockJS();
	        //apic 테스트를 위해 추가
	        registry.addEndpoint("/stomp/chat")
	        .setAllowedOrigins("*");
	    }
	
	
}
