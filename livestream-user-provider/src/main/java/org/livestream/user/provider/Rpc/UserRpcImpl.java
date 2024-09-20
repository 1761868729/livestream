package org.livestream.user.provider.Rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.livestream.user.Dto.UserDto;
import org.livestream.user.Interfaces.IUserRpc;
import org.livestream.user.provider.Seveice.IUserService;

import java.util.List;
import java.util.Map;

/**
 * @author dream
 * @data 2024/9/9 上午11:42
 * @descripation
 */
@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    @Override
    public UserDto getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * 批量获取用户信息
     * @param userIds
     * @return
     */
    @Override
    public Map<Long, UserDto> batchQueryUserInfo(List<Long> userIds) {
        return userService.batchQueryUserInfo(userIds);
    }

    /**
     * 更新用户信息
     * @param userDto
     * @return
     */
    @Override
    public boolean updateUser(UserDto userDto) {
        return userService.updateUser(userDto);
    }

    /**
     * 插入用户信息
     * @param userDto
     * @return
     */
    @Override
    public boolean insertUser(UserDto userDto) {
        return userService.insertUser(userDto);
    }

    /**
     * 删除用户信息
     * @param userIds
     * @return
     */
    @Override
    public boolean deleteUser(List userIds) {
        return userService.deleteUser(userIds);
    }
}
