package community.independe.api.manytomany;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.manytomany.recommendpost.RecommendPostResponse;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.PostService;
import community.independe.service.manytomany.RecommendPostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendPostApiController {

    private final RecommendPostService recommendPostService;
    private final PostService postService;

    @Operation(summary = "게시글 추천 *")
    @PostMapping("/api/recommendPost/{postId}")
    public Result addRecommendPost(@PathVariable(name = "postId") Long postId,
                                                 @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        RecommendPost findRecommendPostByPost = recommendPostService.findByPostIdAndMemberId(postId, loginMember.getId());

        if (findRecommendPostByPost == null) {
            recommendPostService.save(postId, loginMember.getId());
        } else if(findRecommendPostByPost.getIsRecommend() == false) {
            recommendPostService.updateIsRecommend(findRecommendPostByPost, true);
        } else if(findRecommendPostByPost.getIsRecommend() == true) {
            recommendPostService.updateIsRecommend(findRecommendPostByPost, false);
        }

        Long countRecommendPost = recommendPostService.countAllByPostIdAndIsRecommend(postId);
        RecommendPostResponse recommendPostResponse =
                RecommendPostResponse.builder().recommendPostCount(countRecommendPost).build();

        return new Result(recommendPostResponse);
    }
}
