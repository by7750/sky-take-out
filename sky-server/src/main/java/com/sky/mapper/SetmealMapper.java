package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into setmeal(category_id, name, price, description, image, create_time, update_time, create_user, update_user, status) " +
            "values(#{categoryId},#{name},#{price},#{description},#{image},#{createTime},#{updateTime},#{createUser}, #{updateUser}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Setmeal setmeal);


    /**
     * 分页查
     * @param setmealPageQueryDTO 页面信息
     * @return
     */
    Page<Setmeal> selectPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查
     * @param id
     * @return
     */
    @Select("select id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user " +
            "from setmeal " +
            "where id = #{id}")
    Setmeal selectById(Long id);

    /**
     * 批量删除
     * @param ids 需要删除的id
     * @return
     */
    int deleteBatch(Long[] ids);

    /**
     * 修改套餐信息
     * @param setmeal
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    int update(Setmeal setmeal);


    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
