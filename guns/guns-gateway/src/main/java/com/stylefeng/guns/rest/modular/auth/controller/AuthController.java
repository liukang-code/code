package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.rest.modular.vo.ResultVO;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Reference(interfaceClass = UserAPI.class,check = false)
    private UserAPI userAPI;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "${jwt.auth-path}")
    public ResultVO createAuthenticationToken(AuthRequest authRequest) {

        boolean validate = true;
        int userId = userAPI.login(authRequest.getUserName(),authRequest.getPassword());
        // 去掉guns自带的用户名密码验证机制，使用我们自己的
        if (userId == 0){
            validate = false;
        }

        if (validate) {
//            randomKey和token已经生成
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken(userId+"", randomKey);
//            返回值
            return ResultVO.success(new AuthResponse(token, randomKey));
        } else {
            return ResultVO.serviceFail("用户名或者密码错误");
        }
    }
}
