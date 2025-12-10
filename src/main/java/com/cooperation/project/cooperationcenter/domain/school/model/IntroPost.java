package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.school.dto.CollegeDegreeType;
import com.cooperation.project.cooperationcenter.domain.school.dto.IntroRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.IntroResponse;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "intro_post")
@Builder
@SQLDelete(sql = "UPDATE intro_post SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class IntroPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "introPost") // 주인 아님
    private SchoolBoard schoolBoard;

    //note intro
    private String title;
    private String description;
    private String advantages;  //_로 구분
    private String schoolPicUrl;

    //note BasicInfo
    private String schoolName;
    private String builtAt;
    private String location;
    private String feature;  //,로 구분

    //note homepageUrl
    private String urlNames;
    private String urls;

    @OneToMany(mappedBy = "introPost")
    private List<College> college = new ArrayList<>();

    private String homepageUrl;
    private String englishPageUrl;
    private String mapUrl;
    private int collegeRank;


    public void setBoard(SchoolBoard board){
        this.schoolBoard = board;
    }

    public void deleteBoard(){
        this.schoolBoard.deleteIntroPost(this);
    }

    public void updateCollege(List<College> colleges){
        this.college = colleges;
    }

    public String getImgUrl(){
        if(this.schoolPicUrl==null || this.schoolPicUrl.isEmpty()){
            return "/api/v1/file/default/school";
        }else{
            return this.schoolPicUrl;
        }
    }

    public void fromDto(IntroRequest.TotalIntroSaveDto request){
        IntroRequest.IntroSaveDto intro = request.intro();
        if (intro != null) {
            this.title = intro.title();
            this.description = intro.description();

            // advantages: List<String> → "_" 로 join
            this.advantages = (intro.advantages() != null)
                    ? String.join("_", intro.advantages())
                    : null;
            this.schoolPicUrl = intro.schoolPicUrl();
        }

        IntroRequest.BasicInfoSaveDto basic = request.basicInfo();
        if (basic != null) {
            this.schoolName = basic.schoolName();
            this.builtAt = basic.builtAt();
            this.location = basic.location();

            // feature: 단일 문자열 (이미 ","로 구분된 채로 들어올 수도 있음)
            this.feature = basic.feature();
        }

        IntroRequest.HomepageUrlSaveDto urlsDto = request.urlsDto();
        if (urlsDto != null) {
            // List<String> → ","로 join
            this.urlNames = (urlsDto.urlNames() != null)
                    ? String.join(",", urlsDto.urlNames())
                    : null;

            this.urls = (urlsDto.url() != null)
                    ? String.join(",", urlsDto.url())
                    : null;
        }
        if(request.homepageUrl()!=null) this.homepageUrl = request.homepageUrl();
        if(request.englishPageUrl()!=null) this.englishPageUrl = request.englishPageUrl();
        if(request.collegeRank() != 0) this.collegeRank = request.collegeRank();
        if(request.mapUrl() != null) this.mapUrl = request.mapUrl();
    }

    public IntroResponse.IntroPostResponseDto toResponse() {
        School school = this.getSchoolBoard().getSchool();
        // intro info
        IntroResponse.IntroInfo introInfo = new IntroResponse.IntroInfo(
                this.title,
                this.description,
                this.advantages != null ? List.of(this.advantages.split("_")) : null,
                getImgUrl()
        );

        // basic info
        IntroResponse.BasicInfo basicInfo = new IntroResponse.BasicInfo(
                this.schoolName,
                this.builtAt,
                this.location,
                this.feature != null ? List.of(this.feature.split(",")) : null
        );

        // homepage url info
        IntroResponse.UrlInfo urlInfo = new IntroResponse.UrlInfo(
                this.urlNames != null ? List.of(this.urlNames.split(",")) : null,
                this.urls != null ? List.of(this.urls.split(",")) : null
        );

        // college list
        List<IntroResponse.CollegeInfo> collegeInfoList = (this.college != null)
                ? this.college.stream()
                .map(c -> new IntroResponse.CollegeInfo(
                        c.getId(),
                        c.getCollegeName(),
                        c.getType().name(),
                        c.getDepartments() != null ?c.getDepartments() : null
                ))
                .toList()
                : null;

        return new IntroResponse.IntroPostResponseDto(
                this.id,
                introInfo,
                basicInfo,
                urlInfo,
                collegeInfoList,
                this.homepageUrl,
                this.englishPageUrl,
                this.mapUrl,
                "https://www.google.com/maps?q="
                        + URLEncoder.encode(this.location, StandardCharsets.UTF_8)
                        + "&output=embed",
                this.collegeRank,
                new IntroResponse.SchoolDto(
                        school.getSchoolKoreanName(),
                        school.getSchoolEnglishName()
                )
        );
    }

}
