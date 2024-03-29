package community.independe.service.manytomany;

import community.independe.domain.manytomany.FavoritePost;

public interface FavoritePostService {

    Long save(Long postId, Long memberId);

    FavoritePost findByPostIdAndMemberId(Long postId, Long memberId);

    void updateIsFavorite(FavoritePost favoritePost, Boolean isFavorite);

    FavoritePost findByPostIdAndMemberIdAndIsRecommend(Long postId, Long memberId);
}
