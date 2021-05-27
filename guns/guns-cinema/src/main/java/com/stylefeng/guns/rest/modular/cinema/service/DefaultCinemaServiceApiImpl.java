package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuKang on 2021/5/18 12:42
 */
@Component
@Service(interfaceClass = CinemaServiceApi.class,executes = 10)
public class DefaultCinemaServiceApiImpl implements CinemaServiceApi {

    @Autowired
    private MoocCinemaTMapper cinemaTMapper;

    @Autowired
    private MoocAreaDictTMapper areaDictTMapper;

    @Autowired
    private MoocBrandDictTMapper brandDictTMapper;

    @Autowired
    private MoocHallDictTMapper hallDictTMapper;

    @Autowired
    private MoocFieldTMapper fieldTMapper;

    @Autowired
    private MoocHallFilmInfoTMapper hallFilmInfoTMapper;

    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if (cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType() != 99) {
            entityWrapper.like("hall_ids", "%#" + cinemaQueryVO.getBrandId() + "#%");
        }

        Page<CinemaVO> cinemaVOPage = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());
        List<MoocCinemaT> moocCinemaTS = cinemaTMapper.selectPage(cinemaVOPage, entityWrapper);

//        数据实体转化为业务实体
        List<CinemaVO> cinemas = new ArrayList<>();
        for (MoocCinemaT moocCinemaT : moocCinemaTS) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(moocCinemaT.getUuid() + "");
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice() + "");
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());

            cinemas.add(cinemaVO);
        }

        long counts = cinemaTMapper.selectCount(entityWrapper);

        Page<CinemaVO> result = new Page();
        result.setRecords(cinemas);
        result.setTotal(counts);
        result.setSize(cinemaQueryVO.getPageSize());
        return result;
    }

    @Override
    public List<BrandVO> getBrands(int brandId) {
        MoocBrandDictT moocBrandDictT = brandDictTMapper.selectById(brandId);
        List<MoocBrandDictT> brands = brandDictTMapper.selectList(null);

        List<BrandVO> brandVOS = new ArrayList<>();

        boolean flag = false;
        if (brandId == 99 || moocBrandDictT == null || moocBrandDictT.getUuid() == null) {
            flag = true;
        }
        for (MoocBrandDictT brand : brands) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(brand.getUuid() + "");
            brandVO.setBrandName(brand.getShowName());
            if (flag) {
                if (brand.getUuid() == 99) {
                    brandVO.setActive(true);
                }
            } else {
                if (brand.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }

            brandVOS.add(brandVO);
        }

        return brandVOS;
    }

    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areaVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocAreaDictT moocAreaDictT = areaDictTMapper.selectById(areaId);
        // 判断brandId 是否等于 99
        if (areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = areaDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for (MoocAreaDictT area : moocAreaDictTS) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid() + "");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if (flag) {
                if (area.getUuid() == 99) {
                    areaVO.setActive(true);
                }
            } else {
                if (area.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }

            areaVOS.add(areaVO);
        }

        return areaVOS;
    }

    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> hallTypeVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocHallDictT moocHallDictT = hallDictTMapper.selectById(hallType);
        // 判断brandId 是否等于 99
        if (hallType == 99 || moocHallDictT == null || moocHallDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocHallDictT> moocHallDictTS = hallDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for (MoocHallDictT hall : moocHallDictTS) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHallTypeName(hall.getShowName());
            hallTypeVO.setHallTypeId(hall.getUuid() + "");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if (flag) {
                if (hall.getUuid() == 99) {
                    hallTypeVO.setActive(true);
                }
            } else {
                if (hall.getUuid() == hallType) {
                    hallTypeVO.setActive(true);
                }
            }

            hallTypeVOS.add(hallTypeVO);
        }

        return hallTypeVOS;
    }

    @Override
    public CinemaInfoVO getCinemaInfoByCinemaId(int cinemaId) {

        MoocCinemaT moocCinemaT = cinemaTMapper.selectById(cinemaId);
        if (moocCinemaT == null) {
            return new CinemaInfoVO();
        }
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid() + "");
        cinemaInfoVO.setCinemaAddress(moocCinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }

    @Override
    public List<FilmInfoVO> getFiledInfosByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos = fieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }

    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {
        return fieldTMapper.getHallInfo(fieldId);
    }

    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        return fieldTMapper.getFilmInfoById(fieldId);
    }

    @Override
    public OrderQueryVO getOrderNeeds(String filedId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        MoocFieldT moocFieldT = fieldTMapper.selectById(filedId);
        orderQueryVO.setCinemaId(moocFieldT.getCinemaId()+"");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice()+"");

        return orderQueryVO;
    }
}
