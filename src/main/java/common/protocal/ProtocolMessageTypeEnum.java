package common.protocal;

import lombok.Getter;

@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);
    private final int key;

    ProtocolMessageTypeEnum(int value) {
        this.key = value;
    }

    /**
     * 根据 value 获取枚举 对应的消息类型
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }
}
