package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/28 - 0:16
 * @className UserService
 * @since 1.0
 */
public interface UserService {

    User wxLogin(UserLoginDTO loginDTO);
}
