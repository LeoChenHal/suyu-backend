package com.lch.suyu.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -4329840397670939221L;
    /**
     * id主键
     */
    private Integer id;
    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDesc;


    /**
     * 队伍过期时间
     */
    private Date expireTime;


    /**
     * 队伍状态 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 队伍密码
     */
    private String teamPwd;

}
