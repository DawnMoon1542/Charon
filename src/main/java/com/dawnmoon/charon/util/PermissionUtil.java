package com.dawnmoon.charon.util;

/**
 * 权限工具类
 * 提供权限编码相关的工具方法
 */
public class PermissionUtil {

    /**
     * 权限编码分隔符
     */
    private static final String SEPARATOR = ":";

    /**
     * 权限编码格式正则表达式
     * 格式：模块:操作，如 USER:CREATE, ROLE:UPDATE
     */
    private static final String PERMISSION_CODE_PATTERN = "^[A-Z_]+:[A-Z_]+$";

    /**
     * 生成权限编码
     *
     * @param module 模块名称（大写字母和下划线）
     * @param action 操作名称（大写字母和下划线）
     * @return 权限编码
     */
    public static String generatePermissionCode(String module, String action) {
        if (module == null || action == null) {
            throw new IllegalArgumentException("模块和操作不能为空");
        }
        return module.toUpperCase() + SEPARATOR + action.toUpperCase();
    }

    /**
     * 解析权限编码
     *
     * @param permissionCode 权限编码
     * @return [0]:模块, [1]:操作
     */
    public static String[] parsePermissionCode(String permissionCode) {
        if (!isValidPermissionCode(permissionCode)) {
            throw new IllegalArgumentException("权限编码格式不正确: " + permissionCode);
        }
        return permissionCode.split(SEPARATOR);
    }

    /**
     * 获取权限编码的模块部分
     *
     * @param permissionCode 权限编码
     * @return 模块名称
     */
    public static String getModule(String permissionCode) {
        String[] parts = parsePermissionCode(permissionCode);
        return parts[0];
    }

    /**
     * 获取权限编码的操作部分
     *
     * @param permissionCode 权限编码
     * @return 操作名称
     */
    public static String getAction(String permissionCode) {
        String[] parts = parsePermissionCode(permissionCode);
        return parts[1];
    }

    /**
     * 验证权限编码格式是否正确
     *
     * @param permissionCode 权限编码
     * @return true-格式正确, false-格式错误
     */
    public static boolean isValidPermissionCode(String permissionCode) {
        return permissionCode != null && permissionCode.matches(PERMISSION_CODE_PATTERN);
    }

    /**
     * 判断权限编码是否属于指定模块
     *
     * @param permissionCode 权限编码
     * @param module         模块名称
     * @return true-属于, false-不属于
     */
    public static boolean belongsToModule(String permissionCode, String module) {
        if (!isValidPermissionCode(permissionCode)) {
            return false;
        }
        return getModule(permissionCode).equals(module.toUpperCase());
    }

    /**
     * 判断权限编码是否为指定操作
     *
     * @param permissionCode 权限编码
     * @param action         操作名称
     * @return true-是, false-否
     */
    public static boolean isAction(String permissionCode, String action) {
        if (!isValidPermissionCode(permissionCode)) {
            return false;
        }
        return getAction(permissionCode).equals(action.toUpperCase());
    }
}

