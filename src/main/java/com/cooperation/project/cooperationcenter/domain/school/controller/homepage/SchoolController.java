package com.cooperation.project.cooperationcenter.domain.school.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.handler.BoardViewDispatcher;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/school")
public class SchoolController {

    private final String schoolPath = "homepage/user/school/";
    private final SchoolFindService schoolFindService;
    private final BoardViewDispatcher boardViewDispatcher;

    @RequestMapping("/{school}/board/{boardId}")
    public String schoolBoard(@PathVariable String school, @PathVariable Long boardId, Model model,
                              @RequestParam(required = false) String keyword,
                              @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                              Pageable pageable){
        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);
        log.info("keyword:{}",keyword);
        SchoolBoard schoolBoard = schoolFindService.loadBoardById(boardId);
        model.addAttribute("schoolLogo",schoolBoard.getSchool().getLogoUrl());
        return boardViewDispatcher.dispatch(schoolBoard, school, model, pageable,keyword);
    }

    @RequestMapping("/{school}/files/{boardId}")
    public String schoolFilePost(@PathVariable String school, @PathVariable Long boardId, Model model,
                                 @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC)
                                 Pageable pageable){
        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);

        return schoolPath+"school-board";
    }

    @RequestMapping("/{school}/board/{boardId}/post/{postId}")
    public String schoolpostDeatil(@PathVariable String school,@PathVariable Long boardId,@PathVariable Long postId,Model model){
        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);
        SchoolResponse.PostDetailDto postDetailDto =schoolFindService.getDetailPostDto(postId);
        log.info("postDetail:{}",postDetailDto);
        model.addAttribute("postDto",postDetailDto);

        return schoolPath+"postDetailTemplate";
    }

}
