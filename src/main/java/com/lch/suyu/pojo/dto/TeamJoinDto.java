package com.lch.suyu.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TeamJoinDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4687377077519142439L;
    /**
     * id
     */
  private   Integer teamId;

    /**
     * 队伍密码
     */
    private String teamPwd;
}
