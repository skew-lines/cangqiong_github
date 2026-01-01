package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
     * 新增菜品和对应的口味数据
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //菜品表插入一条数据,设置了主键回显
        dishMapper.insert(dish);

        Long dishId = dish.getId();
        //口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null || !flavors.isEmpty()) {
            //为每一条数据添加dishId
            flavors.forEach(item -> item.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //拦截下一次select语句
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> dishes = (Page<DishVO>) dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(dishes.getTotal(),dishes.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除---是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除---是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);

        if(setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联口味数据
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //数据库操作优化
        //批量删除菜品
        dishMapper.deleteByIds(ids);
        //删除菜品关联口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO getWithFlavorById(Long id) {
        //根据id查询菜品信息
        Dish dish = dishMapper.getById(id);
        //根据dishId查询口味信息
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);
        //封装VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        //categoryName在前端分页查询的时候已经通过id:name的形式保存了，这里就不用返回了
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品信息
        dishMapper.update(dish);
        //删除原有口味信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //添加现有口味信息
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        //判断口味信息存在
        if(dishFlavors != null && !dishFlavors.isEmpty()) {
            //添加dishId
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            //批量插入
            dishFlavorMapper.insertBatch(dishFlavors);
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }

}
