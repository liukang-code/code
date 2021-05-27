package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuKang on 2021/5/17 19:41
 */
@Data
public class InfoRequestVO implements Serializable {

    private String biography;
    private ImgVO imgs;
    private ActorRequestVO actors;
    private String filmId;
}
