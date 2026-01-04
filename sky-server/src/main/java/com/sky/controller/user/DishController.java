package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "用户端菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品,缓存处理
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //不同菜品根据categoryId进行展示，约定不同的category的内存存储格式
        //key ->  dish_{category}
        //value -> 字符串类型，将List<DishVO>序列化存入
        String key = "dish_" + categoryId;

        //查询redis中的缓存数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果redis中存在数据
        if(list != null && !list.isEmpty()) {
            return Result.success(list);
        }
        //如果redis中不存在数据
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        list = dishService.listWithFlavor(dish);

        //存储数据到redis中
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }

}
