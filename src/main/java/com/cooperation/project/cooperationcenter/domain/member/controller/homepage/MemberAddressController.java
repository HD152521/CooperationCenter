package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;


import com.cooperation.project.cooperationcenter.domain.member.service.MemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "주소 자동완성 검색",
            description = """
        키워드를 기반으로 주소 자동완성 검색 결과를 제공합니다.
        외부 주소 API를 통해 데이터를 조회합니다.
        """
    )
    @CrossOrigin(origins = "*") // 필요시 특정 도메인으로 제한 가능
    @GetMapping("/address")
    public ResponseEntity<String> suggest(@RequestParam String keyword) {
        return memberAddressService.getMap(keyword);
    }

}
