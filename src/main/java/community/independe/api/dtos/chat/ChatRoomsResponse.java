package community.independe.api.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomsResponse {

    private Long chatRoomId;
    private String senderNickname;
    private String receiverNickname;
    private String lastMessage;
    private Boolean isRead;
}
