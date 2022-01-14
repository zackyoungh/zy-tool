package top.zackyoung;

import cn.hutool.Hutool;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.ExpressionUtil;
import cn.hutool.extra.expression.engine.jexl.JexlEngine;
import cn.hutool.extra.expression.engine.spel.SpELEngine;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Path;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.baidu.hugegraph.structure.gremlin.ResultSet;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import top.zackyoung.tool.json.JSONUtils;
import top.zackyoung.tool.lambda.VUtils;

import javax.json.JsonObject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
        new VUtils.ifElseBuilder().cd(false).handler(() -> {
            System.out.println(1);
        }).cd(false).handler(() -> {
            System.out.println(2);
        }).elseHandler(() -> {
            System.out.println(4);
        }).exec();
        System.out.println(StrUtil.subSuf("f", 1));
        System.out.println(new BigDecimal("2.2558966E7".trim()).toPlainString());
        System.out.println(PageUtil.totalPage(40, 20));
    }

    @Test
    public void JsonTest() {


        JSONObject jsonObject = JSONObject.parseObject(
                "{\"user\":{\"name\":\"张三\",\"x\":{\"y\":[{\"name\":\"李四\"}],\"z\":{\"name\":\"王五\"}}}}"
        );

        User user = JSONUtils.toBean(User.class, jsonObject.getJSONObject("user"));
        System.out.println(user);
        System.out.println(JSONUtils.toBeanStream(X.class, JSONObject.parseArray("[{\"name\":\"张三\"},{\"name\":\"李四\"}]"), (x) -> true).peek(x -> x.setName(x.getName() + "456465")).collect(Collectors.toList()));
        TimeInterval timer = DateUtil.timer();

    }



    public void insert(int length, Object t) {
        JSONObject json = JSONObject.parseObject(JSON.toJSONString(t));
        if (json.containsKey("value") && json.getJSONObject("value").size() < 1) {
            System.out.println(json);
            System.out.print(length);
            return;
        }
        int size = json.size();
        if (json.containsKey("value")) {
            insert(length + 1, json.get("value"));
        } else {
            Set<String> keySet = json.keySet();
            for (String k : keySet) {
                insert(length + 1, json.getJSONObject(k));
            }

        }

    }
}


@Data
@ToString
class User {
    @JSONField(name = "name")
    String name;
    @JSONField(name = "x.y")
    List<X> list;

    @JSONField(name = "x.y.name.m:qw")
    String x;

    @JSONField(name = "x.z")
    Z z;
}

@Data
@ToString
class X {
    @JSONField(name = "name")
    String name;

    private void format() {
        setName(name + "lllll");
    }
}

@Data
@ToString
class Z {
    @JSONField(name = "name")
    String name;

}
