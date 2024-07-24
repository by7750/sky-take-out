package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/24 - 1:30
 * @className DishServiceImpl
 * @since 1.0
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品和对应口味数据
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        // 向菜品表插入一条
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        int cnt = dishMapper.insert(dish);
        if (cnt != 1) {
            throw new BaseException(MessageConstant.INSERT_ERROR);
        }
        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors == null || flavors.size() == 0) {
            return;
        }
        // 设置菜品id
        flavors.forEach(e -> e.setDishId(dish.getId()));
        int count = dishFlavorMapper.insertBatch(flavors);
        if (count != flavors.size()) {
            throw new BaseException(MessageConstant.INSERT_ERROR);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.selectPage(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断菜品是否能够删除
        ids.forEach(id -> {
            Dish dish = dishMapper.selectById(id);
            // 是否启售？
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        // 是否被套餐关联
        List<Long> setids = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (null != setids && setids.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品数据
        int cnt = 0;
//        for (Long id : ids) {
//            cnt += dishMapper.deleteById(id);
//            // 删除对应口味数据
//            int fcnt = dishFlavorMapper.deleteByDishId(id);
//            log.info("删除{}号菜品和对应{}种口味", id, fcnt);
//        }
        cnt = dishMapper.deleteByIds(ids);
        if (cnt != ids.size()) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }

        int fcnt = dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品信息和对应的口味信息
     *
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 更新菜品数据
        int cnt = dishMapper.update(dish);
        if (cnt != 1) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
        // 删除口味数据再重新插入
        Long dishId = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(dishId);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (null == flavors || flavors.size() == 0) {
            return;
        }
        // 设置dishid
        flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
        int i = dishFlavorMapper.insertBatch(flavors);
        if (i != flavors.size()) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }

    }

    /**
     * 修改菜品售卖状态
     *
     * @param status 1为启售，0为停售
     * @param id     菜品id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = Dish.builder().id(id).status(status).build();
        int update = dishMapper.update(dish);
        if (update != 1) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 根据分类查菜品
     * @param categoryId 分类id
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId);
    }
}
