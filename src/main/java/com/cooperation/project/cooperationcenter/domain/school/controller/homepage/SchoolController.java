package com.cooperation.project.cooperationcenter.domain.school.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public String schoolBoard(@PathVariable String school, @PathVariable Long boardId, Model model,
                              @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                              Pageable pageable){
        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);
        SchoolBoard schoolBoard = schoolFindService.loadBoardById(boardId);
        if(schoolBoard.getType().equals(SchoolBoard.BoardType.NOTICE)) {
            model.addAttribute("postDto", schoolFindService.loadPostByBoardByDto(schoolBoard,pageable));
            return schoolPath + school + "/postTemplate";
        }else if(schoolBoard.getType().equals(SchoolBoard.BoardType.INTRO)){
            String content = schoolFindService.loadIntroByBoard(schoolBoard).getContent();
            log.info("content:{}",content);
            model.addAttribute("content",content);
//            return schoolPath + school + "/introductionTemplate";
            String url = schoolPath + school + "/" +content;
            log.info("url:{}",url);
            return url;
        }
        else if(schoolBoard.getType().equals(SchoolBoard.BoardType.FILES)){
            model.addAttribute("filePostDto",schoolFindService.loadFilePostPageByBoardByDto(schoolBoard,pageable));
            return schoolPath + school + "/school-board";
        }
        else if(schoolBoard.getType().equals(SchoolBoard.BoardType.SCHEDULE)){
            log.info("response:{}",schoolFindService.loadScheduleDtoByBoard(schoolBoard));
            model.addAttribute("SchoolScheduleDto",schoolFindService.loadScheduleDtoByBoard(schoolBoard));
            return schoolPath + school + "/school-schedule";
        }
        return null;
    }

    @RequestMapping("/{school}/files/{boardId}")
    public String schoolFilePost(@PathVariable String school, @PathVariable Long boardId, Model model,
                                 @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC)
                                 Pageable pageable){
        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);

        return schoolPath+school+"/school-board";
    }

    @RequestMapping("/{school}/board/{boardId}/post/{postId}")
    public String schoolpostDeatil(@PathVariable String school,@PathVariable Long boardId,@PathVariable Long postId,Model model){
        model.addAttribute("school",school);
        model.addAttribute("boardId",boardId);
        SchoolResponse.PostDetailDto postDetailDto =schoolFindService.getDetailPostDto(postId);
        log.info("postDetail:{}",postDetailDto);
        model.addAttribute("postDto",postDetailDto);

        return schoolPath+school+"/postDetailTemplate";
    }

    /*
    fixme
     public String sanitize(String html) {
       return Jsoup.clean(html, Safelist.basicWithImages());
     }
        Safelist.basicWithImages() => 태그들 자동 무해화하기
     */
}
