package top.zackyoung.tool.lambda.interfaces;


import top.zackyoung.tool.lambda.VUtils;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@FunctionalInterface
public interface ConditionHandler {
    VUtils.ifElseBuilder handler(Runnable runnable);
}
