package com.sparta.spring_post.service;

import com.sparta.spring_post.dto.PostRequestDto;
import com.sparta.spring_post.dto.ResponseDto;
import com.sparta.spring_post.entity.Post;
import com.sparta.spring_post.entity.Users;
import com.sparta.spring_post.jwt.JwtUtil;
import com.sparta.spring_post.repository.PostRepository;
import com.sparta.spring_post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// @Service : 해당 클래스를 루트 컨테이너에 빈 ( Bean) 객체로 생성해주는 어노테이션
// 부모 어노테이션인 @Component 를 붙여줘도 똑같이 루트 컨테이너에 생성되지만 가시성이 떨어지기 때문에 잘 사용하지 않음
@RequiredArgsConstructor
// @RequiredArgsConstructor : final 이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 lombok 어노테이션
public class PostService {

    // PostRepository 연결
    private final PostRepository postRepository;
    // UserRepository 연결
    private final UserRepository userRepository;
    // JwtUtil 연결
    private final JwtUtil jwtUtil;

    // 목록 조회
    @Transactional(readOnly = true)
    // Transaction : DB 관리 시스템 또는 유사한 시스템에서 상호작용의 단위 ( 더 이상 쪼개질 수 없는 최소의 연산 )
    // @Transactional : 해당 범위 내 메서드가 트랜잭션이 되도록 보장해줌 ( 선언적 트랜잭션 - 선언만으로 관리를 용이하게 해줌 )
    // (readOnly = true) : 읽기 전용 모드 - 예상치 못한 Entity 의 생성, 수정, 삭제를 예방할 수 있음
    public ResponseDto<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return ResponseDto.setSuccess("게시물 목록 조회 성공!", posts);
    }

    // 상세 조회
    @Transactional(readOnly = true)
    public ResponseDto<Post> getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(id + "번 게시물이 존재하지 않습니다.")
                // IllegalArgumentException : 유효하지 않거나 잘못된 인수가 메서드에 전달될 때 발생하는 예외
                //                            일반적으로 메서드가 예상 값 범위에 속하지 않거나 특정 제약 조건을 위반하는 인수를 받을 때 발생함
        );
        return ResponseDto.setSuccess(id + "번 게시물 조회 성공!", post);
    }

    // 추가
    @Transactional
    public ResponseDto<Post> createPost(PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        // jwtUtil.resolveToken(httpServletRequest) : httpServletRequest 객체에서 토큰을 추출하는 데 사용되는 메서드
        // 추출된 토큰은 디코딩하여 포함된 사용자 정보를 얻을 수 있음

        if (token == null) {
            return ResponseDto.setFailed("토큰이 없습니다.");
        }

        try {
            jwtUtil.validateToken(token);
            // validateToken : JWT 토큰의 유효성을 검사하고, 올바른 서명을 가지고 있는지 발급자와 수신자가 유효한지 등을 확인함
        } catch (Exception e) {
            return ResponseDto.setFailed("유효한 토큰이 없습니다.");
        }

        Users user = userRepository.findByUsername(postRequestDto.getUsername()).orElseThrow();

        Post post = new Post(user, postRequestDto);
        postRepository.save(post);
        return ResponseDto.setSuccess("게시물 작성 성공!", post);
    }

    // 수정
    @Transactional
    public ResponseDto<Post> updatePost(Long id, PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;
        // JWT 인증 체계에서 Claims 는 사용자의 ID, 이름, 역할, 또는 코튼의 만료일 같은 정보를 포함할 수 있음 ( payload 에 인코딩 된 정보 )
        // Claims : 사용자의 신원을 확인하고 리소스에 대한 액세슬 제어를 제공하는데 사용할 수 있음

        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(id + "번 게시물이 없습니다.")
        );

        if (token == null) {
            return ResponseDto.setFailed("토큰이 없습니다.");
        }

        try {
            jwtUtil.validateToken(token);
        } catch (Exception e) {
            return ResponseDto.setFailed("유효한 토큰이 없습니다.");
        }

        claims = jwtUtil.getUserInfoFromToken(token);
        if (post.getUsers().getUsername().equals(claims.getSubject())) {
            post.update(postRequestDto);
            return ResponseDto.setSuccess(id + "번 게시물 수정 성공!", post);
        } else {
            return ResponseDto.setFailed(id + "번 게시물을 수정할 권한이 없습니다.");
        }
    }

    // 삭제
    @Transactional
    public ResponseDto deletePost(Long id, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;

        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(id + "번 게시물이 없습니다.")
        );

        if (token == null) {
            return ResponseDto.setFailed("토큰이 없습니다.");
        }

        try {
            jwtUtil.validateToken(token);
        } catch (Exception e) {
            return ResponseDto.setFailed("유효한 토큰이 없습니다.");
        }

        claims = jwtUtil.getUserInfoFromToken(token);
        if (post.getUsers().getUsername().equals(claims.getSubject())) {
            postRepository.deleteById(id);
            return ResponseDto.setSuccess(id + "번 게시물 삭제 성공!", null);
        } else {
            return ResponseDto.setFailed(id + "번 게시물을 삭제할 권한이 없습니다.");
        }
    }

}
