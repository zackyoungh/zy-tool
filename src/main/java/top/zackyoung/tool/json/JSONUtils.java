package top.zackyoung.tool.json;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2021/12/24
 */
@SuppressWarnings("all")
@Log4j2
public class JSONUtils {
    /**
     * java常用普通类
     */
    static Set<Class<?>> clazzSet = CollectionUtil.newHashSet(Integer.class, BigDecimal.class, Long.class, Boolean.class, Date.class, Float.class, String.class);

    /**
     * @param clazz 类
     * @param json  json
     * @param <T>   泛型
     * @return bean的实体
     * {@link com.alibaba.fastjson.annotation.JSONField} 解析name，用法和spring的@value差不多
     * 把json转换成bean，类里面包含format方法自动进行格式化数据
     */
    @SneakyThrows
    public static <T> T toBean(Class<T> clazz, JSONObject json) {
        T t = ReflectUtil.newInstance(clazz);
        Field[] declaredFields = ReflectUtil.getFields(clazz);
        for (Field field : declaredFields) {
            JSONField annotation = field.getAnnotation(JSONField.class);
            if (annotation != null) {
                // 映射字段
                field.setAccessible(true);
                String name = annotation.name();
                if (StrUtil.isBlank(name)) {
                    continue;
                }
                // 解析:是否有默认值
                List<String> split = StrUtil.split(name, ":");
                BeanPath resolver = new BeanPath(split.get(0));
                Object o1 = resolver.get(json);
                try {
                    // 设置默认值
                    if (o1 == null && split.size() == 2) {
                        try {
                            o1 = Convert.convert(field.getType(), split.get(1));
                        } catch (Exception e) {
                            log.error("转换类型错误：表达式：{}", name, e);
                        }
                    }
                    // 当对象为 list 时，再 toBeanList一下
                    if (field.getType().equals(List.class)) {
                        Class<?> cla = (Class<?>) TypeUtil.getTypeArgument(TypeUtil.getType(field));
                        List<?> list = o1 == null || ((o1 instanceof List && (!((List) o1).isEmpty()) && ((List) o1).get(0) == null)) ? new ArrayList<>() : toBeanList(cla, (JSONArray) o1);
                        field.set(t, list);
                    }
                    // 当为普通值时赋值
                    else if (clazzSet.contains(field.getType())) {
                        field.set(t, o1);
                    }
                    // 此刻应该为 bean 中bean的了
                    else {
                        Class<?> cla = (Class<?>) TypeUtil.getType(field);
                        Object o = toBean(cla, o1 == null ? null : (JSONObject) o1);
                        field.set(t, o);
                    }
                } catch (Exception e) {
                    log.error("类 ：{}  字段：{}，解析错误，解析标签:{} ", clazz.getName(), field.getName(), name, e);
                }

            }
        }
        // 如果类有format方法则调用
        Method format = ReflectUtil.getMethodByName(clazz, "format");
        Optional.ofNullable(format).ifPresent(x -> ReflectUtil.invoke(t, format));
        return t;
    }

    /**
     * jsonArray转实体，可指定过滤值
     *
     * @param clazz     实体类
     * @param json      json
     * @param condition 过滤条件
     * @param <T>       泛型
     * @return list的实体
     */
    public static <T> List<T> toBeanList(Class<T> clazz, JSONArray json, Function<JSONObject, Boolean> condition) {
        return toBeanList(clazz, json, condition, (x) -> {
        });
    }

    /**
     * 转换流
     *
     * @param clazz     类
     * @param json      json
     * @param condition 过滤条件
     * @param <T>       泛型
     * @return 流
     */
    public static <T> Stream<T> toBeanStream(Class<T> clazz, JSONArray json, Function<JSONObject, Boolean> condition) {
        return json.stream().map(x -> JSONObject.parseObject(JSONObject.toJSONString(x)))
                .filter(condition::apply)
                .map(x -> toBean(clazz, x));
    }

    public static <T> Stream<T> toBeanStream(Class<T> clazz, JSONArray json) {
        return json.stream().map(x -> JSONObject.parseObject(JSONObject.toJSONString(x)))
                .filter(x -> true)
                .map(x -> toBean(clazz, x));
    }

    /**
     * jsonArray转实体，可指定过滤值
     *
     * @param clazz     实体类
     * @param json      json
     * @param condition 过滤条件
     * @param <T>       泛型
     * @param reSet     格式化一部分值
     * @return list的实体
     */
    public static <T> List<T> toBeanList(Class<T> clazz, JSONArray json, Function<JSONObject, Boolean> condition, Consumer<T> reSet) {
        List<T> list = json.stream().map(x -> JSONObject.parseObject(JSONObject.toJSONString(x)))
                .filter(condition::apply)
                .map(x -> toBean(clazz, x))
                .peek(reSet::accept)
                .collect(Collectors.toList());
        return list;
    }


    /**
     * jsonArray转实体，可指定过滤值
     *
     * @param clazz 实体类
     * @param json  json
     * @param <T>   泛型
     * @param reSet 格式化一部分值
     * @return list的实体
     */
    public static <T> List<T> toBeanList(Class<T> clazz, JSONArray json, Consumer<T> reSet) {
        return toBeanList(clazz, json, (x) -> true, reSet);
    }

    /**
     * jsonArray转实体
     *
     * @param clazz 实体类
     * @param json  json
     * @param <T>   泛型
     * @return list的实体
     */
    public static <T> List<T> toBeanList(Class<T> clazz, JSONArray json) {
        return toBeanList(clazz, json, x -> true);
    }

    /**
     * 通过递归解析字段：如  common.user.name  只会解析到user时的对象
     *
     * @param length 长度，一般为0
     * @param json   json对象
     * @param fields 需要解析的字段集
     * @return json
     */
    public static JSONObject parseFiled(int length, JSONObject json, String[] fields) {
        if (length >= fields.length - 1) {
            return json;
        }
        Object o = json.get(fields[length]);
        if (o instanceof JSONArray) {
            JSONArray o1 = (JSONArray) o;
            return (o1.isEmpty() ? null : o1.getJSONObject(0));
        }
        length++;
        JSONObject o1 = (JSONObject) o;

        return parseFiled(length, o1, fields);
    }
}