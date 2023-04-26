package com.sparta.spring_post.controller;

import com.sparta.spring_post.dto.PostRequestDto;
import com.sparta.spring_post.dto.ResponseDto;
import com.sparta.spring_post.entity.Post;
import com.sparta.spring_post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController         // @Controller + @ResponseBody 컨트롤러 클래스 하위 메서드에 @ResponseBody 어노테이션을 붙이지 않아도 문자열과 JSON 등을 전송할 수 있음
                        // @Controller : View 에 표시될 데이터가 있는 Model 객체를 만들고 올바른 View 를 선택하는 일
                        // @RestController : 단순히 객체만들 반환하고 객체 데이터는 JSON 또는 XML 형식으로 HTTP 응답에 담아서 전송함
@RequiredArgsConstructor        // 의존성주입 종류 : Constructor, Setter, Field 타입이 있음
                                // @RequiredArgsConstructor : final 이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 lombok 어노테이션
@RequestMapping("/api")             // @RequestMapping : 공통되는 URL 이 있을 경우 메서드에 중복되는 value 값을 없앨 수 있음
                                       // 배열로 묶어서 다중 요청도 가능함 - @RequestMapping(value = {"/api/user", "/api/users"})
public class PostController {

    // PostService 연결
    private final PostService postService;      // private 필드를 선언. 해당 필드는 PostService 타입이며 생성자를 통해 PostService 매개변수를 전달하여 초기화함
                                                // PostController 는 PostService 가 제공하는 메서드를 사용하여 데이터에 액세스하고 조작할 수 있음 ( 종속성 주입 )
                                                // MVC 디자인 패턴을 사용할 때 사용함

    // 목록 조회
    @GetMapping("/posts")
    public ResponseDto<List<Post>> getAllPosts() {      // Post 객체의 List 를 포함하는 ResponseDto 객체를 반환함
        return postService.getAllPosts();
    }

    // 상세 조회
    @GetMapping("/posts/{id}")
    public ResponseDto<Post> getPost(@PathVariable Long id) {       // @PathVariable : URL 경로에 변수를 넣어주는 어노테이션
                                                                    // null 이나 공백값이 들어가는 파라미터는 적용 X
        return postService.getPost(id);
    }

    // 추가
    @PostMapping("/post")
    public ResponseDto<Post> createPost(@RequestBody PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {
        // HttpServletRequest : ServletRequest 의 하위. 클라이언트의 요청과 관련된 정보와 동작을 가지고 있는 객체
        //                      하나의 서블릿 페이지가 실행되는 동안에만 메모리에 존재하는 객체임
        // HttpServletResponse : ServletResponse 의 하위. 응답할 클라이언트에 대한 정보와 동작을 가지고 있는 객체
        //                      Web Container 가 생성하여 service() 의 인수로 넘겨줌
        return postService.createPost(postRequestDto, httpServletRequest);
    }

    // 수정
    @PutMapping("/post/{id}")
    public ResponseDto<Post> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {
        // @RequestBody : 클라이언트가 전송하는 JSON 형태의 HTTP Body 내용을 Java 객체로 변환시켜줌
        return postService.updatePost(id, postRequestDto, httpServletRequest);
    }

    // 삭제
    @DeleteMapping("/post/{id}")
    public ResponseDto deletePost(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        return postService.deletePost(id, httpServletRequest);
    }

}
