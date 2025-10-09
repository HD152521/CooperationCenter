package com.cooperation.project.cooperationcenter.domain.member.model;


import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "password_reset_token")
@Builder
@SQLDelete(sql = "UPDATE password_reset_token SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class PasswordResetToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;
    private String token;
    private boolean used;
    private LocalDateTime expiredAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public void update(){
        this.token = UUID.randomUUID().toString();
        this.used = false;
        this.expiredAt = LocalDateTime.now().plusMinutes(30);
    }

    @Builder
    public PasswordResetToken(Member member){
        this.member = member;
        this.token = UUID.randomUUID().toString();
        this.used = false;
        this.expiredAt = LocalDateTime.now().plusMinutes(30);
    }

}
