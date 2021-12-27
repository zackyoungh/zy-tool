package top.zackyoung.tool.lambda.interfaces;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/26
 */
@FunctionalInterface
public interface ThrowExceptionFunction {
    /**
     * 抛出异常信息
     *
     * @param message 异常信息
     */
    void throwMessage(String message);
}
