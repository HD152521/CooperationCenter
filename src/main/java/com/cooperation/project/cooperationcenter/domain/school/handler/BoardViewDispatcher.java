package com.cooperation.project.cooperationcenter.domain.school.handler;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardViewDispatcher{
    private final List<BoardViewHandler> strategies;

    public String dispatch(SchoolBoard board,
                           String school,
                           Model model,
                           Pageable pageable) {

        return strategies.stream()
                .filter(strategy -> strategy.supports(board.getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 BoardType: " + board.getType()))
                .handle(board, school, model, pageable);
    }
}
