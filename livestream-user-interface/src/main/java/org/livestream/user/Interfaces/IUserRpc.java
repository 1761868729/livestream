package org.livestream.user.Interfaces;

import org.livestream.user.Dto.UserDto;

import java.util.List;
import java.util.Map;

/**
 * @author dream
 * @data 2024/9/9 上午11:39
 * @descripation
 */
public interface IUserRpc {
    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    UserDto getUserById(Long userId);

    /**
     * 批量获取用户信息
     * @param userIds
     * @return
     */
    Map<Long,UserDto> batchQueryUserInfo(List<Long> userIds);

    /**
     * 更新用户信息
     * @param userDto
     * @return
     */
    boolean updateUser(UserDto userDto);

    /**
     * 插入用户信息
     * @param userDto
     * @return
     */
    boolean insertUser(UserDto userDto);

    /**
     * 删除用户信息
     * @param userId
     * @return
     */
    boolean deleteUser(List userId);


}
