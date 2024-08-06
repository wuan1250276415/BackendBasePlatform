package pro.wuan.common.web.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 分序列化成元
 * 支持 BigDecimal 的数据的小数点。用法 @JsonSerialize(using = YuanSerializer.class)
 * @program: cpm_web
 * @author: HawkWang
 * @create: 2021-12-24 07:27
 **/
public class YuanSerializer extends JsonSerializer<BigDecimal> {
    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles.
     *
     * @param value       Value to serialize; can <b>not</b> be null.
     * @param gen         Generator used to output resulting Json content
     * @param serializers Provider that can be used to get serializers for
     */
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (null != value) {
            gen.writeNumber(value);
        }
    }
}
