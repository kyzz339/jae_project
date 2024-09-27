package com.springboot.project.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.project.data.entity.ChatRoom;
import com.springboot.project.data.entity.QChatRoom;
import com.springboot.project.data.entity.QChatUser;


@Component
public class ChatRoomRepositoryCustomImpl extends QuerydslRepositorySupport implements ChatRoomRepositoryCustom{

	public ChatRoomRepositoryCustomImpl() {
		super(ChatRoom.class);
	}
	
	@Autowired
	JPAQueryFactory jpaQueryFactory;
	
	@Override
	public Page<ChatRoom> mychatRooms(String email,String type , Pageable pageable){
		
		QChatRoom qchatRoom = QChatRoom.chatRoom;
		QChatUser qchatUser = QChatUser.chatUser;
		
		List<ChatRoom> chatRoomlist = jpaQueryFactory
                .select(qchatRoom)
                .from(qchatRoom)
                .leftJoin(qchatRoom.chatUsers, qchatUser)  // 변경된 부분
                .on(qchatRoom.roomId.eq(qchatUser.chatRoom.roomId))
                .where(qchatUser.email.eq(email).and(qchatRoom.type.eq(type)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
		// 전체 결과 개수 구하기
        long total = jpaQueryFactory
                .select(qchatRoom)
                .from(qchatRoom)
                .leftJoin(qchatRoom.chatUsers, qchatUser)
                .on(qchatRoom.roomId.eq(qchatUser.chatRoom.roomId))
                .where(qchatUser.email.eq(email).and(qchatRoom.type.eq(type)))
                .fetchCount();

        // PageImpl로 결과를 반환
        return new PageImpl<>(chatRoomlist, pageable, total);
	} 
	
}
