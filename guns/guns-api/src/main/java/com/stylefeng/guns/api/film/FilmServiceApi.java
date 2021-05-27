package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

/**
 * @author LiuKang on 2021/5/17 0:56
 */
public interface FilmServiceApi {

//    获取Banners
    List<BannerVO> getBanners();
//    获取热映影片
    FilmVO getHotFilms(boolean isLimit,int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
//    获取即将上映的影片（受欢迎程度排序）
    FilmVO getSoonFilms(boolean isLimit,int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
//    获取经典影片
    FilmVO getClassicFilms(int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
//    获取票房排行榜
    List<FilmInfo> getBoxRanking();
//    获取人气排行榜
    List<FilmInfo> getExpectRanking();
//    获取top100
    List<FilmInfo> getTop100();

    List<CatVO> getCats();

    List<YearVO> getYears();

    List<SourceVO> getSources();

    FilmDetailVO getFilmDetail(int searchType,String searchParam);


}
