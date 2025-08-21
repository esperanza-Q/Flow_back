package org.example.flow.apiPayload.code;


import org.example.flow.apiPayload.dto.ErrorReasonDTO;

public interface BaseErrorCode {
    ErrorReasonDTO getReason();
    ErrorReasonDTO getReasonHttpStatus();
}