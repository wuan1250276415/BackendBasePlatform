package pro.wuan.common.core.model;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.common.core.constant.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Result<T> implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * 结果对象
     */
    private T result;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息内容
     */
    private String message;

    /**
     * Map类的结果对象
     */
    public Map<String, Object> rtnObj;

    public Result(){}

    public Result(int code,String message){
        this.code = code;
        this.message = message;
    }

    public Result(int code,String message,T t){
        this.code = code;
        this.message = message;
        this.result = t;
    }

    public static Result of(int code,String message){
        return of(code,message,null);
    }

    public static <T> Result of(int code,String message, T t){
        return new Result(code,message,t);
    }

    public static <T> Result success(){
        return of(HttpStatus.OK.value(),"执行成功", null);
    }

    public static <T> Result success(T t){
        return of(HttpStatus.OK.value(),"执行成功",t);
    }

    public static <T> Result success(int code, T t){ return of(code,"执行成功",t); }

    public static <T> Result success(int code,String message, T t){
        return of(code,message,t);
    }

    public static <T> Result failure(){ return of(HttpStatus.INTERNAL_SERVER_ERROR.value(),"执行失败", null); }

    public static <T> Result failure(T t){ return of(HttpStatus.INTERNAL_SERVER_ERROR.value(),"执行失败",t); }

    public static <T> Result failure(int code,T t){
        return of(code,"执行失败",t);
    }

    public static <T> Result failure(int code,String message,T t){
        return of(code,message,t);
    }

    public static <T> Result failure(String message){ return of(HttpStatus.INTERNAL_SERVER_ERROR.value(),message,null); }

    public static <T> Result DuplicateSubmitError(String message){ return of(HttpStatus.DUPLICATE_SUBMIT.value(),message,null); }

    public Result putData(String key,Object value){
        if(rtnObj == null){
            rtnObj = new HashMap<>();
        }
        rtnObj.put(key,value);
        return this;
    }

    public Boolean isSuccess(){
        return HttpStatus.OK.value()==code.intValue();
    }

    @Override
    public String toString() {
        return "Result 【code " + code + ";message:" + message + ";object:" + result.toString() + "】";
    }

    /**
     * 转json字符串
     * @return
     */
    public String toJsonString(){
        return JSON.toJSONString(this);
    }
}
