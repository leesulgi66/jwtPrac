package com.example.jwtprac.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    NICKNAME_DUPLICATION_CODE(400, "C001", "중복된 닉네임이 있습니다"),
    ID_DUPLICATION_CODE(400, "C002", "중복된 유저 입니다."),
    ID_LENGTH_CHECK_CODE(400, "C003", "아이디를 3자 이상 입력 해 주세요"),
    ID_FORM_CHECK_CODE(400, "C004", "아이디를 형식에 맞게 입력 해 주세요"),
    LENGTH_CHECK_CODE(400, "C005", "아이디를 2-8자로 입력해 주세요"),
    PASSWORD_LENGTH_CODE(400, "C006", "패스워드는 4자 이상 입력해주세요"),
    PASSWORD_CHECK_CODE(400, "C007", "패스워드와 패스워드 확인이 일치하지 않습니다"),
    PASSWORD_CONTAIN_CHECK_CODE(400, "C008", "비밀번호에 아이디를 포함할 수 없습니다."),
    PASSWORD_NULL_CHECK_CODE(400, "C009", "패스워드를 입력 해 주세요");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) { //enum 은 생성자가 존재하지만 Default 생성자는 private 로 되어 있으며 public 으로 변경하는 경우 컴파일 에러가 발생
        //다른 클래스나 인터페이스에서의 상수선언이 클래스 로드 시점에서 생성되는 것 처럼 Enum 또한 생성자가 존재하지만
        // 클래스가 로드되는 시점에서 생성되기 때문에 임의로 생성하여 사용 할 수 없다
        this.status = status;
        this.code = code;
        this.message = message;
    }
}