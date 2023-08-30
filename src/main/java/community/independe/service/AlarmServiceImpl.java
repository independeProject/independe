package community.independe.service;

import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.alarm.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmServiceImpl implements AlarmService{

    private AlarmRepository alarmRepository;
    private MemberRepository memberRepository;

    @Override
    public Long saveAlarm(String message, Boolean isRead, AlarmType alarmType, Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFountException("Member Not Exist")
        );

        Alarm alarm = Alarm.builder()
                .message(message)
                .isRead(isRead)
                .alarmType(alarmType)
                .member(findMember)
                .build();

        Alarm savedAlarm = alarmRepository.save(alarm);

        return savedAlarm.getId();
    }
}
