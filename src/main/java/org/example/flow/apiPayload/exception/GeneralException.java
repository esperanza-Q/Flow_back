package org.example.flow.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.flow.apiPayload.code.BaseErrorCode;
import org.example.flow.apiPayload.dto.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
