package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.dto.ScheduleType;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SchoolScheduleRepository extends JpaRepository<SchoolSchedule,Long> {
@Query("""
select s
from SchoolSchedule s
where s.schoolBoard.id = :boardId
  and (
       (:start is null and :end is null)
    or ( s.startDate <= coalesce(:end, :start)
         and coalesce(s.endDate, s.startDate) >= coalesce(:start, :end) )
  )
  and ( :type is null or s.type = :type )
  and ( :keyword is null or :keyword = '' or lower(s.title) like concat('%', lower(:keyword), '%') )
order by s.startDate desc, s.id desc
""")
    Page<SchoolSchedule> findSchedulesByBoardAndFlexibleDate(
            @Param("boardId") Long boardId,
            @Param("start")   LocalDate start,    // dto.startDate
            @Param("end")     LocalDate end,      // dto.endDate
            @Param("keyword") String keyword,     // 제목 검색어(옵션)
            ScheduleType type,
            Pageable pageable
    );

    List<SchoolSchedule> findSchoolSchedulesBySchoolBoard(SchoolBoard schoolBoard);
}
