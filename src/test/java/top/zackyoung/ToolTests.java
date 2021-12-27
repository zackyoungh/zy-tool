package top.zackyoung;

import org.junit.jupiter.api.Test;
import top.zackyoung.tool.lambda.VUtils;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/27
 */
public class ToolTests {
    @Test
    public void test1() {
        if (true) {
            System.out.println(1);
        } else if (true) {
            System.out.println(2);
        } else if (false) {
            System.out.println(3);
        } else {
            System.out.println(4);
        }

        System.out.println("\n");
         new VUtils.ifElseBuilder().cd(false).handler(()->{
            System.out.println(1);
        }).cd(false).handler(()->{
            System.out.println(2);
        }).elseHandler(()->{
            System.out.println(4);
        }).exec();
    }
}
