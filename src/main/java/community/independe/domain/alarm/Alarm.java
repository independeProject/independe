package community.independe.domain.alarm;

import community.independe.domain.BaseEntity;
import community.independe.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "alarm_id")
    private Long id;

    private String message; // 알림 메시지
    private Boolean isRead; // 읽음 여부

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    //== 연관 관계 ==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 알람, 회원 N : 1 다대일 단방향 매핑

    @Builder
    public Alarm(String message, Boolean isRead, AlarmType alarmType, Member member) {
        this.message = message;
        this.isRead = isRead;
        this.alarmType = alarmType;
        this.member = member;
    }
}
