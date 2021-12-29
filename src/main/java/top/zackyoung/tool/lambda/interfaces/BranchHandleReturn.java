package top.zackyoung.tool.lambda.interfaces;

import java.util.function.Supplier;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@FunctionalInterface
public interface BranchHandleReturn<T> {
    /**
     * 分支操作
     *  @param trueHandle  为true时要进行的操作
     * @param falseHandle 为false时要进行的操作
     * @return
     */
    T trueOrFalseHandle(Supplier<T> trueHandle, Supplier<T> falseHandle);
}
