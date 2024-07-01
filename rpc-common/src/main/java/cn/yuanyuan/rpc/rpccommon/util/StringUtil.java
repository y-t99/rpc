package cn.yuanyuan.rpc.rpccommon.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 *
 * @author wuyitao
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 分割固定格式的字符串
     */
    public static String[] split(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }
}
