package com.cooperation.project.cooperationcenter.global.token;


import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.token.vo.AccessToken;
import com.cooperation.project.cooperationcenter.global.token.vo.RefreshToken;

public interface TokenProvider {
    AccessToken generateAccessToken(Member member);

    RefreshToken generateRefreshToken(Member member);
}
