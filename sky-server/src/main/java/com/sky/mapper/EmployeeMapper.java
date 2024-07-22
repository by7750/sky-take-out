package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee(username,name,password,phone, sex, id_number, status, create_time, update_time,create_user, update_user) " +
            "values(#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int insert(Employee emp);


    Page<Employee> pageQuery(String name);

    int update(Employee emp);

    @Select("select id, username,name,password,phone, sex, id_number, status, create_time, update_time,create_user, update_user from employee where id = #{id}")
    Employee selectById(Long id);
}
