package com.example.jwtprac.controller;

import com.example.jwtprac.config.auth.UserDetailsImpl;
import com.example.jwtprac.dto.LoginIdCheckDto;
import com.example.jwtprac.model.Member;
import com.example.jwtprac.service.KakaoService;
import com.example.jwtprac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    // 회원 가입 요청 처리
    @PostMapping("/user/signup")
    public String registerUser(@Valid @RequestBody Member requestDto) {
        String res = userService.registerUser(requestDto);
        if (res.equals("")) {
            return "회원가입 성공";
        } else {
            return res;
        }
    }

    //카카오 소셜 로그인
    @GetMapping("/auth/kakao/callback")
    public @ResponseBody boolean kakaoCalback(String code, HttpServletResponse response) {      //ResponseBody -> Data를 리턴해주는 컨트롤러 함수
        return kakaoService.requestKakao(code, response);
    }

    //로그인 유저 정보
    @GetMapping("user/login/auth")
    public LoginIdCheckDto userDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails);
    }
}