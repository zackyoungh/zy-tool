package top.zackyoung.tool.lambda;

import cn.hutool.core.lang.Singleton;
import top.zackyoung.tool.lambda.interfaces.*;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@SuppressWarnings("all")
public class VUtils {
    public static VUtils getInstance = Singleton.get(VUtils.class);
    /**
     * 如果参数为true抛出异常
     *
     * @param b 参数
     * @return 表达式
     */
    public static ThrowExceptionFunction isTrue(boolean b) {
        return (msg) -> {
            if (b) {
                throw new RuntimeException(msg);
            }
        };
    }

    /**
     * 参数为true或false时，分别进行不同的参数
     *
     * @param b 条件逻辑
     * @return lambda
     */
    public static BranchHandle isTrueOrFalse(boolean b) {
        return (trueHandle, falseHandle) -> {
            if (b) {
                trueHandle.run();
            } else {
                falseHandle.run();
            }
        };
    }

    /**
     * 判断逻辑进行对应的处理逻辑
     *
     * @param b 条件逻辑
     * @return lambda
     */
    public static JudgeHandle judge(boolean... b) {
        return (runnables) -> {
            isTrue(runnables.length != b.length).throwMessage("条件逻辑与运行数不匹配");
            for (int i = 0; i < b.length; i++) {
                if (b[i]) {
                    runnables[i].run();
                }
            }
        };
    }

    /**
     * 参数为true或false时，分别进行不同的操作
     *
     * @param str 字符串
     * @return lambda
     */
    public static PresentOrElseHandler<?> isBlankOrNoBlank(String str) {
        return (consumer, runnable) -> isTrueOrFalse(str == null || str.length() == 0).trueOrFalseHandle(runnable, () -> consumer.accept(str));
    }


    public ConditionHandler cd(boolean b) {
        return (runnable) -> {
            if (b) {
                runnable.run();
            }
            return Singleton.get(VUtils.class);
        };
    }
}
