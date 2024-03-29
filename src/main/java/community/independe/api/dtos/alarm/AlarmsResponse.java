package community.independe.api.dtos.alarm;

import community.independe.domain.alarm.AlarmType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmsResponse {
    private Long memberId;
    private String message;
    private AlarmType alarmType;
    private Boolean isRead;

    @Builder
    public AlarmsResponse(Long memberId, String message, AlarmType alarmType, Boolean isRead) {
        this.memberId = memberId;
        this.message = message;
        this.alarmType = alarmType;
        this.isRead = isRead;
    }
}
