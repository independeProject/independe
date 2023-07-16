package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.member.AuthenticationRegionRequest;
import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        Member member = Member.builder()
                .username("testUsername")
                .password("testPasswrod1!")
                .nickname("testNickname")
                .role("ROLE_USER")
                .build();

        memberRepository.save(member);
    }

    @Test
    @WithUserDetails(value = "testUsername", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void authenticateRegionTest() throws Exception {

        AuthenticationRegionRequest request = new AuthenticationRegionRequest();
        request.setRegion("경남");

        // 실행 및 검증
        mockMvc.perform(post("/api/members/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success Region Authentication"));
    }

    @Test
    @WithMockUser(username = "testUsername")
    void createMemberTest() throws Exception {

        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setUsername("username");
        createMemberRequest.setPassword("Aasdf123!@");
        createMemberRequest.setNickname("nick12");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest))
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUsername")
    void createMemberFailTest() throws Exception {

        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setUsername("testUsername");
        createMemberRequest.setPassword("abc");
        createMemberRequest.setNickname("nick12");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest))
                .with(csrf()));

        // then
        perform.andExpect(status().isBadRequest());
    }
}
