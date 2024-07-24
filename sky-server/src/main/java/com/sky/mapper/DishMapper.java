package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 插入一条记录
     *
     * @param dish
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int insert(Dish dish);

    /**
     * 分页查
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> selectPage(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @Delete("delete from dish where id = #{id}")
    int deleteById(Long id);

    /**
     * 根据多个id删除
     *
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 修改菜品信息
     * @param dish
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    int update(Dish dish);

    @Select("select * from dish where category_id = #{id}")
    List<Dish> selectByCategoryId(Long categoryId);
}
