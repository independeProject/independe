package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class MemberRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void initData() {
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);
    }

    @Test
    public void saveTest() {
        // given
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .number("01012345678")
                .email("mockEmail")
                .registrationId("mockRegistration")
                .provider("mockProvider")
                .build();
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(()
                -> new IllegalArgumentException("Member not exist"));

        // then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember.getNickname()).isEqualTo(savedMember.getNickname());
        assertThat(findMember.getRole()).isEqualTo(savedMember.getRole());
        assertThat(findMember.getNumber()).isEqualTo(savedMember.getNumber());
        assertThat(findMember.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(findMember.getRegistrationId()).isEqualTo(savedMember.getRegistrationId());
        assertThat(findMember.getProvider()).isEqualTo(savedMember.getProvider());
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getLastModifiedDate()).isNotNull();
    }

    @Test
    public void findByUsernameTest() {
        // given
        String username = "id";

        // when
        Member findMember = memberRepository.findByUsername(username);

        // then
        assertThat(findMember.getUsername()).isEqualTo(username);
    }

    @Test
    public void findByIdTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        Long id = savedMember.getId();

        // when
        Member findMember = memberRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Member not exist"));

        // then
        assertThat(findMember.getId()).isEqualTo(id);
    }

    @Test
    void findByNicknameTest() {
        // given
        String nickname = "nick";

        // when
        Member findMember = memberRepository.findByNickname(nickname);

        // then
        assertThat(findMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    void findByNicknameFailTest() {
        // given
        String nickname = "fail";

        // when
        Member findMember = memberRepository.findByNickname(nickname);

        // then
        assertThat(findMember).isNull();
    }
}