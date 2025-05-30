package com.cooperation.project.cooperationcenter.domain.member.model;


import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private String memberName;     //실명
    @NotNull private String email;          //이메일
    @NotNull private String password;
    @NotNull private Role role;             //권한
    @NotNull private boolean isFirstLogin;  //처음로그인
    private String imgUrl;


    public enum Role{
        USER("USER"),
        ADMIN("ADMIN");

        Role(String role){}
        private String role;
    }

    @Builder
    public Member(String memberName,String email,String password, String imgUrl){
        this.memberName = memberName;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.isFirstLogin = true;
        this.imgUrl = imgUrl;
    }
}
