package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuKang on 2021/5/20 15:37
 */
@Data
public class AlipayInfoVO implements Serializable {

    private String orderId;
    private String QRCodeAddress;


}
