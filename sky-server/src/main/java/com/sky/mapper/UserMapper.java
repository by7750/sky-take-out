package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/28 - 20:27
 * @className UserMapper
 * @since 1.0
 */
@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户
     *
     * @param user 用户信息
     * @return
     */
    int insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 根据条件统计用户数据
     * @param params
     * @return
     */
    int selectCountByMap(Map<String, LocalDate> params);
}
