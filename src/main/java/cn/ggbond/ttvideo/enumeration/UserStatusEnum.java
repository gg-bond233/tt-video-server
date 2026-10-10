package cn.ggbond.ttvideo.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserStatusEnum {

    ENABLE(0, "启用"),
    DISABLE(1, "禁用");

    @EnumValue
    private final Integer id;
    private final String userStatus;

    UserStatusEnum(Integer id, String userStatus) {
        this.id = id;
        this.userStatus = userStatus;
    }

    /**
     * Jackson 序列化：枚举 -> JSON 输出 id（0/1）
     * 与前端 el-switch 的 activeValue={0} / inactiveValue={1} 对齐
     */
    @JsonValue
    public Integer getId() {
        return id;
    }

    /**
     * Jackson 反序列化：JSON 输入 id（0/1）-> 枚举
     * 兼容前端传入数字 0/1，也兼容字符串 "0"/"1" 以及枚举名 "ENABLE"/"DISABLE"
     */
    @JsonCreator
    public static UserStatusEnum fromValue(Object value) {
        if (value == null) {
            return null;
        }
        // 数字
        if (value instanceof Number) {
            int code = ((Number) value).intValue();
            return fromId(code);
        }
        // 字符串
        String s = value.toString().trim();
        // 数字字符串
        try {
            int code = Integer.parseInt(s);
            return fromId(code);
        } catch (NumberFormatException ignored) {
            // 不是数字，继续按枚举名匹配
        }
        // 枚举名
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(s))
                .findFirst()
                .orElse(null);
    }

    private static UserStatusEnum fromId(int code) {
        return Arrays.stream(values())
                .filter(e -> e.id.equals(code))
                .findFirst()
                .orElse(null);
    }
}
