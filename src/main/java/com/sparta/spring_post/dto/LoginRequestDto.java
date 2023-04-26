package com.sparta.spring_post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter     // Lombok 어노테이션. private 변수에 접근하기 위해 사용함
// @Getter : 인스턴스 변수를 반환 - 변수 앞에 get
@Setter
// @Setter : 인스턴스 변수를 대입하거나 수정 - 변수 앞에 set
// @Getter, @Setter 둘 다 데이터 무결성을 위해 사용하나
// ( 들어오는 값을 바로 저장하는 게 아닌 한번 검증하고 처리할 수 있도록 하기 때문에 )
// @Setter 는 데이터 무결성을 해칠 수도 있음
// ( 단순하게 @Setter 로 데이터를 수정하면 어떤 부분을 수정하는지 어디서 데이터를 수정하는지 알 수 없음 )
// 데이터 무결성 : 데이터의 정확성과 일관성을 유지하고 보증하는 것
public class LoginRequestDto {
    // 필드 : 유저명, 비밀번호
    private String username;
    private String password;

}
