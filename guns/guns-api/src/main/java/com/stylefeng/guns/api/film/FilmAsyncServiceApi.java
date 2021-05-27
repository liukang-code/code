package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.ActorVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.ImgVO;

import java.util.List;

/**
 * @author LiuKang on 2021/5/17 22:20
 */
public interface FilmAsyncServiceApi {

    FilmDescVO getFilmDesc(String filmId);

//    获取导演信息
    ActorVO getDirector(String filmId);
//    获取演员信息
    List<ActorVO> getActors(String filmId);

    ImgVO getImgs(String filmId);

}
