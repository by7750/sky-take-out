package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 保存员工
     * @param employeeDTO
     * @return
     */
    boolean save(EmployeeDTO employeeDTO);

    /**
     * 分页
     * @param page
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO page);

    /**
     * 启用和禁用员工账号
     * @param status
     * @param id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 根据id查
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    void editPassword(PasswordEditDTO passwordEditDTO);
}
