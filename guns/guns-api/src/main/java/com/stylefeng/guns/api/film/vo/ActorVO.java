package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuKang on 2021/5/17 22:22
 */
@Data
public class ActorVO implements Serializable {

    private String imgAddress;
    private String directorName;
    private String roleName ;

}
