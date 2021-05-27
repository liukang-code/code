package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * @author LiuKang on 2021/5/17 0:51
 */
@RestController
@RequestMapping("/film/")
public class FilmController {

    public static final String IMG_PRE = "http://img.meetingshop.cn/";
    @Reference(interfaceClass = FilmServiceApi.class)
    private FilmServiceApi filmServiceApi;

    @Reference(interfaceClass = FilmAsyncServiceApi.class,async = true)
    private FilmAsyncServiceApi filmAsyncServiceApi;

    @GetMapping("getIndex")
    public ResultVO getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        // 获取正在热映的电影
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true, 8, 1, 99, 99, 99, 99));
        // 即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true, 8, 1, 99, 99, 99, 99));
        // 票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        // 获取受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        // 获取前一百
        filmIndexVO.setTop100(filmServiceApi.getTop100());

        return ResultVO.success(IMG_PRE, filmIndexVO);
    }


    @GetMapping("getConditionList")
    public ResultVO getConditionList(@RequestParam(value = "catId", required = false, defaultValue = "99") String catId,
                                     @RequestParam(value = "yearId", required = false, defaultValue = "99") String yearId,
                                     @RequestParam(value = "sourceId", required = false, defaultValue = "99") String sourceId) {
        FilmConditionVO filmConditionVO = new FilmConditionVO();

//        分类列表
        List<CatVO> cats = filmServiceApi.getCats();
        Map<String, Integer> map = new HashMap<>();
        for (CatVO catVO : cats) {
            map.put(catVO.getCatId(), cats.indexOf(catVO));
        }
        Integer index = map.get(catId);
        if (index != null) {
            cats.get(index).setActive(true);
        }
        filmConditionVO.setCats(cats);

//        年份列表
        List<YearVO> years = filmServiceApi.getYears();
        Map<String, Integer> map2 = new HashMap<>();
        for (YearVO yearVO : years) {
            map.put(yearVO.getYearId(), years.indexOf(yearVO));
        }
        Integer index2 = map.get(yearId);
        if (index2 != null) {
            years.get(index2).setActive(true);
        }
        filmConditionVO.setYears(years);

//        来源列表
        List<SourceVO> sources = filmServiceApi.getSources();
        Map<String, Integer> map3 = new HashMap<>();
        for (SourceVO sourceVO : sources) {
            map.put(sourceVO.getSourceId(), sources.indexOf(sourceVO));
        }
        Integer index3 = map.get(sourceId);
        if (index3 != null) {
            sources.get(index3).setActive(true);
        }
        filmConditionVO.setSources(sources);

        return ResultVO.success(filmConditionVO);
    }

    //影片查询接口
    @GetMapping("getFilms")
    public ResultVO getFilms(FilmRequestVO filmRequestVO) {
        FilmVO filmVO = null;

        Integer showType = filmRequestVO.getShowType();
        switch (showType) {
            case 2:
                filmVO = filmServiceApi.getSoonFilms(false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            case 3:
                filmVO = filmServiceApi.getClassicFilms(filmRequestVO.getPageSize(), filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            default: // 没有传入showType或者传入的值为1，都是走这个
                filmVO = filmServiceApi.getHotFilms(false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
        }

        return ResultVO.success(IMG_PRE, filmVO.getNowPage(), filmVO.getTotalPage(),filmVO);
    }

    @GetMapping("films/{searchParam}")
    public ResultVO getFilms(@PathVariable("searchParam") String searchParam,int searchType) throws ExecutionException, InterruptedException {
        FilmDetailVO filmDetail = filmServiceApi.getFilmDetail(searchType, searchParam);

        if(filmDetail==null){
            return ResultVO.serviceFail("没有可查询的影片");
        }else if(filmDetail.getFilmId()==null || filmDetail.getFilmId().trim().length()==0){
            return ResultVO.serviceFail("没有可查询的影片");
        }

        String filmId = filmDetail.getFilmId();
//        Dubbo的异步调用
        filmAsyncServiceApi.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();
        filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVO> imgVOFuture = RpcContext.getContext().getFuture();

        filmAsyncServiceApi.getDirector(filmId);
        Future<ActorVO> directorVOFuture = RpcContext.getContext().getFuture();
        filmAsyncServiceApi.getActors(filmId);
        Future<List<ActorVO>> actorsVOFuture = RpcContext.getContext().getFuture();

//        组装filmDetail对象中属性info04的值
        InfoRequestVO infoRequestVO = new InfoRequestVO();
        infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());
        infoRequestVO.setFilmId(filmId);
        infoRequestVO.setImgs(imgVOFuture.get());
    //        获取ActorRequest的的两个值，并封装到info中
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setDirector(directorVOFuture.get());
        actorRequestVO.setActors(actorsVOFuture.get());
        infoRequestVO.setActors(actorRequestVO);

        filmDetail.setInfo04(infoRequestVO);

        return ResultVO.success(IMG_PRE,filmDetail);
    }
}
