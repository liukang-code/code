package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaListResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LiuKang on 2021/5/18 12:42
 */
@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = CinemaServiceApi.class,cache = "lru",connections = 10)
    private CinemaServiceApi cinemaServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class,check = false,group = "order2021")
    private OrderServiceApi orderServiceApi;

    @GetMapping("getCinemas")
    public ResultVO getCinemas(CinemaQueryVO cinemaQuery) {
        try {
            CinemaListResponseVO listResponseVO = new CinemaListResponseVO();
            Page<CinemaVO> cinemas = cinemaServiceApi.getCinemas(cinemaQuery);
            if (cinemas.getRecords() == null || cinemas.getRecords().size() == 0) {
                return ResultVO.success("没有影院可查");
            } else {
                return ResultVO.success("", cinemas.getCurrent(), (int) cinemas.getPages(), cinemas.getRecords());
            }
        } catch (Exception e) {
            // 如果出现异常，应该如何处理
            log.error("获取影院列表异常", e);
            return ResultVO.serviceFail("查询影院列表失败");
        }
    }

    //http://localhost:8080/cinema/getCondition?districtId=1&brandId=1&hallType=2
    @GetMapping("getCondition")
    public ResultVO getCondition(CinemaQueryVO cinemaQueryVO) {
        try {
            // 获取三个集合，然后封装成一个对象返回即可
            List<BrandVO> brands = cinemaServiceApi.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVO> areas = cinemaServiceApi.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaServiceApi.getHallTypes(cinemaQueryVO.getHallType());

            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);

            return ResultVO.success(cinemaConditionResponseVO);
        } catch (Exception e) {
            log.error("获取条件列表失败", e);
            return ResultVO.serviceFail("获取影院查询条件失败");
        }
    }

    @GetMapping("getFields")
    public ResultVO getFields(Integer cinemaId) {
        try {
            CinemaInfoVO cinemaInfoById = cinemaServiceApi.getCinemaInfoByCinemaId(cinemaId);

            List<FilmInfoVO> filmInfoByCinemaId = cinemaServiceApi.getFiledInfosByCinemaId(cinemaId);

            CinemaFieldsResponseVO cinemaFieldResponseVO = new CinemaFieldsResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmList(filmInfoByCinemaId);

            return ResultVO.success(IMG_PRE, cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取播放场次失败", e);
            return ResultVO.serviceFail("获取播放场次失败");
        }
    }

    @PostMapping("getFieldInfo")
    public ResultVO getFieldInfo(Integer cinemaId, Integer fieldId) {
        try {

            CinemaInfoVO cinemaInfoById = cinemaServiceApi.getCinemaInfoByCinemaId(cinemaId);
            FilmInfoVO filmInfoByFieldId = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
            HallInfoVO filmFieldInfo = cinemaServiceApi.getFilmFieldInfo(fieldId);

            // 造几个销售的假数据，后续会对接订单接口
            String soldSeats = orderServiceApi.getSoldSeatsByFiledId(fieldId);
            filmFieldInfo.setSoldSeats(soldSeats);

            CinemaFieldResponseVO cinemaFieldResponseVO = new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldResponseVO.setHallInfo(filmFieldInfo);

            return ResultVO.success(IMG_PRE, cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取选座信息失败", e);
            return ResultVO.serviceFail("获取选座信息失败");
        }
    }
}
