package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendPost;

public interface RecommendPostService {

    Long save(Long postId, Long memberId);

    RecommendPost findById(Long recommendPostId);

    RecommendPost findByPostIdAndMemberId(Long postId, Long memberId);

    void updateIsRecommend(RecommendPost recommendPost ,Boolean isRecommend);

    Long countAllByPostIdAndIsRecommend(Long postId);

    RecommendPost findByPostIdAndMemberIdAndIsRecommend(Long postId, Long memberId);
}
