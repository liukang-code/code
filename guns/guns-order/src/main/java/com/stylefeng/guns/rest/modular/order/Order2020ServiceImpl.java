package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2020TMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2020TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2020T;
import com.stylefeng.guns.rest.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuKang on 2021/5/18 20:55
 */
@Slf4j
@Component
@Service(interfaceClass = OrderServiceApi.class,group = "order2020")
public class Order2020ServiceImpl implements OrderServiceApi {

    // 搭建FTP服务器失败，采用本地读取
//    @Autowired
//    private FTPUtil ftpUtil;

    @Autowired
    private MoocOrder2020TMapper moocOrder2020TMapper;

    @Reference(interfaceClass = CinemaServiceApi.class,check = false)
    private CinemaServiceApi cinemaServiceApi;

    //    验证是否为真实的座位编号
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
//          根据filedId找到
        String seatPath = moocOrder2020TMapper.getSeatsByFieldId(fieldId);
//        String address = ftpUtil.getFileStringByAddress(seatPath);
        String address = FileUtil.getJson(seatPath);
        JSONObject jsonObject = JSONObject.parseObject(address);
        String ids = jsonObject.get("ids").toString();

        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue = 0;
        for (String id : idArrs) {
            for (String seat : seatArrs) {
                if (seat.equalsIgnoreCase(id)) {
                    isTrue++;
                }
            }
        }
        if (seatArrs.length == isTrue) {
            return true;
        } else {
            return false;
        }
    }

    // 判断购票人的选座是否已经被卖出 false-已卖出 true-座位可用
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);
        List<MoocOrder2020T> list = moocOrder2020TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");
//          有任何一个编号匹配上，则直接返回失败
        for (MoocOrder2020T moocOrder2020T : list) {
            String[] ids = moocOrder2020T.getSeatsIds().split(",");
            for (String id : ids) {
                for (String seat : seatArrs) {
                    if (id.equalsIgnoreCase(seat)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public OrderVO saveOrderInfo(Integer filedId, String soldSeats, String seatsName, Integer userId) {
//        编号
        String uuid = UUIDUtil.genUUID();
//        影片信息
        FilmInfoVO filmInfoVO = cinemaServiceApi.getFilmInfoByFieldId(filedId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());
//        获取影院信息
        OrderQueryVO orderNeeds = cinemaServiceApi.getOrderNeeds(filedId + "");
        Integer cinemaId = Integer.parseInt(orderNeeds.getCinemaId());
        Double filmPrice = Double.parseDouble(orderNeeds.getFilmPrice());
//        求订单总金额
        int nums = soldSeats.split(",").length;
        Double totalPrice = getTotalPrice(filmPrice, nums);

//        插入订单实体
        MoocOrder2020T moocOrder2020T = new MoocOrder2020T();
        moocOrder2020T.setUuid(uuid);
        moocOrder2020T.setSeatsName(seatsName);
        moocOrder2020T.setSeatsIds(soldSeats);
        moocOrder2020T.setOrderUser(userId);
        moocOrder2020T.setOrderPrice(totalPrice);
        moocOrder2020T.setFilmPrice(filmPrice);
        moocOrder2020T.setFilmId(filmId);
        moocOrder2020T.setFieldId(filedId);
        moocOrder2020T.setCinemaId(cinemaId);

        Integer result = moocOrder2020TMapper.insert(moocOrder2020T);
        if (result > 0) {
            OrderVO orderVO = moocOrder2020TMapper.getOrderInfoById(uuid);
            if (orderVO == null || orderVO.getOrderId() == null) {
                log.error("获取订单实体出错");
                return null;
            } else {
                return orderVO;
            }
        } else {
            log.error("插入订单实体出错");
            return null;
        }
    }

    /**
     * 使用BigDecimal计算总价
     *
     * @param filmPrice
     * @param nums
     * @return
     */
    private Double getTotalPrice(Double filmPrice, Integer nums) {
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);
        BigDecimal numsDeci = new BigDecimal(nums);
        BigDecimal result = filmPriceDeci.multiply(numsDeci);
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    @Override
    public Page<OrderVO> getOrderInfoByUserId(Integer userId, Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        List<OrderVO> orders = moocOrder2020TMapper.getOrdersInfoByUserId(userId);
        if (orders == null || orders.size() == 0) {
            result.setTotal(0);
            result.setRecords(new ArrayList<>());
            return result;
        } else {
            EntityWrapper<MoocOrder2020T> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("order_user",userId);
            Integer count = moocOrder2020TMapper.selectCount(entityWrapper);
            result.setTotal(count);
            result.setRecords(orders);
            return result;
        }
    }

    @Override
    public String getSoldSeatsByFiledId(Integer filedId) {
        if (filedId == null) {
            log.error("请输入场次号，再查询已售座位");
            return "";
        } else {
            String seats = moocOrder2020TMapper.getSoldSeatsByFieldId(filedId);
            return seats;
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {
        return null;
    }

    @Override
    public boolean paySuccess(String orderId) {
        return false;
    }

    @Override
    public boolean payFail(String orderId) {
        return false;
    }
}
