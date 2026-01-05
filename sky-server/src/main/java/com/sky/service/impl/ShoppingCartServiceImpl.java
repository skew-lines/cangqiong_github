package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        //判断当前加入购物车的菜品/套餐是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && !list.isEmpty()) {
            //已经存在，则数量加1
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            //数据库更新
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //不存在，则插入数据
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null) {
                //添加的是菜品，查询菜品
                Dish dish = dishMapper.getById(dishId);
                //设置插入数据
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }
            Long setmealId = shoppingCartDTO.getSetmealId();
            if(setmealId != null) {
                //添加的是套餐，查询套餐
                Setmeal setmeal = setmealMapper.getById(setmealId);
                //设置插入数据
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

        }



    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> showShoppingCart() {
        return shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    public void deleteSub(ShoppingCartDTO shoppingCartDTO) {
        //如果个数 > 1 个数-1， 如果个数 == 1，删除
        //先查询对应的记录,其实返回的最多只有一条数据,因为是删除所以肯定有且只有一条数据返回
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && !list.isEmpty()) {
            //获得数据
            ShoppingCart cart = list.get(0);
            //个数只有一个
            if(cart.getNumber() == 1) {
                shoppingCartMapper.deleteById(cart.getId());
            }
            else {
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(cart);
            }
        }
    }
}
