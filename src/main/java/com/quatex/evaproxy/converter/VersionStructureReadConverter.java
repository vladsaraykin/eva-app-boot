package com.quatex.evaproxy.converter;

import com.datastax.oss.driver.api.core.data.TupleValue;
import com.quatex.evaproxy.entity.VersionStructure;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class VersionStructureReadConverter implements Converter<TupleValue, VersionStructure<?>> {

    @Override
    public VersionStructure<?> convert(TupleValue source) {
        return new VersionStructure<>(source.getObject(0), source.getObject(1));
    }
}
