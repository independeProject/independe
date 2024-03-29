package community.independe.repository.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ReportPostRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private ReportPostRepository reportPostRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    void saveTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().member(member).build();
        Post savedPost = postRepository.save(post);
        ReportPost reportPost =
                ReportPost.builder().member(savedMember).post(savedPost).isReport(true).build();

        // when
        ReportPost savedReportPost = reportPostRepository.save(reportPost);

        // then
        assertThat(savedReportPost).isEqualTo(reportPost);
        assertThat(savedReportPost.getId()).isEqualTo(reportPost.getId());
        assertThat(savedReportPost.getIsReport()).isEqualTo(reportPost.getIsReport());
        assertThat(savedReportPost.getMember()).isEqualTo(reportPost.getMember());
        assertThat(savedReportPost.getPost()).isEqualTo(reportPost.getPost());
    }

    @Test
    void findByPostIdAndMemberIdTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().member(member).build();
        Post savedPost = postRepository.save(post);
        ReportPost reportPost =
                ReportPost.builder().member(savedMember).post(savedPost).isReport(false).build();
        ReportPost savedReportPost = reportPostRepository.save(reportPost);

        // when
        ReportPost findReportPost =
                reportPostRepository.findByPostIdAndMemberId(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findReportPost).isEqualTo(savedReportPost);
        assertThat(findReportPost.getId()).isEqualTo(savedReportPost.getId());
        assertThat(findReportPost.getIsReport()).isEqualTo(savedReportPost.getIsReport());
        assertThat(findReportPost.getPost()).isEqualTo(savedReportPost.getPost());
        assertThat(findReportPost.getMember()).isEqualTo(savedReportPost.getMember());
        assertThat(findReportPost.getCreatedDate()).isNotNull();
    }

    @Test
    void findByPostIdAndMemberIdAndIsRecommendTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().member(member).build();
        Post savedPost = postRepository.save(post);
        ReportPost reportPost =
                ReportPost.builder().member(savedMember).post(savedPost).isReport(true).build();
        ReportPost savedReportPost = reportPostRepository.save(reportPost);

        // when
        ReportPost findReportPost =
                reportPostRepository.findByPostIdAndMemberIdAndIsRecommend(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findReportPost).isEqualTo(savedReportPost);
    }
}
