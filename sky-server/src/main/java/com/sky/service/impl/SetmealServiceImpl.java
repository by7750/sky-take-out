package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
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
        if(null ==setmealDishes || setmealDishes.size() == 0){
            return ;
        }
        int i = setmealDishMapper.insertBatch(setmealDishes);
        if (i != setmealDishes.size()) {
            throw new BaseException(MessageConstant.INSERT_ERROR);
        }
    }
}
