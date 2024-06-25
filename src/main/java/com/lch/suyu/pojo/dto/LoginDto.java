package com.lch.suyu.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDto {
    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;
}
