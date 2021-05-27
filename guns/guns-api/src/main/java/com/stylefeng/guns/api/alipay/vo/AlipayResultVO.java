package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuKang on 2021/5/20 15:38
 */
@Data
public class AlipayResultVO implements Serializable {

    private String orderId;
    private Integer orderStatus;
    private String orderMsg;
}
