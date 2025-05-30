package com.cooperation.project.cooperationcenter.domain.member.service;


import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.cooperation.project.cooperationcenter.global.token.JwtProvider;
import com.cooperation.project.cooperationcenter.global.token.vo.AccessToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.cooperation.project.cooperationcenter.global.token.JwtProperties.JWT_REFRESH_TOKEN_COOKIE_NAME;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberCookieService memberCookieService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public Member signup(MemberRequest.SignupDto request) throws Exception{
        log.info("enter");
//        try{
//            checkBeforeSignup(request);
//        }catch (Exception e){
//            log.warn(String.valueOf(e));
//            log.warn(e.toString());
//            return null;
//        }

        checkBeforeSignup(request);

        Member member = null;
        try{
            member = createMember(request);
            memberRepository.save(member);
        }catch (Exception e){
            log.warn("멤버 생성하는데 오류가 생김");
            return null;
        }
        return member;
    }

    public void checkBeforeSignup(MemberRequest.SignupDto request) throws Exception {
        log.info("checking signup...");
        // 중복검사, 비번 같은지, 허용했는지
        log.info("checking exist: {}",!memberRepository.existsMemberByEmail(request.email()));
        if(memberRepository.existsMemberByEmail(request.email())) throw new Exception(ErrorCode.MEMBER_ALREADY_EXIST.getMessage());
        if(!request.password().equals(request.retypePassword())) throw new Exception(ErrorCode.PASSWORD_MISS_MATCH.getMessage());
        if(request.terms().equals("null")||request.terms()==null) throw new Exception(ErrorCode.TERMS_NOW_ALLOW.getMessage());
    }

    public Member createMember(MemberRequest.SignupDto request){
        try{
            Member member = Member.builder()
                    .memberName(request.fullName())
                    .email(request.email())
                    .password(request.password())
                    .imgUrl(null)
                    .build();
            return member;
        }catch (Exception e){
            log.warn("member create error");
            throw new BaseException(ErrorCode.MEMBER_SAVE_ERROR);
        }
    }

    public BaseResponse<?> updateRefreshToken(HttpServletRequest request, HttpServletResponse response){
        // 1) REFRESH_TOKEN 쿠키에서 값 꺼내기
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken == null) {
            return BaseResponse.onFailure(ErrorCode.EMPTY_TOKEN_PROVIDED,null);
        }

        // 2) 리프레시 토큰 유효성 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return BaseResponse.onFailure(ErrorCode.REFRESH_TOKEN_NOT_VALID,null);
        }

        String name = jwtProvider.parseAudience(refreshToken);
        Member member = getMember(name);

        // 3) 토큰에서 Authentication 정보 추출
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);

        // 4) 새 Access Token 생성
        String newAccessToken = jwtProvider.createAccessToken(authentication);
        AccessToken accessToken = new AccessToken(newAccessToken);
        //fixme response 값 바꾸기
        return BaseResponse.onSuccess(newAccessToken);
    }

    public Member getMember(String email){
        try {
            return memberRepository.findMemberByEmail(email)
                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        } catch (BaseException e){
            log.warn("멤버 조회 실패: {}", e.getMessage());
            return null;
        } catch (Exception e){
            log.error("알 수 없는 에러 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}
