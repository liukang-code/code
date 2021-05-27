package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUserUtil;
import com.stylefeng.guns.rest.modular.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author LiuKang on 2021/5/16 17:01
 */
@RestController
@RequestMapping("/user/")
public class UserController {

    @Reference(interfaceClass = UserAPI.class,check = false)
    private UserAPI userAPI;

    @PostMapping("/register")
    public ResultVO register(UserModel userModel) {
        if (userModel.getUsername() == null || userModel.getUsername().trim().length() == 0) {
            return ResultVO.serviceFail("用户名不能为空");
        }
        if (userModel.getPassword() == null || userModel.getPassword().trim().length() == 0) {
            return ResultVO.serviceFail("密码不能为空");
        }
//        验证用户名是否可用
        if (!userAPI.checkUsername(userModel.getUsername())){
            return ResultVO.serviceFail("用户名重复");
        }

        boolean register = userAPI.register(userModel);
        if (register) {
            return ResultVO.success("注册成功");
        } else {
            return ResultVO.serviceFail("注册失败");
        }
    }

    @GetMapping("check")
    public ResultVO check(String username){
        if(username!=null && username.trim().length()>0){
            // 当返回true的时候，表示用户名可用
            boolean notExists = userAPI.checkUsername(username);
            if (notExists){
                return ResultVO.success("用户名不存在");
            }else{
                return ResultVO.serviceFail("用户名已存在");
            }
        }else{
            return ResultVO.serviceFail("用户名不能为空");
        }
    }

    @GetMapping("logout")
    public ResultVO logout(){
        /*
            应用：
                1、前端存储JWT 【七天】 ： JWT的刷新
                2、服务器端会存储活动用户信息【30分钟】
                3、JWT里的userId为key，查找活跃用户
            退出：
                1、前端删除掉JWT
                2、后端服务器删除活跃用户缓存
            现状：
                1、前端删除掉JWT
         */


        return ResultVO.success("用户退出成功");
    }


    @GetMapping("getUserInfo")
    public ResultVO getUserInfo(){
        // 获取当前登陆用户
        String userId = CurrentUserUtil.getUserId();
        if(userId != null && userId.trim().length()>0){
            // 将用户ID传入后端进行查询
            int uuid = Integer.parseInt(userId);
            UserInfoModel userInfo = userAPI.getUserInfo(uuid);
            if(userInfo!=null){
                return ResultVO.success(userInfo);
            }else{
                return ResultVO.appFail("用户信息查询失败");
            }
        }else{
            return ResultVO.serviceFail("用户未登陆");
        }
    }

    @PostMapping("updateUserInfo")
    public ResultVO updateUserInfo(UserInfoModel userInfoModel){
        // 获取当前登陆用户
        String userId = CurrentUserUtil.getUserId();
        if(userId != null && userId.trim().length()>0){
            // 将用户ID传入后端进行查询
            int uuid = Integer.parseInt(userId);
            // 判断当前登陆人员的ID与修改的结果ID是否一致
            if(uuid != userInfoModel.getUuid()){
                return ResultVO.serviceFail("请修改您个人的信息");
            }

            UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel);
            if(userInfo!=null){
                return ResultVO.success(userInfo);
            }else{
                return ResultVO.appFail("用户信息修改失败");
            }
        }else{
            return ResultVO.serviceFail("用户未登陆");
        }
    }
}
