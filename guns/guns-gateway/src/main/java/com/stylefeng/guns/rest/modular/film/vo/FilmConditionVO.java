package com.stylefeng.guns.rest.modular.film.vo;

import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author LiuKang on 2021/5/17 15:11
 */
@Data
public class FilmConditionVO implements Serializable {

    private List<CatVO> cats;
    private List<YearVO> years;
    private List<SourceVO> sources;
}
