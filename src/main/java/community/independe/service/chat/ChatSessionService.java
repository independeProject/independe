package community.independe.service.chat;

import community.independe.domain.member.Member;

import java.util.Set;

public interface ChatSessionService {

    void enterChatRoom(Long memberId, Long chatRoomId);
    void leaveChatRoom(Long memberId, Long chatRoomId);
    Set<String> getChatRoomMembers(String chatRoomId);
    void enterSocketSession(String sessionId, Long memberId);
    void removeSocketSession(String sessionId);
    Member getMemberSocketSession(String sessionId);
}
