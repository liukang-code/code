package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author LiuKang on 2021/5/18 0:47
 */
@Data
public class ActorRequestVO implements Serializable {

    private ActorVO director;
    private List<ActorVO> actors;
}
