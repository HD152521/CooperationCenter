package com.cooperation.project.cooperationcenter.domain.member.controller;


import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);
    private final MemberService memberService;

    @PostMapping("/signup")
    public BaseResponse<?> signup(@ModelAttribute MemberRequest.SignupDto request) throws Exception{
        log.info("signup request: {}", request);
        return BaseResponse.onSuccess(memberService.signup(request));
    }

    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkDuplicateId(@RequestParam String username) {
//        boolean isDuplicate = memberService.isUsernameTaken(username);
//        return ResponseEntity.ok(isDuplicate);
        return ResponseEntity.ok(true);
    }

}
