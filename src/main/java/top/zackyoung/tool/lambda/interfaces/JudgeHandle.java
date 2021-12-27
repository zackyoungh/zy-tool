package top.zackyoung.tool.lambda.interfaces;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@FunctionalInterface
public interface JudgeHandle {
    /**
     * 判断处理
     *
     * @param runnables 集合
     */
    void judge(Runnable... runnables);
}
