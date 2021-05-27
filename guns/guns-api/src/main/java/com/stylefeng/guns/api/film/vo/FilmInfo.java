package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuKang on 2021/5/17 1:34
 */
@Data
public class FilmInfo implements Serializable {

    private int filmId;
    private String filmType;
    private String filmName;
    private String imgAddress;
    private String filmScore;
    private String showTime;
    private int boxNum;
    private int expectNum;
    private String score;

}
