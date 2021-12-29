package top.zackyoung.tool.lambda;

import cn.hutool.core.lang.Singleton;
import top.zackyoung.tool.lambda.interfaces.*;

import java.util.ArrayList;
import java.util.List;

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
     * 参数为true或false时，分别进行不同的步骤
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

    public static<T> BranchHandleReturn<T> isTrueOrFalseReturn(boolean b) {
        return (trueHandle, falseHandle) -> {
            if (b) {
                return trueHandle.get();
            } else {
                return falseHandle.get();
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

    /**
     * 条件为true即运行
     */
    public static ConditionHandler2 cd(boolean b) {
        return (runnable) -> {
            if (b) {
                runnable.run();
            }
            return getInstance;
        };
    }

    /**
     * if else
     */
    public static class ifElseBuilder {
        /**
         * 执行标记
         */
        boolean excFlag = false;
        static List<Boolean> list = new ArrayList<>();
        static List<Runnable> runnables = new ArrayList<>();
        static Runnable elseRunable;

        public ifElseBuilder() {
        }


        public ConditionHandler cd(boolean b) {
            list.add(b);
            return (runnable) -> {
                runnables.add(runnable);
                return this;
            };
        }

        //
        public ifElseBuilder elseHandler(Runnable runnable) {
            elseRunable = runnable;
            return this;
        }

        public void exec() {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i)) {
                    runnables.get(i).run();
                    return;
                }
            }
            elseRunable.run();
        }
    }


//    public ConditionHandler cd(boolean b) {
//        return (runnable) -> {
//            if (b) {
//                runnable.run();
//            }
//            return Singleton.get(VUtils.class);
//        };
//    }
}
