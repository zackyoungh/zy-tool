package top.zackyoung.tool.hugegraph;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.copier.Copier;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Path;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.baidu.hugegraph.structure.gremlin.Result;
import com.baidu.hugegraph.structure.gremlin.ResultSet;
import top.zackyoung.tool.lambda.VUtils;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/31
 */
public class HugegraphUtils {
    static String host;
    static String port;
    static HugeClient hugeClient = HugeClient.builder("http://10.255.57.139:8080",
                    "hugegraph")
            .build();
    /**
     * 小数转百分比
     */
    static NumberFormat nf = NumberFormat.getPercentInstance();

    static {
        nf.setMaximumFractionDigits(2);
    }

    /**
     * 获取企业的子节点
     *
     * @param name 公司名
     * @return
     */
    public static JSONArray getChildNode(String name) {
        ResultSet resultSet = hugeClient.gremlin()
                .gremlin("g.V().has(\"name\",\"" + name + "\").outE().inV().path()").execute();
        JSONArray array = new JSONArray();
        resultSet.iterator().forEachRemaining(result -> {
            List<Object> objects = result.getPath().objects();
            Edge edge = (Edge) objects.get(1);
            double regRate = Double.parseDouble(edge.properties().get("stock_percent").toString());
            // 保留投资比例大于0的
            if (regRate <= 0) {
                return;
            }
            Vertex vertex = (Vertex) objects.get(2);
            JSONObject json = new JSONObject();
            json.putAll(vertex.properties());
            // 投资比例百分比
            json.put("regRate", nf.format(regRate));
            // 是否有子节点
            json.put("isChild", false);
            array.add(json);
        });
        return array;
    }

    public static Set<String> getChildNodeIsChild(String name) {
        ResultSet resultSet = hugeClient.gremlin()
                .gremlin("g.V().has(\"name\",\"" + name + "\").out(\"e_invest\").outE().inV().path()").execute();
        Map<String, Boolean> isExistMap = new HashMap<>();
//        Iterable<Result> iterable = resultSet::iterator;
//        StreamSupport.stream(iterable.spliterator(),false)
//                        .map(Result::getPath)
//                                .filter(x->{
//                                    Edge edge = (Edge) x.objects().get(2);
//                                    Object stockPercent = edge.properties().get("stock_percent");
//                                    double aDouble = Double.parseDouble(stockPercent.toString());
//                                    return aDouble > 0;
//                                })
//                .map(x->((Vertex) x.objects().get(1)).properties().get("eid").toString())
//                .collect(Collectors.toSet());
        resultSet.iterator().forEachRemaining(result -> {
            Path path = (Path) result.getObject();
            Vertex vertex1 = (Vertex) path.objects().get(1);
            String eid = vertex1.properties().get("eid").toString();
            if (!isExistMap.getOrDefault(eid, false)) {
                Edge edge = (Edge) path.objects().get(2);
                Object stockPercent = edge.properties().get("stock_percent");
                double aDouble = Double.parseDouble(stockPercent.toString());
                if (aDouble > 0) {
                    isExistMap.put(eid, true);
                }
            }
        });
        return isExistMap.keySet();
    }


    public static void main(String[] args) {
        String name = "重庆正大软件（集团）有限公司";
        JSONArray childNode = getChildNode(name);
        Set<String> childNodeIsChild = getChildNodeIsChild(name);
        childNode.forEach(x -> {
            JSONObject json = (JSONObject) x;
            if (childNodeIsChild.contains(json.getString("eid"))) {
                json.put("isChild", true);
            }
        });
        System.out.println(childNode);
    }
}
