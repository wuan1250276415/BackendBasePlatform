package pro.wuan.utils;//package pro.wuan.sys.utils;
//
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import pro.wuan.core.constant.SearchOptConstant;
//import pro.wuan.core.model.BaseQueryValue;
//import pro.wuan.core.model.PageSearchParam;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * <p>
// * 公共工具类
// * <p>
// *
// * @author libra
// * @since
// */
//@Component
//public class ParamUtilUses {
//
//
//    /**
//     * 构建时间查询参数
//     **/
//    public static PageSearchParam jointPageTimeSearchParam(PageSearchParam pageSearchParam, String cs) {
//        //构建参数时间查询参数 "2022-08-10 00:00:00",
//        BaseQueryValue queryValueConsultTime = pageSearchParam.pop(cs);
//        if (queryValueConsultTime != null && queryValueConsultTime.getValues() != null && queryValueConsultTime.getValues().length>1) {
//            String startDate = queryValueConsultTime.getValues()[0].toString().substring(0,10) + " 00:00:00";
//            String endDate = queryValueConsultTime.getValues()[1].toString().substring(0,10) + " 23:59:59";
//            BaseQueryValue baseQueryValue = new BaseQueryValue();
//            baseQueryValue.setOpt(SearchOptConstant.SEARCH_BETWEEN);
//            String[] ss = new String[]{startDate,endDate};
//            baseQueryValue.setValues(ss);
//            pageSearchParam.addQueryParams(cs,baseQueryValue);
//        }
//        return pageSearchParam;
//    }
//
//    /**
//     * 剔除value为空字符串的查询参数,并构造查询条件
//     **/
//    public static PageSearchParam removeParamValueIsEmpty(PageSearchParam pageSearchParam, String sortField, String sortOrder) {
//        Map<String, BaseQueryValue> paramMapl = pageSearchParam.getQueryPrams();
//        Map<String, BaseQueryValue> paramMap = new HashMap<>(10);
//        for (Map.Entry<String, BaseQueryValue> entry : paramMapl.entrySet()) {
//            //剔除无运算符
//            if(StrUtil.isBlank(entry.getValue().getOpt())){
//                continue;
//            }
//            //剔除空value
//            Object[] objArr = entry.getValue().getValues();
//            if(objArr.length==0 || ObjectUtil.isEmpty(objArr[0])){
//                continue;
//            }
//            paramMap.put(entry.getKey(),entry.getValue());
//        }
//        //排序组装
//        if(StrUtil.isBlank(pageSearchParam.getSortField())){
//            pageSearchParam.setSortField(sortField);
//        }
//        if(StrUtil.isBlank(pageSearchParam.getSortOrder())){
//            pageSearchParam.setSortOrder(sortOrder);
//        }
//        if(pageSearchParam.getLimit() == 0 || pageSearchParam.getPage()==0){
//            Assert.notNull(null, "请选择查询页数或查询每页大小！");
//        }
//        return pageSearchParam;
//    }
//
//
//    /**
//     * 动态构建查询参数
//     **/
//    public static PageSearchParam dynamicParam(PageSearchParam pageSearchParam, String[] filedArr, String[] valueArr, String[] optArr) {
//        //遍历数组
//        for (int i=0;i<filedArr.length;i++) {
//            //构造参数发布状态 2：已发布
//            BaseQueryValue baseQueryValue = new BaseQueryValue();
//            baseQueryValue.setOpt(optArr[i]);
//            String[] ss = valueArr[i].split(",");
//            baseQueryValue.setValues(ss);
//            pageSearchParam.addQueryParams(filedArr[i],baseQueryValue);
//        }
//        return pageSearchParam;
//    }
//
//
//
//}
