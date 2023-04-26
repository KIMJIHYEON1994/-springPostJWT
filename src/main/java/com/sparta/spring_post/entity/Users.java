package com.sparta.spring_post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Users {
    @Id
    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore     // 데이터를 주고받을 때, 해당 데이터 ignore. 응답값 보이지 않음
    private String password;

    public Users(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
