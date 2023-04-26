package com.sparta.spring_post.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {
    // 필드 : 유저명, 비밀번호
    private String username;
    private String password;

}
