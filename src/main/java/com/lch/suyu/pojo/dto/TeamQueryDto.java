package com.lch.suyu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 队伍查询封装类
 */
@Data
@AllArgsConstructor
public class TeamQueryDto extends PageDto implements Serializable{

    @Serial
    private static final long serialVersionUID = 7867666217752129060L;
    private Integer id;

    private List<Integer> idList;
    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 搜索文本，可以根据队伍名字或者标签搜索
     */
    private String searchText;

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


}
