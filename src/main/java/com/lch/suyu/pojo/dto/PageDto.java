package com.lch.suyu.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageDto implements Serializable {

    private Integer pageNum=1;

    private Integer pageSize=10;
}
