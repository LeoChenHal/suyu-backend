package com.lch.suyu.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TeamQuitDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4330896144523847499L;

    /**
     * id
     */
    private   Integer teamId;

}
