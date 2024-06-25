package com.lch.suyu.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lch
 */
@Data
public class RegisterDto implements Serializable {

    String userAccount;
    String userPassword;
    String checkPassword;
}
