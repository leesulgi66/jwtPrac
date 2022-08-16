package com.example.jwtprac.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwtprac.config.auth.UserDetailsImpl;
import com.example.jwtprac.dto.LoginIdCheckDto;
import com.example.jwtprac.dto.SignupRequestDto;
import com.example.jwtprac.dto.SocialSignupRequestDto;
import com.example.jwtprac.model.Member;
import com.example.jwtprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    //일반 사용자 회원가입
    public String registerUser(SignupRequestDto requestDto) throws IOException {
        String error = "";
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String profileImage = requestDto.getUserProfile();
        String passwordCheck = requestDto.getPasswordCheck();
        String nickname = requestDto.getNickname();
        String age = requestDto.getAge();
        String gender = requestDto.getGender();
        String address = requestDto.getAddress();
        String pattern = "^[a-zA-Z0-9]*$";

        System.out.println(username);
        System.out.println(nickname);
        System.out.println(age);
        System.out.println(gender);
        System.out.println(address);

        // 회원 ID 중복 확인
        Optional<Member> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            return "중복된 id 입니다.";
        }

        //이거 수정
        Optional<String> founds = userRepository.findByNickname(nickname);
        if (founds.isPresent()) {
            return "중복된 nickname 입니다.";
        }

        // 회원가입 조건
        if (username.length() < 3) {
            return "아이디를 3자 이상 입력하세요";
        } else if (!Pattern.matches(pattern, username)) {
            return "알파벳 대소문자와 숫자로만 입력하세요";
        } else if (password.length() < 3) {
            return "비밀번호를 4자 이상 입력하세요";
        } else if (password.contains(username)) {
            return "비밀번호에 아이디를 포함할 수 없습니다.";
        }else if (password == null) {
            return "비밀번호를 입력해 주세요.";
        }
        if (!password.equals(passwordCheck)) {
            return "비밀번호가 일치하지 않습니다";
        }

        // 패스워드 인코딩
        password = passwordEncoder.encode(password);
        requestDto.setPassword(password);

        // 유저 정보 저장
        Member member = new Member(username, password, profileImage, nickname, age, gender, address);

        // 프로필 이미지 추가
        if (requestDto.getUserProfileimage() != null) {
            String profileUrl = s3Uploader.upload(requestDto.getUserProfileimage(), "profile");
            member.setProfileImage(profileUrl);
        }
        userRepository.save(member);

        return error;
    }

    //소셜 사용자 회원가입
    public String registerSocialUser(SocialSignupRequestDto requestDto) throws IOException {
        String error = "";
        String username = requestDto.getUsername();
        String password = requestDto.getUsername()+"1234";
        String profileImage = requestDto.getUserProfile();
        MultipartFile setProfileImage = requestDto.getUserProfileImage();
        String nickname = requestDto.getNickname();
        String age = requestDto.getAge();
        String gender = requestDto.getGender();
        String address = requestDto.getAddress();
        String pattern = "^[a-zA-Z0-9]*$";

        System.out.println(username);
        System.out.println(nickname);
        System.out.println(age);
        System.out.println(gender);
        System.out.println(address);


        //닉네임 중복 체크
        Optional<String> founds = userRepository.findByNickname(nickname);
        if (founds.isPresent()) {
            return "중복된 nickname 입니다.";
        }

        // 회원가입 조건
        if (nickname.length() <= 2 || nickname.length() >= 6) {
            return "아이디를 3-6자 이상 입력하세요";
        }
//        else if (!Pattern.matches(pattern, nickname)) {
//            return "알파벳 대소문자와 숫자로만 입력하세요";
//        }

        // 패스워드 인코딩
        password = passwordEncoder.encode(password);
        requestDto.setPassword(password);

        // 유저 정보 저장
        Member member = new Member(username, password, profileImage, nickname, age, gender, address);

        // 프로필 이미지 추가
        if (setProfileImage != null) {
            String profileUrl = s3Uploader.upload(requestDto.getUserProfileImage(), "profile");
            member.setProfileImage(profileUrl);
        }

        userRepository.save(member);

        return error;
    }


    //로그인 유저 정보 반환
    public LoginIdCheckDto userInfo(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        String usernickname = userDetails.getMember().getNickname();
        LoginIdCheckDto userinfo = new LoginIdCheckDto(username, usernickname);
        return userinfo;
    }

    //토큰 발급
    public String JwtTokenCreate(String username){
        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                .withClaim("username", username)
                .sign(Algorithm.HMAC512("6dltmfrl"));
        return jwtToken;
    }
}