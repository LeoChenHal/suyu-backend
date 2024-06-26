package com.lch.suyu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 队伍查询封装类
 */
@Data
@AllArgsConstructor
public class TeamDto  extends PageDto implements Serializable{

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
     * 队伍最大人数
     */
    private Integer maxNum;



    /**
     * 队伍创建者id
     */
    private Integer userId;

    /**
     * 队伍状态 0-公开 1-私有 2-加密
     */
    private Integer status;



    private static final long serialVersionUID = 1L;
}
