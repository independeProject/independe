package community.independe.api;

import community.independe.api.dtos.chat.Message;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.AlarmService;
import community.independe.service.EmitterService;
import community.independe.service.chat.ChatService;
import community.independe.util.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatApiController {

    private final static String CHAT_MESSAGE = "채팅이 도착했습니다.";
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate; // 특정 상대에게 메시지를 보내기 위한 객체
    private final JwtTokenVerifier jwtTokenVerifier;
    private final EmitterService emitterService;
    private final AlarmService alarmService;

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message, @Header(name = "Authorization") String header){

        jwtTokenVerifier.verifyToken(header);

        Member loginMember = ((MemberContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMember();

        if (message.getIsReadData()) {
            // 읽음처리
            Long isReadChatRoomId = message.getIsReadChatRoomId(); // 채팅 방 번호
            Long isReadChatId = message.getIsReadChatId(); // 채팅 번호

            chatService.updateChatIsRead(isReadChatId, isReadChatRoomId, loginMember.getId());
        } else {
            message.setSenderNickname(loginMember.getNickname());
            message.setCreatedDate(LocalDateTime.now());

            Long savedChat = chatService.saveChat(message.getMessage(), loginMember.getId(), message.getReceiverId(), message.getChatRoomId());
            message.setChatId(savedChat);
            simpMessagingTemplate.convertAndSendToUser(message.getChatRoomId().toString(),"/private",message);

            emitterService.notify(message.getReceiverId(), CHAT_MESSAGE);
            alarmService.saveAlarm(CHAT_MESSAGE, false, AlarmType.TALK, message.getReceiverId());
        }

        return message;
    }
}
