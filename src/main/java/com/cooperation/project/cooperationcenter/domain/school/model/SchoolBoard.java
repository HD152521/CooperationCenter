package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "school_board")
@Builder
@SQLDelete(sql = "UPDATE school_board SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SchoolBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String boardTitle;
    private String realTitle;
    private String boardDescription;
    @Enumerated(EnumType.STRING) private BoardType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private School school;

    @OneToMany(mappedBy = "schoolBoard")
    private List<SchoolPost> posts = new ArrayList<>();

    @OneToMany(mappedBy = "schoolBoard")
    private List<FilePost> filePosts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "intro_post_id")
    private IntroPost introPost;

    public void setSchool(School school){
        this.school = school;
    }

    public void addPost(SchoolPost post) {
        posts.add(post);
        post.setBoard(this);
    }

    public void addFilePost(FilePost post){
        filePosts.add(post);

    }

    public void deleteSchool(){
        school.deleteBoard(this);
        school = null;
    }

    public void deletePost(SchoolPost post) {
        posts.remove(post);
    }
    public void deleteFilePost(FilePost post){filePosts.remove(post);}

    public void setIntroPost(IntroPost introPost) {
        this.introPost = introPost;
    }

    public void deleteIntroPost(IntroPost post) {
        this.introPost=null;
    }

    public void deleteAllPost() {
        posts.clear();
    }

    @Getter
    public enum BoardType {
        INTRO("INTRO","/homepage/user/school/introTemplate"),
        NOTICE("NOTICE","/homepage/user/school/postTemplate"),
        FILES("FILES","/homepage/user/school/school-board");

        private final String type;
        private final String path;

        BoardType(String type, String path) {
            this.path = path;
            this.type = type;
        }

        public static BoardType from(String type) {
            return Arrays.stream(BoardType.values())
                    .filter(t -> t.getType().equalsIgnoreCase(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board type: " + type));
        }
    }

    public static SchoolBoard fromDto(SchoolRequest.SchoolBoardDto dto){
        BoardType type = BoardType.from(dto.boardType());
        return SchoolBoard.builder()
                .boardTitle(dto.boardTitle())
                .realTitle(dto.realTitle())
                .boardDescription(dto.boardDescription())
                .type(type)
                .build();
    }
}
