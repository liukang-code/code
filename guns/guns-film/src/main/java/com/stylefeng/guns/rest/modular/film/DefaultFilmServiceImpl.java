package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuKang on 2021/5/17 0:55
 */
@Component
@Service(interfaceClass = FilmServiceApi.class)
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Autowired
    private MoocBannerTMapper bannerTMapper;

    @Autowired
    private MoocFilmTMapper filmTMapper;

    @Autowired
    private MoocCatDictTMapper catDictTMapper;

    @Autowired
    private MoocYearDictTMapper yearDictTMapper;

    @Autowired
    private MoocSourceDictTMapper sourceDictTMapper;

    @Override
    public List<BannerVO> getBanners() {

        List<MoocBannerT> bannerTs = bannerTMapper.selectList(null);
        List<BannerVO> banners = new ArrayList();
        for (MoocBannerT banner : bannerTs) {
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerUrl(banner.getBannerUrl());
            bannerVO.setBannerId(banner.getUuid() + "");
            bannerVO.setBannerAddress(banner.getBannerAddress());
            banners.add(bannerVO);
        }
        return banners;
    }

    /**
     * 将数据库表Mooc_film_t转化为MoocFilmInfo
     *
     * @param moocFilmTS
     * @return
     */
    public List<FilmInfo> getFilmInfoList(List<MoocFilmT> moocFilmTS) {
        List<FilmInfo> result = new ArrayList<>();
        for (MoocFilmT moocFilmT : moocFilmTS) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));
            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType() + "");
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(moocFilmT.getUuid());
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            result.add(filmInfo);
        }
        return result;
    }

    /**
     * @param isLimit
     * @param nums    获取电影的每页条数限制
     * @return
     */
    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
//        获取所有的热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");

//        判断是否是首页显示的内容
        if (isLimit) {
//            是则限制条数，限制转态是热映
            List<MoocFilmT> films = filmTMapper.selectList(entityWrapper);
            Page<MoocFilmT> filmTPage = new Page<>(1, nums);
            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, entityWrapper);
            filmVO.setFilmInfo(getFilmInfoList(moocFilmTS));
            filmVO.setFilmNum(moocFilmTS.size());
            return filmVO;
        } else {
//            无限制
            Page<MoocFilmT> filmTPage = null;
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_data", yearId);
            }
            if (catId != 99) {
//                一部电影可能属于多个分类中
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }

//            排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
            switch (sortId) {
                case 1:
                    filmTPage = new Page<>(nowPage, nums, "film_box_office");
                    break;
                case 2:
                    filmTPage = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    filmTPage = new Page<>(nowPage, nums, "film_box_office");
                    break;
                default:
                    filmTPage = new Page<>(nowPage, nums, "film_box_office");
                    break;
            }


            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, entityWrapper);

            filmVO.setFilmInfo(getFilmInfoList(moocFilmTS));
            int size = moocFilmTS.size();
            filmVO.setFilmNum(size);
            filmVO.setNowPage(nowPage);
            int totalPage = (size / nums) + 1;
            filmVO.setTotalPage(totalPage);

            return filmVO;
        }
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
//        获取所有的即将上映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");

//        判断是否是首页显示的内容
        if (isLimit) {
//            是则限制条数，限制转态是即将上映
            List<MoocFilmT> films = filmTMapper.selectList(entityWrapper);
            Page<MoocFilmT> filmTPage = new Page<>(1, nums);
            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, entityWrapper);
            filmVO.setFilmInfo( getFilmInfoList(moocFilmTS));
            filmVO.setFilmNum(moocFilmTS.size());
            return filmVO;
        } else {
//            无限制即是获取所有的影片
            Page<MoocFilmT> filmTPage = null;
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_data", yearId);
            }
            if (catId != 99) {
//                一部电影可能属于多个分类中
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }

//            排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
            switch (sortId) {
                case 1:
                    filmTPage = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                case 2:
                    filmTPage = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    filmTPage = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                default:
                    filmTPage = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
            }


            List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, entityWrapper);

            filmVO.setFilmInfo(getFilmInfoList(moocFilmTS));
            int size = moocFilmTS.size();
            filmVO.setFilmNum(size);
            filmVO.setNowPage(nowPage);
            int totalPage = (size / nums) + 1;
            filmVO.setTotalPage(totalPage);

            return filmVO;


        }
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
//        获取所有的即将上映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "3");

        Page<MoocFilmT> filmTPage = null;
        if (sourceId != 99) {
            entityWrapper.eq("film_source", sourceId);
        }
        if (yearId != 99) {
            entityWrapper.eq("film_data", yearId);
        }
        if (catId != 99) {
//                一部电影可能属于多个分类中
            String catStr = "%#" + catId + "#%";
            entityWrapper.like("film_cats", catStr);
        }

//            排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
        switch (sortId) {
            case 1:
                filmTPage = new Page<>(nowPage, nums, "film_box_office");
                break;
            case 2:
                filmTPage = new Page<>(nowPage, nums, "film_time");
                break;
            case 3:
                filmTPage = new Page<>(nowPage, nums, "film_box_office");
                break;
            default:
                filmTPage = new Page<>(nowPage, nums, "film_box_office");
                break;
        }


        List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, entityWrapper);

        filmVO.setFilmInfo(getFilmInfoList(moocFilmTS));
        int size = moocFilmTS.size();
        filmVO.setFilmNum(size);
        filmVO.setNowPage(nowPage);
        int totalPage = (size / nums) + 1;
        filmVO.setTotalPage(totalPage);

        return filmVO;
    }


    @Override
    public List<FilmInfo> getBoxRanking() {
        // 条件 -> 正在上映的，票房前10名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        Page<MoocFilmT> filmTPage = new Page<>(1, 10, "film_box_office");
        List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, wrapper);
        return getFilmInfoList(moocFilmTS);
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        // 条件 -> 即将上映的，预售前10名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        Page<MoocFilmT> filmTPage = new Page<>(1, 10, "film_preSaleNum");
        List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, wrapper);
        return getFilmInfoList(moocFilmTS);
    }

    @Override
    public List<FilmInfo> getTop100() {
        // 条件 -> 正在上映的，评分前10名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        Page<MoocFilmT> filmTPage = new Page<>(1, 10, "film_score");
        List<MoocFilmT> moocFilmTS = filmTMapper.selectPage(filmTPage, wrapper);
        return getFilmInfoList(moocFilmTS);
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> cats = new ArrayList<>();
        List<MoocCatDictT> moocCatDictTS = catDictTMapper.selectList(null);
        for (MoocCatDictT moocCatDictT : moocCatDictTS) {
            CatVO catVO = new CatVO();
            catVO.setCatName(moocCatDictT.getShowName());
            catVO.setCatId(moocCatDictT.getUuid() + "");
            cats.add(catVO);
        }
        return cats;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> years = new ArrayList<>();
        List<MoocYearDictT> moocYearDictTS = yearDictTMapper.selectList(null);
        for (MoocYearDictT moocyearDictT : moocYearDictTS) {
            YearVO yearVO = new YearVO();
            yearVO.setYearName(moocyearDictT.getShowName());
            yearVO.setYearId(moocyearDictT.getUuid() + "");
            years.add(yearVO);
        }
        return years;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sources = new ArrayList<>();
        List<MoocSourceDictT> moocSourceDictTS = sourceDictTMapper.selectList(null);
        for (MoocSourceDictT moocSourceDictT : moocSourceDictTS) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceId(moocSourceDictT.getUuid() + "");
            sourceVO.setSourceName(moocSourceDictT.getShowName());
            sources.add(sourceVO);
        }
        return sources;
    }

    @Override
    public FilmDetailVO getFilmDetail(int searchType, String searchParam) {
        FilmDetailVO filmDetailVO = null;
        // searchType 1-按名称  2-按ID的查找
        switch(searchType){
            case 1:
                filmDetailVO = filmTMapper.getFilmDetailByName("%"+searchParam+"%");
                break;
            default:
                filmDetailVO = filmTMapper.getFilmDetailById(searchParam);
                break;
        }
        return filmDetailVO;
    }
}
