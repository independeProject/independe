package community.independe.api.manytomany;

import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.manytomany.ReportPostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportPostApiController {

    private final ReportPostService reportPostService;

    @Operation(summary = "게시글 신고 *")
    @PostMapping("/api/reportPost/{postId}")
    public ResponseEntity addReportPost(@PathVariable(name = "postId") Long postId,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        ReportPost findReportPost = reportPostService.findByPostIdAndMemberId(postId, loginMember.getId());

        if (findReportPost == null) {
            reportPostService.save(postId, loginMember.getId());
        } else if (findReportPost.getIsReport() == false) {
            reportPostService.updateIsReport(findReportPost, true);
        } else if (findReportPost.getIsReport() == true) {
            reportPostService.updateIsReport(findReportPost, false);
        }

        return ResponseEntity.ok("OK");
    }
}
