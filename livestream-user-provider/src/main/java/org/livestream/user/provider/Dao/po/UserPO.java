package org.livestream.user.provider.Dao.po;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author dream
 * @data 2024/9/17 下午8:49
 * @descripation
 */
@Data
@ToString
@TableName("t_user")
public class UserPO {

    @TableId(type = IdType.INPUT)
    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer sex;
    private Integer workCity;
    private Integer bornCity;
    private Date bornDate;
    private Date createTime;
    private Date updateTime;

}
