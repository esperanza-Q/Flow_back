package org.example.flow.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.flow.apiPayload.dto.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "테스트용 예외입니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EXISTSUSER400", "이미 존재하는 사용자입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "WRONGPW400", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "WRONGNEWPW400", "새 비밀번호와 확인용 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "존재하지 않는 사용자입니다."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE404", "해당 사용자의 프로필이 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON404", "쿠폰을 찾을 수 없습니다."),
    COUPON_NOT_OWNED(HttpStatus.FORBIDDEN, "COUPON403", "해당 사용자의 쿠폰이 아닙니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "COUPON400_USED", "이미 사용된 쿠폰입니다."),
    SHOP_ID_REQUIRED(HttpStatus.BAD_REQUEST, "COUPON400_SHOP_REQUIRED", "VISIT 쿠폰은 shopInfo_id가 필요합니다."),
    VISIT_SHOP_MISMATCH(HttpStatus.BAD_REQUEST, "COUPON400_SHOP_MISMATCH", "해당 매장에서 사용할 수 없는 VISIT 쿠폰입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).httpStatus(httpStatus).build();

    }
}