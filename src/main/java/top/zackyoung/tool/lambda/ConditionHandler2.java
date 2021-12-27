package top.zackyoung.tool.lambda;


/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@FunctionalInterface
public interface ConditionHandler2 {
    VUtils handler(Runnable runnable);
}
