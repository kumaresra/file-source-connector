
package io.huzzle.kafka.connect.file;

import com.github.jcustenborder.kafka.connect.spooldir.SchemaGenerator;
import org.apache.kafka.connect.data.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class FCSchemaGenerator extends SchemaGenerator<FCSourceConnectorConfig> {
    private static final Logger log = LoggerFactory.getLogger(FCSchemaGenerator.class);

    public FCSchemaGenerator(Map<String, ?> settings) {
        super(settings);
    }

    @Override
    protected FCSourceConnectorConfig config(Map<String, ?> settings) {
        return new FCSourceConnectorConfig(false, settings);
    }

    @Override
    protected Map<String, Schema.Type> determineFieldTypes(InputStream inputStream) throws IOException {
        return new LinkedHashMap<>();
    }
}
