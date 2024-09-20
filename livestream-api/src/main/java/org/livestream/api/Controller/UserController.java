package org.livestream.api.Controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.livestream.user.Dto.UserDto;
import org.livestream.user.Interfaces.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dream
 * @data 2024/9/13 下午2:11
 * @descripation
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @DubboReference
    private IUserRpc userRpc;

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/getUserInfo")
    public UserDto getUserInfo(Long userId) {
        return userRpc.getUserById(userId);
    }

    /**
     * 批量获取用户信息
     * @param userIds
     * @return
     */
    @GetMapping("/batchQueryUserInfo")
    public Map<Long,UserDto> batchQueryUserInfo(String userIds) {
        return userRpc.batchQueryUserInfo(Arrays.stream(userIds.split(",")).map(Long::parseLong).collect(Collectors.toList()));
    }


    /**
     * 更新用户信息
     * @param userId
     * @param nickName
     * @return
     */
    @GetMapping("/updateUser")
    public boolean updateUser(Long userId, String nickName) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setNickName(nickName);
        return userRpc.updateUser(userDto);
    }

    /**
     * 插入用户信息
     * @param userId
     * @return
     */
    @GetMapping("/insertUser")
    public boolean insertUser(Long userId , String nickName) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setNickName(nickName);
        return userRpc.insertUser(userDto);
    }



    /**
     * 删除用户信息
     * @param userIds
     * @return
     */
    @GetMapping("/deleteUser")
    public boolean deleteUser(List userIds) {
        return userRpc.deleteUser(userIds);
    }
}
