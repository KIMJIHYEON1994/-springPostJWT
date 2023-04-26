package com.sparta.spring_post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      // @NoArgsConstructor : 파라미터가 없는 기본 생성자를 생성해줌
public class PostRequestDto {
    // 필드 : 유저명, 제목, 내용
    private String username;
    private String title;
    private String content;

}
