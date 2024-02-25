package com.quatex.evaproxy;

import com.quatex.evaproxy.converter.VersionStructureReadConverter;
import com.quatex.evaproxy.converter.VersionStructureWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CassandraConfig {

    @Bean
    public CassandraCustomConversions customConversions() {

        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(new VersionStructureWriteConverter());
        converters.add(new VersionStructureReadConverter());

        return new CassandraCustomConversions(converters);
    }
}
