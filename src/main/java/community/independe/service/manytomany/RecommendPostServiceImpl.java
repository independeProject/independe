package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.PostRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendPostServiceImpl implements RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public Long save(Long postId, Long memberId) {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not exist"));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not exist"));

        RecommendPost savedRecommendPost = recommendPostRepository.save(
                RecommendPost.builder()
                        .member(findMember)
                        .post(findPost)
                        .build()
        );

        return savedRecommendPost.getId();
    }

    @Override
    public RecommendPost findById(Long recommendPostId) {
        return recommendPostRepository.findById(recommendPostId)
                .orElseThrow(() -> new IllegalArgumentException("recommendPost not exist"));
    }
}
