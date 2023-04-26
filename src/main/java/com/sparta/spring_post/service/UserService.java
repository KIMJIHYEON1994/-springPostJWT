package com.sparta.spring_post.service;

import com.sparta.spring_post.dto.LoginRequestDto;
import com.sparta.spring_post.dto.ResponseDto;
import com.sparta.spring_post.dto.SignupRequestDto;
import com.sparta.spring_post.entity.Users;
import com.sparta.spring_post.jwt.JwtUtil;
import com.sparta.spring_post.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    // UserRepository 연결
    private final UserRepository userRepository;
    // JwtUtil 연결
    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseDto signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();

        // 아이디 형식 확인
        if (!Pattern.matches("^[a-z0-9]{4,10}$", username)) {
            // 정규표현식을 사용하여 username 문자열 유효성 검사
            // 소문자 ( a-z ), 숫자 ( 0-9 ) 로만 구성되어야 하며 길이는 4~10자 사이여야 함
            // ^ : 시작, $ : 끝
            // ㄱ-ㅎ가-힣 : 한글 문자
            // \\d : 0-9 숫자, \\D : \\d 가 아닌 것
            // \\w : [A-Za-z0-9_], \\W : \\w 가 아닌 것
            return ResponseDto.setFailed("형식에 맞지 않는 아이디 입니다.");
        }

        // 비밀번호 형식 확인
        if (!Pattern.matches("^[a-zA-Z0-9]{8,15}$", password)) {
            // 정규표현식을 사용하여 password 문자열 유효성 검사
            // 대문자 ( A-Z ), 소문자 ( a-z ), 숫자 ( 0-9 ) 로만 구성되어야 하며 길이는 8~15자 사이여야 함
            return ResponseDto.setFailed("형식에 맞지 않는 비밀번호 입니다.");
        }

        // 회원 중복 확인
        Optional<Users> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            // isPresent() : Optional 객체가 값을 가지고 있다면 true, 값이 없다면 false 리턴 ( boolean 타입 )
            return ResponseDto.setFailed("중복된 사용자입니다.");
        }

        Users users = new Users(username, password);
        userRepository.save(users);
        return ResponseDto.setSuccess("회원가입 성공!", null);
    }

    @Transactional(readOnly = true)
    public ResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 아이디 확인
        Users users = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 아이디입니다.")
        );

        // 비밀번호 확인
        if (!users.getPassword().equals(password)) {
            return ResponseDto.setFailed("일치하지 않는 비밀번호 입니다.");
        }

        httpServletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(users.getUsername()));
        // JWT 토큰을 생성하고 HTTP 응답 헤더에 추가
        return ResponseDto.setSuccess("로그인 성공!", null);
    }

}
