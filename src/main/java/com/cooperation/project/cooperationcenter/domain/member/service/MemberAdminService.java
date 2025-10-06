//package com.cooperation.project.cooperationcenter.domain.member.service;
//
//
//import com.cooperation.project.cooperationcenter.domain.member.dto.MemberAdminStatsDto;
//import com.cooperation.project.cooperationcenter.domain.member.model.UserStatus;
//import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.YearMonth;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MemberAdminService {
//
////    public MemberResponse.LoginDto login(MemberRequest.LoginDto request, HttpServletResponse response) {
////        Member member = findMemberByEmail(request.email());
////        if(member == null) {
////            log.warn("해당 이메일을 가진 멤버가 존재하지 않음");
////            throw new BaseException(ErrorCode.EMAIL_NOT_FOUND);
////        }
////
////        if(!member.getPassword().equals(request.password())){
////            log.warn("로그인 실패");
////            throw new BaseException(ErrorCode.PASSWORD_ERROR);
////        }
////
////        if(!member.getRole().equals(Member.Role.ADMIN)){
////            throw new BaseException(ErrorCode.MEMBER_NOT_ADMIN);
////        }
////
////        TokenResponse tokenResponse = getTokenResponse(response,member);
////        memberCookieService.addTokenCookies(response,tokenResponse);
////        return MemberResponse.LoginDto.from(member);
////    }
////
////    public Member findMemberByEmail(String email) {
////        try{
////            Member member = memberRepository.findMemberByEmail(email).orElseThrow(
////                    () -> new BaseException(ErrorCode.MEMBER_NOT_FOUND)
////            );
////            return member;
////        }catch(Exception e){
////            return null;
////        }
////    }
////
////    public MemberResponse.LoginDto logout(HttpServletResponse response, HttpSession session, MemberDetails memberDetails, HttpServletRequest request){
////        Member member = getMember(memberDetails.getUsername());
////        //fixme 토큰 response가져와서 그걸 바꿔야함.
////
////        AccessToken accessToken = AccessToken.of(jwtProvider.resolvAccesseToken(request));
////        RefreshToken refreshToken = RefreshToken.of(jwtProvider.resolveRefreshToken(request));
////        log.info("refresh:{}",refreshToken.token());
////        memberCookieService.deleteCookie(response,TokenResponse.of(accessToken,refreshToken));
////        log.info("cookie삭제");
////        session.invalidate();
////        log.info("세션삭제");
////        return MemberResponse.LoginDto.from(member);
////    }
////
////    @NotNull
////    private TokenResponse getTokenResponse(HttpServletResponse response, Member member) {
////
////        AccessToken accessToken = jwtProvider.generateAccessToken(member);
////        RefreshToken refreshToken = jwtProvider.generateRefreshToken(member);
////        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken);
////        return tokenResponse;
////    }
////
////    public Member getMember(String email){
////        try {
////            return memberRepository.findMemberByEmail(email)
////                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
////        } catch (BaseException e){
////            log.warn("멤버 조회 실패: {}", e.getMessage());
////            return null;
////        } catch (Exception e){
////            log.error("알 수 없는 에러 발생: {}", e.getMessage(), e);
////            return null;
////        }
////    }
////
////    @Transactional
////    public void acceptedMember(String email){
////        Member member = memberRepository.findMemberByEmail(email)
////                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
////        member.accept();
////        memberRepository.save(member);
////
////        Agency agency = Agency.fromMember(member);
////        agencyRepository.save(agency);
////    }
//}
