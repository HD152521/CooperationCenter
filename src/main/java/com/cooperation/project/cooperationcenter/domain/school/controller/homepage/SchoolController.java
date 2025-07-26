package com.cooperation.project.cooperationcenter.domain.school.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/school")
public class SchoolController {

    private final String schoolPath = "homepage/user/school/";
    private final SchoolFindService schoolFindService;

    @RequestMapping("/{school}/intro")
    public String schoolIntro(@PathVariable String school, Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-introduction";
    }

    @RequestMapping("/{school}/curriculum")
    public String schoolCurriculum(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-curriculum";
    }

    @RequestMapping("/{school}/board")
    public String schoolBoard(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-board";
    }

    @RequestMapping("/{school}/schedule")
    public String schoolSchedule(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-schedule";
    }

    @RequestMapping("/{school}/contact")
    public String schoolContact(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-contact";
    }

    @RequestMapping("/{school}/board/{boardId}")
    public String schoolBoard(@PathVariable String school,@PathVariable Long boardId,Model model){

        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);
        SchoolBoard schoolBoard = schoolFindService.loadBoardById(boardId);
        if(schoolBoard.getType().equals(SchoolBoard.BoardType.NOTICE)) {
            model.addAttribute("postDto", schoolFindService.loadPostByBoardByDto(schoolBoard));
            return schoolPath + school + "/postTemplate";
        }else{
            return schoolPath + school + "/introductionTemplate";
        }
    }

    @RequestMapping("/{school}/board/{boardId}/post/{postId}")
    public String schoolpostDeatil(@PathVariable String school,@PathVariable Long boardId,@PathVariable Long postId,Model model){
        model.addAttribute("school",school);
        model.addAttribute("postDto",schoolFindService.loadPostByIdByDto(postId));
        model.addAttribute("boardId",boardId);
        model.addAttribute("fileDtos",schoolFindService.loadPostFileByPost(postId));
        return schoolPath+school+"/postDetailTemplate";
    }
}
