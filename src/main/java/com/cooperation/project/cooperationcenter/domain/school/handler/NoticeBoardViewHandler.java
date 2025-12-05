package com.cooperation.project.cooperationcenter.domain.school.handler;

import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoticeBoardViewHandler implements BoardViewHandler{

    private final SchoolFindService schoolFindService;
    private final String schoolPath = "homepage/user/school/";

    @Override
    public boolean supports(SchoolBoard.BoardType type) {
        return type == SchoolBoard.BoardType.NOTICE;
    }

    @Override
    public String handle(SchoolBoard board, String school, Model model, Pageable pageable,String keyword) {
//        SchoolResponse.PostResponseDto dto = schoolFindService.getNoticePostByPageByBoardByDtoByKeyword(board, pageable,keyword);
        Page<SchoolResponse.SchoolPostDto> dto = schoolFindService.loadPostByBoardByDto(board, pageable,keyword);

        log.info("PostResponse : {}",dto.toString());
        model.addAttribute("postDto", dto);
        return schoolPath  + "postTemplate";
    }

}
