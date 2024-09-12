package com.springboot.project.data.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_message")
public class ChatMessage {
	
		@Id
		private String id;
		
		@Field("roomId")
	    private int roomId;

	    @Field("sender")
	    private String sender;
	    
	    @Field("name")
	    private String name;

	    @Field("content")
	    private String content;
	    
	    @Field("type")
	    private String type;
	    
	    @Field("file_url")
	    private String fileUrl;

	    @Field("timestamp")
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	    private LocalDateTime timestamp;

}