package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.CreateIndependentPostRequest;
import community.independe.api.dtos.post.CreateRegionPostRequest;
import community.independe.api.dtos.post.PostResponse;
import community.independe.api.dtos.post.PostsResponse;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.comment.Comment;
import community.independe.domain.keyword.KeywordDto;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.query.PostApiRepository;
import community.independe.service.CommentService;
import community.independe.service.KeywordService;
import community.independe.service.PostService;
import community.independe.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final KeywordService keywordService;
    private final VideoService videoService;
    private final PostApiRepository postApiRepository;

    // 자취 게시글 카테고리로 불러오기
    @GetMapping("/api/posts/independent/{type}")
    public Result independentPosts(@PathVariable(name = "type") IndependentPostType independentPostType,
                                   @PageableDefault(
                                           size = 10,
                                           sort = "lastModifiedDate",
                                           direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Post> allIndependentPosts =
                postService.findAllIndependentPostsByTypeWithMember(independentPostType, pageable);
        List<Post> independentPosts = allIndependentPosts.getContent();
        long totalCount = allIndependentPosts.getTotalElements();

        List<PostsResponse> collect = independentPosts.stream()
                .map(p -> new PostsResponse(
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getLastModifiedDate(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                ))
                .collect(Collectors.toList());

        return new Result(collect, totalCount);
    }

    // 자취 게시글 생성
    @PostMapping("/api/posts/independent/new")
    public ResponseEntity<Long> createIndependentPost(@RequestBody @Valid CreateIndependentPostRequest request) {

        Long independentPost = postService.createIndependentPost(
                request.getMemberId(),
                request.getTitle(),
                request.getContent(),
                request.getIndependentPostType());

        return ResponseEntity.ok(independentPost);
    }

    // 지역 게시글 카테고리 별로 가져오기
    @GetMapping("/api/posts/region/{regionType}/{regionPostType}")
    public Result regionPosts(@PathVariable(name = "regionType") RegionType regionType,
                              @PathVariable(name = "regionPostType") RegionPostType regionPostType,
                              @PageableDefault(size = 10,
                                      sort = "lastModifiedDate",
                                      direction = Sort.Direction.DESC)Pageable pageable) {

        Page<Post> allRegionPosts = postService.findAllRegionPostsByTypesWithMember(regionType, regionPostType, pageable);
        List<Post> regionPosts = allRegionPosts.getContent();
        long totalCount = allRegionPosts.getTotalElements();

        List<PostsResponse> collect = regionPosts.stream()
                .map(p -> new PostsResponse(
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getLastModifiedDate(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                ))
                .collect(Collectors.toList());

        return new Result(collect, totalCount);
    }

    // 지역 게시글 생성
    @PostMapping("/api/posts/region/new")
    public ResponseEntity<Long> createRegionPost(@RequestBody @Valid CreateRegionPostRequest request) {
        Long regionPost = postService.createRegionPost(
                request.getMemberId(),
                request.getTitle(),
                request.getContent(),
                request.getRegionType(),
                request.getRegionPostType()
        );

        return ResponseEntity.ok(regionPost);
    }

    // 게시글 1개 구체정보 가져오기
    @GetMapping("/api/posts/{postId}")
    public Result post(@PathVariable(name = "postId") Long postId) {

        Post findPost = postService.findById(postId);
        List<Comment> findComments = commentService.findAllByPostId(postId);

        PostResponse postResponse = new PostResponse(findPost, findComments);
        return new Result(postResponse);
    }

    @GetMapping("/api/posts/main")
    public Result mainPost() {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        // 인기 게시글(10개)
        List<Post> findAllPopularPosts = postApiRepository.findAllPopularPosts(yesterday, today);
        List<PopularPostDto> popularPostDto = findAllPopularPosts.stream()
                .map(p -> new PopularPostDto(
                        p.getId(),
                        p.getTitle(),
                        (p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription(),
                        (p.getRegionType() == null) ? null : p.getRegionType().getDescription(),
                        (p.getRegionPostType() == null) ? null : p.getRegionPostType().getDescription(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        true
                )).collect(Collectors.toList());

        // 추천수 자취 게시글 10개
        List<Post> findAllIndependentPostByRecommendCount = postApiRepository.findAllIndependentPostByRecommendCount(yesterday, today);
        List<PopularIndependentPostsDto> popularIndependentPostsDto = findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                        , true
                )).collect(Collectors.toList());

        // 전체 지역 게시글 5개
        List<Post> findAllRegionPostByRecommendCount = postApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        false
                )).collect(Collectors.toList());

        // 전체 아닌 지역 게시글 5개
        List<Post> findRegionNotAllPostByRecommendCount = postApiRepository.findRegionNotAllPostByRecommendCount(yesterday, today);
        List<RegionNotAllPostDto> regionNotAllPostDto = findRegionNotAllPostByRecommendCount.stream()
                .map(p -> new RegionNotAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRegionType().getDescription(),
                        p.getRegionPostType().getDescription(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        true
                )).collect(Collectors.toList());

        // 인기 검색어 10개
        List<KeywordDto> keywordDto = keywordService.findKeywordsByGroup();

        // 영상
        List<Video> findAllForMain = videoService.findAllForMain();
        List<VideoMainDto> videoMainDto = findAllForMain.stream()
                .map(v -> new VideoMainDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                )).collect(Collectors.toList());

        MainPostDto mainPostDto = new MainPostDto(
                "오늘은 힘드네요",
                popularPostDto,
                regionAllPostDto,
                regionNotAllPostDto,
                popularIndependentPostsDto,
                keywordDto,
                videoMainDto
        );

        return new Result(mainPostDto);
    }
}
