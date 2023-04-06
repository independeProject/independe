package community.independe.api.dtos.post.main;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularPostDto {

    private Long postId;
    private String title;
    private String independentPostType;
    private String regionType;
    private String regionPostType;
    private IndependentPostType independentPostTypeEn;
    private RegionType regionTypeEn;
    private RegionPostType regionPostTypeEn;
    private int views;
    private int recommendCount;
    private Long commentCount;
    private boolean isPicture;
}
