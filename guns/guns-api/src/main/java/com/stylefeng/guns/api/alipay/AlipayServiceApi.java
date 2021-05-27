package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;

/**
 * @author LiuKang on 2021/5/20 15:39
 */
public interface AlipayServiceApi {

    AlipayInfoVO getQRCode(String orderId);

    AlipayResultVO getOrderStatus(String orderId);

}
