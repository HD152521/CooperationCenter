package com.cooperation.project.cooperationcenter.domain.member.service;


import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.agency.repository.AgencyRepository;
import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberResponse;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.model.UserStatus;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerRequest;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.cooperation.project.cooperationcenter.global.token.JwtProvider;
import com.cooperation.project.cooperationcenter.global.token.vo.AccessToken;
import com.cooperation.project.cooperationcenter.global.token.vo.RefreshToken;
import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberCookieService memberCookieService;
    private final JwtProvider jwtProvider;
    private final FileService fileService;

    private final MemberRepository memberRepository;
    private final AgencyRepository agencyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(String data, MultipartFile agencyPicture,MultipartFile businessCertificate) throws Exception{
        MemberRequest.SignupDto request = mappingToDto(data);
        String encodedPassword = passwordEncoder.encode(request.password());

        String uuid = UUID.randomUUID().toString();
        FileAttachment file1 = (agencyPicture==null) ? null : fileService.saveFile(new FileAttachmentDto(agencyPicture,"member",null,uuid,null));
        FileAttachment file2 = (businessCertificate==null) ? null : fileService.saveFile(new FileAttachmentDto(businessCertificate,"member", null,uuid,null));

        Member member = Member.fromDto(request.withEncodedPassword(encodedPassword),file1,file2,uuid);
        memberRepository.save(member);
    }

    public void login(MemberRequest.LoginDto request,HttpServletResponse response){
        Member member = memberRepository.findMemberByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        checkLogin(request,member);
        //성공시 cookie
        TokenResponse tokenResponse = getTokenResponse(response,member);
        memberCookieService.addTokenCookies(response,tokenResponse);
        log.info("login success");
    }

    public void checkLogin(MemberRequest.LoginDto request, Member member){
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(!member.isAccept()){
            log.warn("아직 승인되지 않은 아이디임.");
            throw new BaseException(ErrorCode.MEMBER_NOT_ACCEPTED);
        }

    }


    private MemberRequest.SignupDto mappingToDto(String data) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(data, MemberRequest.SignupDto.class);
    }

    public Member createMember(MemberRequest.SignupDto request){
        try{
            Member member = Member.builder()

                    .build();
            return member;
        }catch (Exception e){
            log.warn("member create error");
            throw new BaseException(ErrorCode.MEMBER_SAVE_ERROR);
        }
    }


    @NotNull
    private TokenResponse getTokenResponse(HttpServletResponse response, Member member) {
        AccessToken accessToken = jwtProvider.generateAccessToken(member);
        RefreshToken refreshToken = jwtProvider.generateRefreshToken(member);
        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken);
        return tokenResponse;
    }

    public void logout(HttpServletRequest request,HttpServletResponse response){
        log.info("로그아웃 시도");
        Cookie[] cookies = request.getCookies();
        String accessToken = jwtProvider.resolvAccesseToken(request);
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        memberCookieService.deleteCookie(response,TokenResponse.of(AccessToken.of(accessToken),RefreshToken.of(refreshToken)));
        SecurityContextHolder.clearContext();
    }


    public BaseResponse<?> updateRefreshToken(HttpServletRequest request, HttpServletResponse response){
        // 1) REFRESH_TOKEN 쿠키에서 값 꺼내기
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken == null) {
            return BaseResponse.onFailure(ErrorCode.EMPTY_TOKEN_PROVIDED,null);
        }

        // 2) 리프레시 토큰 유효성 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            log.info("refreshtoken vaild failed...");
            return BaseResponse.onFailure(ErrorCode.REFRESH_TOKEN_NOT_VALID,null);
        }

        String name = jwtProvider.parseAudience(refreshToken);
        Member member = getMember(name);

        AccessToken newAccessToken = jwtProvider.generateAccessToken(member);
        log.info("get new accesstoken from refreshtoken... ");
        memberCookieService.addAccessTokenCookies(response,newAccessToken.token());

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

    public boolean isUsernameTaken(String username){
        try{
            return memberRepository.existsMemberByEmail(username);
        }catch (Exception e){
            log.warn(e.getMessage());
            return false;
        }
    }

/*
        ========================
       ㅐ                       ㅐ
       ㅐ                       ㅐ
       ㅐ    Admin임 밑에는       ㅐ
       ㅐ                       ㅐ
       ========================
 */
    public void adminLogin(MemberRequest.LoginDto request,HttpServletResponse response){
        Member member = memberRepository.findMemberByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        if(!member.getRole().isAdmin()) throw new BaseException(ErrorCode.MEMBER_NOT_ADMIN);
        checkLogin(request,member);
        //성공시 cookie
        TokenResponse tokenResponse = getTokenResponse(response,member);
        memberCookieService.addTokenCookies(response,tokenResponse);
        log.info("login success");
    }

    @Transactional
    public void acceptedMember(String email){
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        member.accept();
        if(member.getAgency()==null) {
            Agency agency = Agency.fromMember(member);
            agencyRepository.save(agency);
            member.setAgency(agency);
            //fixme 여기서 이미 있는 유학원이면 추가를 하면 안됨.
        }
        memberRepository.save(member);
    }

    @Transactional
    public void pendingMember(String email){
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        member.pending();
        memberRepository.save(member);
    }

    public MemberResponse.DetailDto detailMember(String email){
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        return MemberResponse.DetailDto.from(member);
    }

    public List<MemberResponse.PendingDto> getPendingList(){
        List<Member> member = memberRepository.findTop4ByApprovalSignupFalseOrderByCreatedAtDesc();
        if(member == null){
            log.warn("가입 승인 대기중인 멤버가 없음.");
            return null;
        }
        return MemberResponse.PendingDto.from(member);
    }

    public MemberResponse.UserPageDto getMangeUserPage(MemberRequest.UserFilterDto condition, Pageable pageable){
        log.info("Get manage user page data...");
        YearMonth ym   = YearMonth.now();                 // 이번 달
        LocalDateTime start = ym.atDay(1).atStartOfDay(); // 이번 달 1일 00:00:00
        LocalDateTime end   = ym.atEndOfMonth()
                .atTime(LocalTime.MAX);

        long total = memberRepository.count();
        long active = memberRepository.countByStatus(UserStatus.APPROVED);
        long newMember = memberRepository.countByCreatedAtBetween(start,end);
        long waitMember = memberRepository.countByStatus(UserStatus.PENDING);

        return new MemberResponse.UserPageDto(
                total,
                active,
                newMember,
                waitMember,
                findUsersByCondition(condition,pageable)
        );
    }

    public Page<MemberResponse.UserDto> findUsersByCondition(MemberRequest.UserFilterDto condition, Pageable pageable) {
        log.info("Get user from dto...");
        return memberRepository.searchMembers(condition.keyword(), condition.status(), condition.date(),pageable)
                .map(MemberResponse.UserDto::from);
    }



}
