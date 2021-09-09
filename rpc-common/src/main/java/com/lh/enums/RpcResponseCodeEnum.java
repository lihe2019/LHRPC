package com.lh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {
    SUCCESS(200, "The remote call is successful"),
    FAILURE(500,"The remote call is failed");
    private final int code;

    private final String message;
}
