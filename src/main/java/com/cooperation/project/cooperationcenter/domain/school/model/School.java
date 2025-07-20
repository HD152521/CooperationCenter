package com.cooperation.project.cooperationcenter.domain.school.model;


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
@Table(name = "school")
@Builder
@SQLDelete(sql = "UPDATE school SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class School extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolKoreanName;
    private String schoolEnglishName;
    private String logoUrl;

    @OneToMany(mappedBy = "school")
    private List<SchoolBoard> boards = new ArrayList<>();

    public void addBoard(SchoolBoard board) {
        boards.add(board);
        board.setSchool(this);
    }

    public void deleteBoard(SchoolBoard board) {
        boards.remove(board);
    }

    public void deleteAllBoard() {
        boards.clear();
    }

    public static School fromDto(SchoolRequest.SchoolDto dto){
        return School.builder()
                .schoolEnglishName(dto.schoolEnglishName())
                .schoolKoreanName(dto.schoolKoreanName())
                .logoUrl(dto.imgUrl())
                .build();
    }
}
