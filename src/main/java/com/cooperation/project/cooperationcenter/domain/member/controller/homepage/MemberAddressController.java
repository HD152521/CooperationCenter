package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;


import com.cooperation.project.cooperationcenter.domain.member.service.MemberAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/tencent")
public class MemberAddressController {

    private final MemberAddressService memberAddressService;

    @CrossOrigin(origins = "*") // 필요시 특정 도메인으로 제한 가능
    @GetMapping("/address")
    public ResponseEntity<String> suggest(@RequestParam String keyword) {
        return memberAddressService.getMap(keyword);
    }

}
