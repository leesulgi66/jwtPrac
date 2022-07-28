package com.example.jwtprac.controller;

import com.example.jwtprac.config.auth.UserDetailsImpl;
import com.example.jwtprac.dto.LoginIdCheckDto;
import com.example.jwtprac.dto.SignupRequestDto;
import com.example.jwtprac.model.Member;
import com.example.jwtprac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    // 회원 가입 요청 처리
    @PostMapping("/user/signup")
    public String registerUser(@Valid @RequestBody SignupRequestDto requestDto) {
        String res = userService.registerUser(requestDto);
        if (res.equals("")) {
            return "회원가입 성공";
        } else {
            return res;
        }
    }

    //아이디 중복 체크
    @GetMapping("user/login/userIds")
    public String idCheck(@RequestBody LoginIdCheckDto loginIdCheckDto){
        return userService.userIdCheck(loginIdCheckDto);
    }

    //닉네임 중복 체크
    @GetMapping("user/login/nickNames")
    public String nicNAmeCheck(@RequestBody LoginIdCheckDto loginIdCheckDto){
        return userService.userNicNameCheck(loginIdCheckDto);
    }

    //로그인 유저 정보
    @GetMapping("user/login/auth")
    public Member userDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails);
    }
}