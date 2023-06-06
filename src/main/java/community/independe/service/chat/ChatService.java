package community.independe.service.chat;

import community.independe.domain.chat.Chat;

import java.util.List;

public interface ChatService {

    Long saveChat(Long senderId, Long receiverId, String content, Boolean isRead);
    List<Chat> findChatRooms(Long memberId);
    List<Chat> findChatHistory(Long loginMemberId, Long receiverId);
}