package com.cooperation.project.cooperationcenter.domain.school.model;


import com.cooperation.project.cooperationcenter.domain.school.dto.ScheduleType;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "school_schedule")
@Builder
@SQLDelete(sql = "UPDATE school_schedule SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SchoolSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING) private ScheduleType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_board")
    private SchoolBoard schoolBoard;

    public void setBoard(SchoolBoard board){
        this.schoolBoard = board;
    }

    public static SchoolSchedule fromDto(SchoolRequest.ScheduleDto dto){
        ScheduleType type = ScheduleType.from(dto.type());
        return SchoolSchedule.builder()
                .title(dto.title())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .type(type)
                .build();
    }

    public void updateFromDto(SchoolRequest.ScheduleDto dto){
        ScheduleType type = ScheduleType.from(dto.type());
        this.title = dto.title();
        this.type = type;
        this.startDate = dto.startDate();
        this.endDate = dto.endDate();
    }



}
