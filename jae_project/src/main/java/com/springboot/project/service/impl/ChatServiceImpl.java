package com.springboot.project.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
										.original_filename(chatmessage.getOriginal_filename())
										.content(chatmessage.getContent())
										.timestamp(chatmessage.getTimestamp())
										.build();
		
		return chatMessageDTO;
	}
	
	public Page<ChatMessageDTO> findChatMessageByroomId(int roomId , Pageable pageable){
		
		Page<ChatMessage> chatMessages = chatRepository.findAllByRoomId(roomId , pageable);
		
		List<ChatMessageDTO> chatMessageDTO = chatMessages.stream()
											.map(chatMessage -> ChatMessageDTO.builder()
													.id(chatMessage.getId())
													.roomId(chatMessage.getRoomId())
													.sender(chatMessage.getSender())
													.content(chatMessage.getContent())
													.type(chatMessage.getType())
													.fileUrl(chatMessage.getFileUrl())
													.original_filename(chatMessage.getOriginal_filename())
													.timestamp(chatMessage.getTimestamp())
													.build())
												.collect(Collectors.toList());

		return new PageImpl<>(chatMessageDTO , pageable , chatMessages.getTotalElements());
		
	}
	
	public ChatMessageDTO uploadFile(MultipartFile file , ChatMessageDTO chatmessageDTO) {
		
		if(file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("파일 용량이 5MB를 넘을 수 없습니다.");
		}
		
		//공백 및 특수문자 제거 , 보안을 위해 파일이름 변경
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

		Path filePath = Paths.get(uploadDir +"/"+chatmessageDTO.getRoomId() +"/"+ chatmessageDTO.getTimestamp() , fileName);
		
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
		
		ObjectId objectId = new ObjectId();
		chatmessageDTO.setId(objectId.toString());
		
	    if (mimeType.startsWith("image")) {
	    	chatmessageDTO.setContent("이미지를 올렸습니다.");
	    	chatmessageDTO.setType("image");
	    } else {
	    	chatmessageDTO.setContent("파일을 올렸습니다.");
	    	chatmessageDTO.setType("file");
	    }
	    chatmessageDTO.setOriginal_filename(file.getOriginalFilename());
	    chatmessageDTO.setFileUrl("/uploads/" +  chatmessageDTO.getRoomId() + "/"+chatmessageDTO.getTimestamp() +"/"+fileName);
		
		return chatmessageDTO;
		
	}
	
	public Resource downloadFile(String objectId , String filename) {
		
		try {
			
		ObjectId objectId_object = new ObjectId(objectId);
		ChatMessage chatMessage = chatRepository.findById(objectId_object);	
		
		ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
										.roomId(chatMessage.getRoomId())
										.timestamp(chatMessage.getTimestamp())
										.build();
										
		Path filePath = Paths.get(uploadDir + "/" + chatMessageDTO.getRoomId() + "/" + chatMessageDTO.getTimestamp(),filename );
		if(!Files.exists(filePath)) {
			throw new FileNotFoundException("파일을 찾을 수 없습니다: " + filename);
        }
		
		Resource resource = new UrlResource(filePath.toUri());
		
		if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("파일을 읽을 수 없습니다: " + filename);
        }
		return resource;
		
		}catch (MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다: " + filename, e);
        } catch (IOException e) {
            throw new RuntimeException("파일을 로드하는 중 오류가 발생했습니다.", e);
        }
		
	}
	
	public String originalFileName(String id) {
		
		ObjectId objectId = new ObjectId(id);
		ChatMessage chatMessage = chatRepository.findById(objectId);
		ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
										.original_filename(chatMessage.getOriginal_filename())
										.build();
		return chatMessageDTO.getOriginal_filename();
		
	}
	
	
	
}
