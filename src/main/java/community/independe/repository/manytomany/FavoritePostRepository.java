package community.independe.repository.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {

    @Query(value = "select f from FavoritePost f join fetch f.post" +
            " join fetch f.member" +
            " where f.post.id = :postId" +
            " and f.member.id = :memberId",
    countQuery = "select f from FavoritePost f" +
            " where f.post.id = :postId" +
            " and f.member.id = :memberId")
    FavoritePost findByPostIdAndMemberId(@Param("postId") Long postId,
                                         @Param("memberId") Long memberId);

    @Query(value = "select f from FavoritePost f join fetch f.post" +
            " join fetch f.member" +
            " where f.isFavorite = true" +
            " and f.post.id = :postId" +
            " and f.member.id = :memberId")
    FavoritePost findByPostIdAndMemberIdAndIsRecommend(@Param("postId") Long postId,
                                                        @Param("memberId") Long memberId);

    @Query(value = "select p" +
            " from Post p join FavoritePost fp on fp.post.id = p.id" +
            " where fp.member.id = :memberId")
    List<Post> findPostByMemberId(@Param("memberId") Long memberId);
}
