package com.sky.mapper;

import com.sky.entity.User;
import com.sky.vo.DailyUserCountVO;
import com.sky.vo.TotalUserCountVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户数据
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * user表插入一条数据
     * @param user
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time) values (#{openid},#{name},#{phone},#{sex},#{idNumber},#{avatar},#{createTime})")
    void insert(User user);


    /**
     * 根据id返回用户数据
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 根据开始时间和结束时间统计每一天的新用户（当天注册）个数 [)
     * @param begin
     * @param end
     * @return
     */
    @Select("select DATE(create_time) date, count(*) as count from `user` where create_time >= #{begin} and create_time < #{end} group by date ")
    List<DailyUserCountVO> getByBeginAndEnd(@Param("begin") LocalDateTime begin, @Param("end")LocalDateTime end);

    /**
     * 截止到结束时间的每一天的总用户数,)
     * @param end
     * @return
     */
    List<TotalUserCountVO> getToEndTime(LocalDateTime end);
}
