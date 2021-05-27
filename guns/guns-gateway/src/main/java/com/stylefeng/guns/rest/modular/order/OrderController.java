package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.alipay.AlipayServiceApi;
import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.rest.common.CurrentUserUtil;
import com.stylefeng.guns.rest.modular.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuKang on 2021/5/18 20:59
 */
@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2021", timeout = 100000)
    private OrderServiceApi orderServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2020", timeout = 100000)
    private OrderServiceApi order2020ServiceApi;

    @Reference(interfaceClass = AlipayServiceApi.class, check = false, timeout = 100000)
    private AlipayServiceApi alipayServiceApi;

    @PostMapping("buyTickets")
    public ResultVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        try {
//            验证售出的票是否为真
            boolean trueSeats = orderServiceApi.isTrueSeats(fieldId.toString(), soldSeats);
//            验证已销售的座位是否包含这次请求的座位
            boolean notSoldSeats = orderServiceApi.isNotSoldSeats(fieldId.toString(), soldSeats);
            if (trueSeats && notSoldSeats) {
                String userId = CurrentUserUtil.getUserId();
                if (userId == null || userId.trim().length() == 0) {
                    return ResultVO.serviceFail("用户未登陆");
                }
                OrderVO orderVO = orderServiceApi.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                if (orderVO == null) {
                    log.error("购票未成功");
                    return ResultVO.serviceFail("购票业务异常");
                }
                return ResultVO.success(orderVO);
            } else {
                return ResultVO.serviceFail("订单中的座位有异常");
            }
        } catch (Exception e) {
            log.error("购票业务异常", e);
            return ResultVO.serviceFail("购票业务异常");
        }
    }

    @PostMapping("getOrderInfo")
    public ResultVO getOrderInfo(@RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
                                 @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize) {

        String userId = CurrentUserUtil.getUserId();
        if (userId == null || userId.trim().length() == 0) {
            return ResultVO.serviceFail("用户未登陆");
        }

//        合并订单
        Page<OrderVO> order2021Page = orderServiceApi.getOrderInfoByUserId(Integer.parseInt(userId), new Page<OrderVO>(nowPage, pageSize));
        Page<OrderVO> order2020Page = order2020ServiceApi.getOrderInfoByUserId(Integer.parseInt(userId), new Page<OrderVO>(nowPage, pageSize));
        int mergePages = (int) (order2020Page.getPages() + order2021Page.getPages());
        List<OrderVO> mergeOrders = new ArrayList<>();
        mergeOrders.addAll(order2020Page.getRecords());
        mergeOrders.addAll(order2021Page.getRecords());
        return ResultVO.success("", nowPage, mergePages, mergeOrders);
    }

    @PostMapping("getPayInfo")
    public ResultVO getPayInfo(@RequestParam("orderId") String orderId) {

        String userId = CurrentUserUtil.getUserId();

        if (userId == null || userId.trim().length() == 0) {
            return ResultVO.serviceFail("用户未登陆");
        }

//          返回订单支付二维码
        AlipayInfoVO qrCode = alipayServiceApi.getQRCode(orderId);
        if (qrCode == null || qrCode.getQRCodeAddress().trim().length() == 0) {
            return ResultVO.serviceFail("获取支付宝二维码失败");
        }
        return ResultVO.success(IMG_PRE, qrCode);
    }

    @PostMapping("getPayResult")
    public ResultVO getPayResult(@RequestParam("orderId") String orderId,
                                 @RequestParam(value = "tryNum",defaultValue = "1",required = false) Integer tryNum) {
        String userId = CurrentUserUtil.getUserId();
        if (userId == null || userId.trim().length() == 0) {
            return ResultVO.serviceFail("用户未登陆");
        }

        if (tryNum >= 4) {
            return ResultVO.serviceFail("订单支付失败，请稍后再试");
        } else {
            AlipayResultVO orderStatus = alipayServiceApi.getOrderStatus(orderId);
            if (orderStatus == null || ToolUtil.isEmpty(orderStatus)) {
                AlipayResultVO alipayResultVO = new AlipayResultVO();
                alipayResultVO.setOrderMsg("订单支付失败");
                alipayResultVO.setOrderStatus(0);
                alipayResultVO.setOrderId(orderId);
                return ResultVO.success(alipayResultVO);
            } else {
                return ResultVO.success(orderStatus);
            }
        }
    }
}
