package com.example.jwtprac.controller;

import com.example.jwtprac.auth.UserDetailsImpl;
import com.example.jwtprac.dto.LoginIdCheckDto;
import com.example.jwtprac.dto.SignupRequestDto;
import com.example.jwtprac.dto.SocialSignupRequestDto;
import com.example.jwtprac.model.Member;
import com.example.jwtprac.service.KakaoService;
import com.example.jwtprac.service.NaverService;
import com.example.jwtprac.service.S3Uploader;
import com.example.jwtprac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final S3Uploader s3Uploader;

    //S3 Test controller
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
    }

    // 회원 가입 요청 처리
    @PostMapping("/api/user/signup")
    public String registerUser(@Valid @RequestBody SignupRequestDto requestDto) throws IOException {
        String res = userService.registerUser(requestDto);
        if (res.equals("")) {
            return "회원가입 성공";
        } else {
            return res;
        }
    }

    // 소셜 회원 가입 요청 처리
    @PostMapping("/api/user/register")
    public String registerSocialUser(@Valid @ModelAttribute SocialSignupRequestDto requestDto) throws IOException {
        String res = userService.registerSocialUser(requestDto);
        if (res.equals("")) {
            return "회원가입 성공";
        } else {
            return res;
        }
    }

    //카카오 소셜 로그인
    @GetMapping("/auth/kakao/callback")
    public @ResponseBody Member kakaoCalback(String code, HttpServletResponse response) {      //ResponseBody -> Data를 리턴해주는 컨트롤러 함수
        return kakaoService.requestKakao(code, response);
    }

    //네이버 소셜 로그인
    @GetMapping("/login/ouath2/code/naver")
    public @ResponseBody Member naverCalback(String code, HttpServletResponse response) {      //ResponseBody -> Data를 리턴해주는 컨트롤러 함수
        return naverService.requestNaver(code, response);
    }

    //로그인 유저 정보
    @GetMapping("/api/login/auth")
    public LoginIdCheckDto userDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails);
    }
}