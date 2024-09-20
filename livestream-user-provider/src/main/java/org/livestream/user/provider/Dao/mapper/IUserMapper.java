package org.livestream.user.provider.Dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.livestream.user.Dto.UserDto;
import org.livestream.user.provider.Dao.po.UserPO;

/**
 * @author dream
 * @data 2024/9/17 下午8:50
 * @descripation
 */
@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {
}
