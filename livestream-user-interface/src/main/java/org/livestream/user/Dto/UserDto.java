package org.livestream.user.Dto;

import jdk.jfr.DataAmount;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author dream
 * @data 2024/9/17 下午8:58
 * @descripation
 */
@Data
@ToString
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
