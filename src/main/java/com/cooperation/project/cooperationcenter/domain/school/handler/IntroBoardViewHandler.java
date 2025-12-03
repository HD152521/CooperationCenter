package com.cooperation.project.cooperationcenter.domain.school.handler;

import com.cooperation.project.cooperationcenter.domain.school.dto.IntroResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntroBoardViewHandler implements BoardViewHandler{

    private final SchoolFindService schoolFindService;
    private final String schoolPath = "homepage/user/school/";

    @Override
    public boolean supports(SchoolBoard.BoardType type) {
        return type == SchoolBoard.BoardType.INTRO;
    }

    @Override
    public String handle(SchoolBoard board,
                         String school,
                         Model model,
                         Pageable pageable,
                         String keyword) {

        IntroResponse.IntroPostResponseDto content = schoolFindService.loadIntroByBoard(board).toResponse();
        log.info("content:{}",content.colleges().toString());
        model.addAttribute("content", content);
        String url = schoolPath + school + "/" +"introductionTemplate";
        return url;
    }
}
