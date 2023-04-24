package community.independe.api.android;

import community.independe.api.android.dto.*;
import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.BestCommentDto;
import community.independe.api.dtos.post.PostCommentResponse;
import community.independe.api.dtos.post.PostResponse;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.query.PostApiRepository;
import community.independe.service.CommentService;
import community.independe.service.FilesService;
import community.independe.service.PostService;
import community.independe.service.VideoService;
import community.independe.service.manytomany.FavoritePostService;
import community.independe.service.manytomany.RecommendCommentService;
import community.independe.service.manytomany.RecommendPostService;
import community.independe.service.manytomany.ReportPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostAndroidApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final VideoService videoService;
    private final PostApiRepository postApiRepository;
    private final FilesService filesService;
    private final RecommendPostService recommendPostService;
    private final FavoritePostService favoritePostService;
    private final ReportPostService reportPostService;
    private final RecommendCommentService recommendCommentService;

    @Operation(summary = "자취 게시글 타입별 조회 (안드로이드)")
    @GetMapping("/api/android/posts/independent/{independentPostType}")
    public Result androidIndependentPosts(@PathVariable(name = "independentPostType")IndependentPostType independentPostType,
                                          @PageableDefault(
                                           size = 10,
                                           sort = "createdDate",
                                           direction = Sort.Direction.DESC)Pageable pageable) {

        // 게시글 불러오기
        Slice<Post> allIndependentPostsSlice = postService.findAllIndependentPostsByTypeWithMember(independentPostType, pageable);

        // 현재 페이지에 나올 데이터 수
        int numberOfElements = allIndependentPostsSlice.getNumberOfElements();
        // 조회된 데이터
        List<Post> allIndependentPosts = allIndependentPostsSlice.getContent();
        // 다음 페이지 여부
        boolean hasNextPage = allIndependentPostsSlice.hasNext();
        // 마지막 페이지 여부
        boolean isLastPage = allIndependentPostsSlice.isLast();
        // 현재 페이지 여부
        boolean isFirstPage = allIndependentPostsSlice.isFirst();
        // 현재 페이지 넘버
        Integer pageNumber = pageable.getPageNumber();

        // 마지막 페이지가 아니고 다음 페이지가 있으면 다음 페이지 ++
        if (hasNextPage) {
            pageNumber++;
        }

        Integer finalPageNumber = pageNumber;
        List<AndroidIndependentPostsResponse> collect = allIndependentPosts.stream()
                .map(p -> new AndroidIndependentPostsResponse(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        numberOfElements,
                        hasNextPage,
                        isFirstPage,
                        isLastPage,
                        finalPageNumber
                )).collect(Collectors.toList());

        return new Result(collect);
    }

    @Operation(summary = "자취 게시글 작성 (안드로이드")
    @PostMapping("/api/android/posts/independent/new")
    public AndroidCreateResponse createIndependentPost(@RequestBody AndroidCreateIndependentPostRequest request,
                                                      @AuthenticationPrincipal Member member) {

        Long independentPost = postService.createIndependentPost(
//                member.getId(),
                1L,
                request.getTitle(),
                request.getContent(),
                request.getIndependentPostType()
        );

        AndroidCreateResponse response = new AndroidCreateResponse(
                200
        );

        return response;
    }

    @Operation(summary = "지역 게시글 타입별 조회 (안드로이드)")
    @GetMapping("/api/android/posts/region/{regionType}/{regionPostType}")
    public Result androidRegionPosts(@PathVariable("regionType")RegionType regionType,
                                     @PathVariable("regionPostType")RegionPostType regionPostType,
                                     @PageableDefault(
                                             size = 10,
                                             sort = "createdDate",
                                             direction = Sort.Direction.DESC)Pageable pageable) {

        Slice<Post> allRegionPostsSlice = postService.findAllRegionPostsByTypesWithMember(regionType, regionPostType, pageable);
        int numberOfElements = allRegionPostsSlice.getNumberOfElements();
        List<Post> allRegionPosts = allRegionPostsSlice.getContent();
        boolean hasNextPage = allRegionPostsSlice.hasNext();
        boolean isLastPage = allRegionPostsSlice.isLast();
        boolean isFirstPage = allRegionPostsSlice.isFirst();
        int pageNumber = pageable.getPageNumber();

        if (hasNextPage) {
            pageNumber++;
        }

        int finalPageNumber = pageNumber;
        List<AndroidRegionPostsDto> collect = allRegionPosts.stream()
                .map(p -> new AndroidRegionPostsDto(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        numberOfElements,
                        hasNextPage,
                        isFirstPage,
                        isLastPage,
                        finalPageNumber
                )).collect(Collectors.toList());

        return new Result(collect);
    }

    @Operation(summary = "지역 게시글 작성 (안드로이드")
    @PostMapping("/api/android/posts/region/new")
    public AndroidCreateResponse createRegionPost(@RequestBody AndroidCreateRegionPostRequest request,
                                                      @AuthenticationPrincipal Member member) {

        Long regionPost = postService.createRegionPost(
//                member.getId(),
                1L,
                request.getTitle(),
                request.getContent(),
                request.getRegionType(),
                request.getRegionPostType()
        );

        AndroidCreateResponse response = new AndroidCreateResponse(
                200
        );

        return response;
    }

    @GetMapping("/api/android/posts/{postId}")
    public Result post(@Parameter(description = "게시글 ID(PK)") @PathVariable(name = "postId") Long postId,
                       @AuthenticationPrincipal Member member) {

        postService.increaseViews(postId);

        // 증가 이후 찾기
        Post findPost = postService.findById(postId);
        List<Comment> findComments = commentService.findAllByPostId(postId);
        Long recommendCount = recommendPostService.countAllByPostIdAndIsRecommend(findPost.getId());

        // 베스트 댓글 찾기
        BestCommentDto bestCommentDto = null;
        List<Object[]> bestCommentList = recommendCommentService.findBestComment();
        if (bestCommentList.isEmpty()) {
            bestCommentDto = null;
        } else {
            Object[] bestCommentObject = bestCommentList.get(0);
            Comment bestComment = (Comment) bestCommentObject[0];
            Long bestCommentRecommendCount = (Long) bestCommentObject[1];
            bestCommentDto = new BestCommentDto(
                    bestComment.getId(),
                    bestComment.getMember().getNickname(),
                    bestComment.getContent(),
                    bestComment.getCreatedDate(),
                    bestCommentRecommendCount
            );
        }

        // 댓글 Dto 생성
        List<PostCommentResponse> commentsDto = findComments.stream()
                .map(c -> new PostCommentResponse(
                        c.getId(),
                        c.getMember().getNickname(),
                        c.getContent(),
                        c.getCreatedDate(),
                        recommendCommentService.countAllByCommentIdAndIsRecommend(c.getId()),
                        (c.getParent() == null) ? null : c.getParent().getId(),
                        isRecommendComment(c.getId(), findPost.getId(), member)
                )).collect(Collectors.toList());

        // 게시글 Dto 생성
        PostResponse postResponse = new PostResponse(
                findPost,
                bestCommentDto,
                commentsDto,
                commentService.countAllByPostId(postId),
                recommendCount,
                isRecommend(findPost.getId(), member),
                isFavorite(findPost.getId(), member),
                isReport(findPost.getId(), member)
        );
        return new Result(postResponse);
    }

    @Operation(summary = "메인화면 조회 (안드로이드)")
    @GetMapping("/api/android/posts/main")
    public Result androidMainPost() {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제

        // 인기 게시글(5개)
        List<Post> findAllPopularPosts = postApiRepository.findAllPopularPosts(yesterday, today, 0, 5);
        List<PopularPostDto> popularPostDto = findAllPopularPosts.stream()
                .map(p -> new PopularPostDto(
                        p.getId(),
                        p.getTitle(),
                        (p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription(),
                        (p.getRegionType() == null) ? null : p.getRegionType().getDescription(),
                        (p.getRegionPostType() == null) ? null : p.getRegionPostType().getDescription(),
                        p.getIndependentPostType(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 추천수 자취 게시글 3개
        List<Post> findAllIndependentPostByRecommendCount = postApiRepository.findAllIndependentPostByRecommendCount(yesterday, today, 0, 3);
        List<PopularIndependentPostsDto> popularIndependentPostsDto = findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getIndependentPostType(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 전체 지역 게시글 3개
        List<Post> findAllRegionPostByRecommendCount = postApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today, 0, 3);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        false
                )).collect(Collectors.toList());

        // 전체 아닌 지역 게시글 3개
        List<Post> findRegionNotAllPostByRecommendCount = postApiRepository.findRegionNotAllPostByRecommendCount(yesterday, today, 0, 3);
        List<RegionNotAllPostDto> regionNotAllPostDto = findRegionNotAllPostByRecommendCount.stream()
                .map(p -> new RegionNotAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRegionType().getDescription(),
                        p.getRegionPostType().getDescription(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 영상
        List<Video> findAllForMain = videoService.findAllForMain();
        List<VideoMainDto> videoMainDto = findAllForMain.stream()
                .map(v -> new VideoMainDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                )).collect(Collectors.toList());

        AndroidMainPostDto mainPostDto = new AndroidMainPostDto(
                "오늘은 힘드네요",
                popularPostDto,
                regionAllPostDto,
                regionNotAllPostDto,
                popularIndependentPostsDto,
                videoMainDto
        );

        return new Result(mainPostDto);
    }

    private boolean isRecommendComment(Long commentId, Long postId, Member member) {
//        if (member == null) {
//            return false;
//        } else {
//            if (recommendCommentService.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, member.getId()) == null) {
//                return false;
//            } else {
//                return true;
//            }
//        }

        if (recommendCommentService.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, 1L) == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isRecommend(Long postId, Member member) {
//        if(member == null) {
//            return false;
//        } else {
//            if(recommendPostService.findByPostIdAndMemberIdAndIsRecommend(postId, member.getId()) == null) {
//                return false;
//            } else {
//                return true;
//            }
//        }
        if(recommendPostService.findByPostIdAndMemberIdAndIsRecommend(postId, 1L) == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isFavorite(Long postId, Member member) {
//        if(member == null) {
//            return false;
//        } else {
//            if(favoritePostService.findByPostIdAndMemberIdAndIsRecommend(postId, member.getId()) == null) {
//                return false;
//            } else {
//                return true;
//            }
//        }
        if(favoritePostService.findByPostIdAndMemberIdAndIsRecommend(postId, 1L) == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isReport(Long postId, Member member) {
//        if(member == null) {
//            return false;
//        } else {
//            if(reportPostService.findByPostIdAndMemberIdAndIsRecommend(postId, member.getId()) == null) {
//                return false;
//            } else {
//                return true;
//            }
//        }
        if(reportPostService.findByPostIdAndMemberIdAndIsRecommend(postId, 1L) == null) {
            return false;
        } else {
            return true;
        }
    }
}
