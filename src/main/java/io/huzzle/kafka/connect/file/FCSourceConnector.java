
package io.huzzle.kafka.connect.file;

import com.github.jcustenborder.kafka.connect.spooldir.SchemaGenerator;
import com.github.jcustenborder.kafka.connect.spooldir.SpoolDirSourceConnector;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;

import java.util.LinkedHashMap;
import java.util.Map;

public class FCSourceConnector extends SpoolDirSourceConnector<FCSourceConnectorConfig> {
    @Override
    protected FCSourceConnectorConfig config(Map<String, String> settings) {
        return new FCSourceConnectorConfig(false, settings);
    }

    @Override
    protected SchemaGenerator<FCSourceConnectorConfig> generator(Map<String, String> settings) {
        throw new NotImplementedException("SchemaGenerator is not implemented for FC");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return FCSourceTask.class;
    }

    @Override
    public ConfigDef config() {
        return FCSourceConnectorConfig.conf();
    }

    @Override
    public void start(Map<String, String> input) {
        this.settings = new LinkedHashMap<>(input);
    }
}
