package com.quatex.evaproxy.converter;

import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.internal.core.data.DefaultTupleValue;
import com.datastax.oss.driver.internal.core.type.DefaultTupleType;
import com.quatex.evaproxy.entity.VersionStructure;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.List;

@WritingConverter
public class VersionStructureWriteConverter implements Converter<VersionStructure<?>, TupleValue> {

    @Override
    public TupleValue convert(VersionStructure<?> source) {
        if (source.getCurrentValue() instanceof String) {
            return new DefaultTupleValue(
                    new DefaultTupleType(List.of(DataTypes.TEXT, DataTypes.TEXT)),
                    source.getCurrentValue(), source.getNewValue());
        }
        if (source.getCurrentValue() instanceof Integer) {
            return new DefaultTupleValue(
                    new DefaultTupleType(List.of(DataTypes.INT, DataTypes.INT)),
                    source.getCurrentValue(), source.getNewValue());
        }
        return null;
    }
}
