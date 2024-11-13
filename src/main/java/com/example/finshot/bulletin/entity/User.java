package com.example.finshot.bulletin.entity;

import com.example.finshot.bulletin.entity.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private String intro;

    @Column(nullable = false)
    private String profileImg;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialCode socialCode;

    private String socialAccessToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    private User(String email, String password, String nickname, String profileImg, SocialCode socialCode, String socialAccessToken){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.socialCode = socialCode;
        this.socialAccessToken = socialAccessToken;
        this.role = UserRole.MEMBER;
    }

    public static User of(String email, String password, String nickname, String profileImg) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImg(profileImg)
                .socialCode(SocialCode.NORMAL)
                .build();
    }

    public static User of(String email, String password, String nickname, String profileImg, SocialCode socialCode, String socialAccessToken) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImg(profileImg)
                .socialCode(socialCode)
                .socialAccessToken(socialAccessToken)
                .build();
    }

    public void updateSocialMember(String email, String name, String picture, String socialAccessToken) {
        this.email = email;
        this.nickname = name;
        this.profileImg = picture;
        this.socialAccessToken = socialAccessToken;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeProfileImg(String profileImgUrl) {
        this.profileImg = profileImgUrl;
    }
}

