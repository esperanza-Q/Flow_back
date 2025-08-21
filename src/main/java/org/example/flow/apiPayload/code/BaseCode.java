package org.example.flow.apiPayload.code;


import org.example.flow.apiPayload.dto.ReasonDTO;

public interface BaseCode {
    ReasonDTO getReason(); // 메시지 및 코드 반환
    ReasonDTO getReasonHttpStatus(); // 메시지 + HTTP 상태 포함 반환
}
