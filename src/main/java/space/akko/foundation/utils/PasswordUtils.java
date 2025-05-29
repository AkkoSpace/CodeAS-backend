package space.akko.foundation.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

/**
 * 密码工具类
 *
 * @author akko
 * @since 1.0.0
 */
public final class PasswordUtils {

    private PasswordUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 密码复杂度正则表达式
     * 至少包含一个数字、一个小写字母、一个大写字母，长度8-20位
     */
    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");

    /**
     * 中等密码复杂度正则表达式
     * 至少包含数字和字母，长度6-20位
     */
    private static final Pattern MEDIUM_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$");

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        if (StrUtil.isBlank(rawPassword)) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return PASSWORD_ENCODER.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (StrUtil.isBlank(rawPassword) || StrUtil.isBlank(encodedPassword)) {
            return false;
        }
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

    /**
     * 生成随机密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            throw new IllegalArgumentException("密码长度不能小于6位");
        }
        return RandomUtil.randomString(length);
    }

    /**
     * 生成强密码
     */
    public static String generateStrongPassword() {
        StringBuilder password = new StringBuilder();

        // 至少包含一个数字
        password.append(RandomUtil.randomNumbers(2));

        // 至少包含一个小写字母
        password.append(RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz", 2));

        // 至少包含一个大写字母
        password.append(RandomUtil.randomStringUpper(2));

        // 填充剩余位数
        password.append(RandomUtil.randomString(2));

        // 打乱顺序
        return RandomUtil.randomString(password.toString(), 8);
    }

    /**
     * 验证密码强度 - 强
     */
    public static boolean isStrongPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证密码强度 - 中等
     */
    public static boolean isMediumPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return false;
        }
        return MEDIUM_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证密码长度
     */
    public static boolean isValidLength(String password, int minLength, int maxLength) {
        if (StrUtil.isBlank(password)) {
            return false;
        }
        int length = password.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 检查密码是否包含用户名
     */
    public static boolean containsUsername(String password, String username) {
        if (StrUtil.isBlank(password) || StrUtil.isBlank(username)) {
            return false;
        }
        return password.toLowerCase().contains(username.toLowerCase());
    }

    /**
     * 检查密码是否为常见弱密码
     */
    public static boolean isCommonWeakPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return true;
        }

        String[] weakPasswords = {
            "123456", "password", "123456789", "12345678", "12345",
            "1234567", "1234567890", "qwerty", "abc123", "111111",
            "123123", "admin", "letmein", "welcome", "monkey"
        };

        String lowerPassword = password.toLowerCase();
        for (String weak : weakPasswords) {
            if (lowerPassword.equals(weak)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取密码强度等级
     *
     * @param password 密码
     * @return 强度等级：0-弱，1-中等，2-强
     */
    public static int getPasswordStrength(String password) {
        if (StrUtil.isBlank(password)) {
            return 0;
        }

        if (isCommonWeakPassword(password)) {
            return 0;
        }

        if (isStrongPassword(password)) {
            return 2;
        }

        if (isMediumPassword(password)) {
            return 1;
        }

        return 0;
    }

    /**
     * 获取密码强度描述
     */
    public static String getPasswordStrengthDescription(String password) {
        int strength = getPasswordStrength(password);
        return switch (strength) {
            case 0 -> "弱";
            case 1 -> "中等";
            case 2 -> "强";
            default -> "未知";
        };
    }
}
