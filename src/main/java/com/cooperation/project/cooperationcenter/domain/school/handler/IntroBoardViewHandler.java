package com.cooperation.project.cooperationcenter.domain.school.handler;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@RequiredArgsConstructor
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

        String content = schoolFindService.loadIntroByBoard(board).getContent();
        model.addAttribute("content", content);
        String url = schoolPath + school + "/" +content;
        return url;
    }
}
