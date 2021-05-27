package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;

import java.sql.ResultSet;

/**
 * @author LiuKang on 2021/5/15 17:56
 */
@Data
public class ResultVO<T> {

    private String retMsg;
    // 返回状态【0-成功，1-业务失败，999-表示系统异常】
    private int retCode;
    private T data;
    private String imgPre;
    private Integer nowPage;
    private Integer totalPage;


    private ResultVO(){}

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public static<T> ResultVO success(T data){
        ResultVO<Object> ResultVO = new ResultVO<>();
        ResultVO.setData(data);
        ResultVO.setRetCode(0);
        return ResultVO;
    }

    public static ResultVO serviceFail(String retMsg){
        ResultVO<Object> ResultVO = new ResultVO<>();
        ResultVO.setRetCode(1);
        ResultVO.setRetMsg(retMsg);
        return ResultVO;
    }

    public static ResultVO appFail(String msg){
        ResultVO ResultVO = new ResultVO();
        ResultVO.setRetCode(999);
        ResultVO.setRetMsg(msg);

        return ResultVO;
    }

    public static<T> ResultVO success(String imgPre,T data){
        ResultVO<Object> ResultVO = new ResultVO<>();
        ResultVO.setData(data);
        ResultVO.setRetCode(0);
        ResultVO.setImgPre(imgPre);
        return ResultVO;
    }

    public static<T> ResultVO success(String imgPre,Integer nowPage,Integer totalPage,T data){
        ResultVO<Object> ResultVO = new ResultVO<>();
        ResultVO.setData(data);
        ResultVO.setRetCode(0);
        ResultVO.setImgPre(imgPre);
        ResultVO.setNowPage(nowPage);
        ResultVO.setTotalPage(totalPage);
        return ResultVO;
    }


}
