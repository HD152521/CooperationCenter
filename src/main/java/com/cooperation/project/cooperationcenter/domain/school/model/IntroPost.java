package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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

    //note BasicInfo
    private String schoolName;
    private String builtAt;
    private String location;
    private String feature;  //,로 구분

    //note homepageUrl
    private String urlNames;
    private String urls;

    @OneToMany(mappedBy = "introPost")
    private List<College> college;

    @Lob
    @Column(columnDefinition = "LONGTEXT") // 또는 "TEXT"
    private String content;



    public void setBoard(SchoolBoard board){
        this.schoolBoard = board;
    }

    public void deleteBoard(){
        this.schoolBoard.deleteIntroPost(this);
    }

}
