package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务类
 *
 * @author yao
 * @version 1.0
 * @date 2024/7/25 - 1:10
 * @className SetmealServiceImpl
 * @since 1.0
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐业务
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        // 拷贝属性
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        // 执行新增套餐
        int cnt = setmealMapper.insert(setmeal);
        if (cnt != 1) {
            throw new BaseException(MessageConstant.INSERT_ERROR);
        }
        // 执行新增套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
//        setmealDishes.forEach(setmealDish -> {
//            int i = setmealDishMapper.insert(setmealDish);
//            if (i != 1) {
//                throw new BaseException(MessageConstant.INSERT_ERROR);
//            }
//        });
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        if (null == setmealDishes || setmealDishes.size() == 0) {
            return;
        }
        int i = setmealDishMapper.insertBatch(setmealDishes);
        if (i != setmealDishes.size()) {
            throw new BaseException(MessageConstant.INSERT_ERROR);
        }
    }

    /**
     * 分页查套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.selectPage(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查套餐
     *
     * @param id 套餐id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 查
        Setmeal setmeal = setmealMapper.selectById(id);
        if (null == setmeal) {
            return null;
        }
        SetmealVO setmealVO = new SetmealVO();
        // 拷贝
        BeanUtils.copyProperties(setmeal, setmealVO);
        // 查询菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @Override
    public void removeBatch(Long[] ids) {
        // 判空
        if (null == ids || ids.length == 0) {
            return;
        }
        // 上架的套餐不能删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (setmeal.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 开始删除
        int cnt = setmealMapper.deleteBatch(ids);
        if (cnt != ids.length) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
    }


    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {
        // 判空
        if (setmealDTO == null || setmealDTO.getId() == null) {
            return;
        }
        // 拷贝属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 修改
        int cnt = setmealMapper.update(setmeal);
        if (cnt != 1) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
        // 修改菜品
        // 删除之后重新添加
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        int i = setmealDishMapper.deleteBySetmealId(setmeal.getId());
        // 给菜品赋上新的套餐id，添加菜品
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        int scnt = setmealDishMapper.insertBatch(setmealDishes);
        if (scnt != setmealDishes.size()) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }

    }

    /**
     * 修改套餐状态
     *
     * @param status 状态码，1为上架，0为下架
     * @param id     套餐id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        int cnt = setmealMapper.update(setmeal);

        if (cnt != 1) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
