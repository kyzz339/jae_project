package com.springboot.project.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.project.data.dto.ChatMessageDTO;
import com.springboot.project.data.entity.ChatMessage;
import com.springboot.project.repository.ChatMessageRepository;
import com.springboot.project.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService{

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	
	@Autowired
	ChatMessageRepository chatRepository;
	
	@Value("${uploadDir}")
	String uploadDir; 
	
	public ChatMessageDTO findRoomByroomId(int roomId) {
		
		ChatMessage chatMessage = chatRepository.findByRoomId(roomId);
		
		ChatMessageDTO chatmessageDTO = ChatMessageDTO.builder()
										.roomId(chatMessage.getRoomId())
										.sender(chatMessage.getSender())
										.name(chatMessage.getName())
										.content(chatMessage.getContent())
										.timestamp(chatMessage.getTimestamp())
										.build();
		
		
		return chatmessageDTO;
	}
	
	public ChatMessageDTO saveChat(ChatMessage chatmessage) {
		
		chatRepository.save(chatmessage);
		
		ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
										.roomId(chatmessage.getRoomId())
										.sender(chatmessage.getSender())
										.name(chatmessage.getName())
										.content(chatmessage.getContent())
										.type(chatmessage.getType())
										.fileUrl(chatmessage.getFileUrl())
										.content(chatmessage.getContent())
										.timestamp(chatmessage.getTimestamp())
										.build();
		
		return chatMessageDTO;
	}
	
	public List<ChatMessageDTO> findChatMessageByroomId(int roomId){
		
		List<ChatMessage> chatMessage = chatRepository.findAllByRoomId(roomId); 
		List<ChatMessageDTO> chatMessageDTO = new ArrayList<>();
		
		for(ChatMessage x : chatMessage) {
			ChatMessageDTO chatmessageDTO = ChatMessageDTO.builder()
											.roomId(x.getRoomId())
											.sender(x.getSender())
											.name(x.getName())
											.content(x.getContent())
											.type(x.getType())
											.fileUrl(x.getFileUrl())
											.timestamp(x.getTimestamp())
											.build();
			chatMessageDTO.add(chatmessageDTO);
		}
		
		return chatMessageDTO;
		
	}
	
	public ChatMessageDTO uploadfile(MultipartFile file , ChatMessageDTO messageDTO) {
		
		if(file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("파일 용량이 5MB를 넘을 수 없습니다.");
		}
		
		//공백 및 특수문자 제거 , 보안을 위해 파일이름 변경
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

		Path filePath = Paths.get(uploadDir +"/"+messageDTO.getRoomId() +"/"+ messageDTO.getTimestamp() , fileName);
		
		try {
			//파일 경로가 존재하지 않으면 자동으로 경로생성
			Files.createDirectories(filePath.getParent());
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//mimeType 으로 하기 떄문에 해당 기능 삭제
		//확장자로만 확인하는 방법 -> mime 데이터로만 확인 하기 위해 제거
		//String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	    //List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "ico");

		String mimeType = file.getContentType();
		
		if (mimeType == null || (!mimeType.startsWith("image/") && !mimeType.startsWith("application/") 
				&& (!mimeType.startsWith("text/")) )) { //이미지 파일 ,
		    throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
		}
		
	    if (mimeType.startsWith("image")) {
	    	messageDTO.setContent("이미지를 올렸습니다.");
	        messageDTO.setType("image");
	    } else {
	    	messageDTO.setContent("파일을 올렸습니다.");
	        messageDTO.setType("file");
	    }
	    messageDTO.setFileUrl("/uploads/"+  messageDTO.getRoomId() + "/"+messageDTO.getTimestamp() +"/"+fileName);
		
		return messageDTO;
		
	}
	
}
