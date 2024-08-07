package pro.wuan.utils;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 公共工具类
 * @author: oldone
 * @date: 2023/11/15 18:06
 */
@Slf4j
@Component
public class CommonUtil {
    public static final int pageSize = 500;
    //设置每次的发送数量
    public static final int sendPageSize = 50;

    /** 自定义页数分组
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        //(先计算出余数)
        int remainder = source.size() % n;
        //然后是商
        int number = source.size() / n;
        //偏移量
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


    /** 自定义每页大小分组
     * @param source 需拆分集合
     * @param MAX_SEND 每页大小
     */
    public static <T> List<List<T>> averageAssignByPage(List<T> source, int MAX_SEND) {
        int limit = countStep(source.size(),MAX_SEND);
        //方法一：使用流遍历操作
        List<List<T>> mglist = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            mglist.add(source.stream().skip((long) i * MAX_SEND).limit(MAX_SEND).collect(Collectors.toList()));
        });
        return mglist;
    }

    /**
     * 计算切分次数
     */
    private static Integer countStep(Integer size,int MAX_SEND) {
        return (size + MAX_SEND - 1) / MAX_SEND;
    }


    /**
     * 生成uuid
     */
    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        return uuid.toString().replace("-","");

    }

    public static String incrementString(String input) {
        // 检查字符串是否为空或全为非数字字符
        if (StrUtil.isNotBlank(input) || !input.matches(".*\\d")) {
            return input;
        }

        // 从后向前遍历，找到第一个数字字符的索引
        int startOfNumber = input.length() - 1;
        while (startOfNumber >= 0 && !Character.isDigit(input.charAt(startOfNumber))) {
            startOfNumber--;
        }

        // 提取数字部分并将其转换为整数
        String numberPart = input.substring(startOfNumber);
        int number = Integer.parseInt(numberPart);

        // 将数字加1
        int incrementedNumber = number + 1;

        // 将新数字转换回字符串，并替换原字符串中的数字部分
        String incrementedNumberStr = String.valueOf(incrementedNumber);
        return input.substring(0, startOfNumber) + incrementedNumberStr;
    }
}
