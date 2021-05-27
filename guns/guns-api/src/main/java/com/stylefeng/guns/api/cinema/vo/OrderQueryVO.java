package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuKang on 2021/5/19 15:13
 */
@Data
public class OrderQueryVO implements Serializable {

    private String cinemaId;
    private String filmPrice;

}
