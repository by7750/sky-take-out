package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 保存员工
     *
     * @param employeeDTO
     * @return
     */
    @Override
    public boolean save(EmployeeDTO employeeDTO) {
        // 拷贝employee
        Employee emp = new Employee();
        BeanUtils.copyProperties(employeeDTO, emp);
        // 设置状态
        emp.setStatus(StatusConstant.ENABLE);
        // 设置默认加密密码
        emp.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 设置创建时间和更新时间
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        // 设置创建人id和更新人id
        Long id = BaseContext.getCurrentId();
        emp.setCreateUser(id);
        emp.setUpdateUser(id);
        int i = employeeMapper.insert(emp);
        if (i != 1) {
            throw new BaseException(MessageConstant.INSERT_ERROR);
        }
        return true;
    }

    /**
     * 员工分页查询
     *
     * @param page 分页参数
     * @return 封装好的页面对象
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO page) {
        PageHelper.startPage(page.getPage(), page.getPageSize());
        Page<Employee> pageInfo = employeeMapper.pageQuery(page.getName());
        return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
    }

    /**
     * 启用和禁用员工账号
     *
     * @param status 状态码
     * @param id     员工id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Employee emp = Employee.builder().id(id).status(status).build();

        int i = employeeMapper.update(emp);

        if (i != 1) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 根据ID查询员工信息
     *
     * @param id 员工id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee != null) {
            employee.setPassword("******");
        }
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置更新时间和更新人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        int cnt = employeeMapper.update(employee);

        if (cnt != 1) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
    }


}
