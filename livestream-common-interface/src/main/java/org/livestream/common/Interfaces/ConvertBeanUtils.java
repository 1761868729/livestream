package org.livestream.common.Interfaces;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dream
 * @data 2024/9/17 下午9:21
 * @descripation
 */
public class ConvertBeanUtils {
    /**
     * 将源对象转换成目标对象
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T t = newInstance(targetClass);
        BeanUtils.copyProperties(source, t);
        return t;
    }

    /**
     * 将List对象转换成目标对象，注意实现是ArrayList
     *
     * @param targetClass
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> List<T> convertList(List<K> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        List targetList = new ArrayList((int)(sourceList.size()/0.75) + 1);
        for (K source : sourceList) {
            targetList.add(convert(source, targetClass));
        }
        return targetList;
    }

    private static <T> T newInstance(Class<T> targetClass) {
        try {
            return targetClass.newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException(targetClass, "instantiation error", e);
        }
    }
}
