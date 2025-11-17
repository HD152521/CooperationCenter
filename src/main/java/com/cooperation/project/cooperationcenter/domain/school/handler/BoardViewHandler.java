package com.cooperation.project.cooperationcenter.domain.school.handler;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

public interface BoardViewHandler {
    boolean supports(SchoolBoard.BoardType type);
    String handle(SchoolBoard board, String school, Model model, Pageable pageable);
}
