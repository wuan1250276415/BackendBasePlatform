package pro.wuan.common.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class IDModel <T extends Model<?>> extends Model<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JSONField(serializeUsing = ToStringSerializer.class)
    @NotNull(groups = ValidatorUpdateCheck.class, message = "ID不能为空")
    private Long id;

    //需要转换的属性
    @JSONField(serialize = false)
    @JsonIgnore
    @TableField(exist = false)
    protected Set<String> convertFields = new LinkedHashSet<>();

    //不需要转换的属性
    @JSONField(serialize = false)
    @JsonIgnore
    @TableField(exist = false)
    protected Set<String> excludeFields = new LinkedHashSet<>();

    //需要转换的注解
    @JSONField(serialize = false)
    @JsonIgnore
    @TableField(exist = false)
    protected Set<Class> convertAnnotations = new LinkedHashSet<>();

    //不需要转换的属性
    @JSONField(serialize = false)
    @JsonIgnore
    @TableField(exist = false)
    protected Set<Class> excludeAnnotations = new LinkedHashSet<>();

    /**
     * @description 制定转换规则，判断是否需要转换
     *  原则：
     *  	1、禁止优先原则；
     *  	2、最少转换原则；
     * 	规则：
     * 		1、静止需要转换的逻辑优先；
     * 		2、相同的Field不考虑存在多个转换逻辑，如果后续一个Field存在多个转换逻辑，需要扩展转换规则；
     * 		3、如果需要转换的类或注解都为空，则全部转换；
     * 		4、如果需要转换的类或注解存在非空的，则按照非空的进行转换；
     * @param field
     * @return
     */
    public boolean hasConvert(Field field) {
        if(field == null) {
            return false;
        }
        //指定禁止需要转换的字段
        if(excludeFields.contains(field.getName())) {
            return false;
        }
        if(hasAnnotation(field, excludeAnnotations)) {
            return false;
        }
        //允许转换的字段和注解全部为空，则除去不允许转换的之外，其余全部允许转换
        if(convertFields.isEmpty() && convertAnnotations.isEmpty()) {
            return true;
        }
        //存在指定需要转换的字段，
        if(!convertFields.isEmpty()) {
            if(convertFields.contains(field.getName())) {
                return true;
            }
        }
        if(!convertAnnotations.isEmpty()) {
            if(hasAnnotation(field,convertAnnotations)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @description 判断字段是否需要转换
     * @param field
     * @param annotations
     * @return
     */
    public boolean hasAnnotation(Field field,Set<Class> annotations) {
        if(annotations == null || annotations.isEmpty()) {
            return false;
        }
        for (Class annotation : annotations) {
            if(field.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }
}