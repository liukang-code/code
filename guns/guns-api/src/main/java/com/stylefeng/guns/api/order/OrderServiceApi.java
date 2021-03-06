package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;

/**
 * @author LiuKang on 2021/5/18 21:21
 */
public interface OrderServiceApi {

    //    验证售出的票是否为真
    boolean isTrueSeats(String fieldId, String seats);

    //    已经销售的座位里有没有这些座位
    boolean isNotSoldSeats(String fieldId, String seats);

    //    创建订单信息
    OrderVO saveOrderInfo(Integer filedId, String soldSeats, String seatsName, Integer userId);

    //    使用当前登录人获取已经购买的订单
    Page<OrderVO> getOrderInfoByUserId(Integer userId, Page<OrderVO> page);

    //    根据filedId获取所有已经销售的座位编号
    String getSoldSeatsByFiledId(Integer filedId);

    //    根据订单id获取订单信息
    OrderVO getOrderInfoById(String orderId);

    boolean paySuccess(String orderId);

    boolean payFail(String orderId);
}
