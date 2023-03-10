package com.example.studymember.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id") //id만 사용하도록 함
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    //이메일을 검색할 때 사용할 토큰값을 db에 저장해놓고 매치하는지 확인하기 위해
    private String emailCheckToken;

    private LocalDateTime joinedAt; //인증이 된 사용자를 그제야 가입될 수 있도록!

    private String bio; //짧은 자기소개

    private String url;

    private String occupation; //직업

    private String location;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;
    private LocalDateTime emailCheckTokenGeneratedAt;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        // 1 시간
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
