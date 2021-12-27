package top.zackyoung.tool.lambda.interfaces;

import java.util.function.Consumer;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@FunctionalInterface
public interface PresentOrElseHandler<T> {
    /**
     * 值不为空时执行消费操作
     * 值为空时执行其他操作
     * @param action 值不为空时，执行的消费操作
     * @param emptyAction 值空时，执行的操作
     */
    void presentOrElseHandle(Consumer<? super T> action,Runnable emptyAction);
}
