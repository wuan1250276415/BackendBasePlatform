package pro.wuan.common.web.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 元反序列化成元
 * 支持字符串、浮点、整数的BigDecimal 一般配合DecimalMax和DecimalMin进行校验
 *    @YuanMax(value = "10000.10", message = "预算项目金额 不能大于{value}元", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
 *    @YuanMin(value = "100.10", message = "预算项目金额 不能小于{value}元", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
 *    @JsonDeserialize(using = YuanDeserializer.class)
 * @program: cpm_web
 * @author: HawkWang
 * @create: 2021-12-26 07:27
 **/
public class YuanDeserializer extends JsonDeserializer<BigDecimal> {
    /**
     * Method that can be called to ask implementation to deserialize
     * JSON content into the value type this serializer handles.
     * Returned instance is to be constructed by method itself.
     * <p>
     * Pre-condition for this method is that the parser points to the
     * first event that is part of value to deserializer (and which
     * is never JSON 'null' literal, more on this below): for simple
     * types it may be the only value; and for structured types the
     * Object start marker or a FIELD_NAME.
     * </p>
     * <p>
     * The two possible input conditions for structured types result
     * from polymorphism via fields. In the ordinary case, Jackson
     * calls this method when it has encountered an OBJECT_START,
     * and the method implementation must advance to the next token to
     * see the first field name. If the application configures
     * polymorphism via a field, then the object looks like the following.
     * <pre>
     *      {
     *          "@class": "class name",
     *          ...
     *      }
     *  </pre>
     * Jackson consumes the two tokens (the <tt>@class</tt> field name
     * and its value) in order to learn the class and select the deserializer.
     * Thus, the stream is pointing to the FIELD_NAME for the first field
     * after the @class. Thus, if you want your method to work correctly
     * both with and without polymorphism, you must begin your method with:
     * <pre>
     *       if (p.getCurrentToken() == JsonToken.START_OBJECT) {
     *         p.nextToken();
     *       }
     *  </pre>
     * This results in the stream pointing to the field name, so that
     * the two conditions align.
     * <p>
     * Post-condition is that the parser will point to the last
     * event that is part of deserialized value (or in case deserialization
     * fails, event that was not recognized or usable, which may be
     * the same event as the one it pointed to upon call).
     * <p>
     * Note that this method is never called for JSON null literal,
     * and thus deserializers need (and should) not check for it.
     *
     * @param p    Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *             this deserialization activity.
     * @return Deserialized value
     */
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        switch (p.getCurrentTokenId()) {
            case JsonTokenId.ID_NUMBER_INT:
                switch (p.getNumberType()) {
                    case INT:
                    case LONG:
                    case BIG_INTEGER:
                        return new BigDecimal(p.getBigIntegerValue());
                }
                break;
            case JsonTokenId.ID_NUMBER_FLOAT:
                BigDecimal wanyuan = p.getDecimalValue();
                if(getNumberOfDecimalPlace(wanyuan) > 2){
                    throw new IllegalArgumentException(String.format("%s小数精度必须小于等于2",wanyuan));
                }
                return wanyuan;
            case JsonTokenId.ID_START_ARRAY:
                throw new IllegalArgumentException("不支持数组参数");
            case JsonTokenId.ID_STRING: // let's do implicit re-parse
                String text = p.getText().trim();
                if (StringUtils.isNotBlank(text)) {
                    BigDecimal textWanyuan = new BigDecimal(text);
                    if(getNumberOfDecimalPlace(textWanyuan) > 2){
                        throw new IllegalArgumentException(String.format("%s小数精度必须小于等于2",textWanyuan));
                    }
                    return textWanyuan;
                }
        }
        BigDecimal unexpetBigDecimal = (BigDecimal) ctxt.handleUnexpectedToken(BigDecimal.class, p);
        if(getNumberOfDecimalPlace(unexpetBigDecimal) > 2){
            throw new IllegalArgumentException(String.format("%s小数精度必须小于等于2",unexpetBigDecimal));
        }
        return unexpetBigDecimal;

    }

    private int getNumberOfDecimalPlace(BigDecimal value) {
        final String s = value.toPlainString();
        final int index = s.indexOf('.');
        if (index < 0) {
            return 0;
        }
        return s.length() - 1 - index;
    }
}
