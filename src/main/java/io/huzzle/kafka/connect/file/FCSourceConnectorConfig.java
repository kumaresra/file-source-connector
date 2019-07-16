
package io.huzzle.kafka.connect.file;

import com.github.jcustenborder.kafka.connect.spooldir.SpoolDirSourceConnectorConfig;
import com.github.jcustenborder.kafka.connect.utils.config.ConfigKeyBuilder;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.errors.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;

class FCSourceConnectorConfig extends SpoolDirSourceConnectorConfig {
    private static final Logger log = LoggerFactory.getLogger(FCSchemaGenerator.class);

    private static final String FC_CHARSET = "fc.file.charset";
    private static final String FC_CHARSET_DISPLAY = "File character set.";

    private static final String FC_CHARSET_DOC = "Character set to read the file with.";
    private static final String FC_CHARSET_DEFAULT = Charset.defaultCharset().name();

    private static final String CSV_GROUP = "CSV Parsing";
    public final Charset charset;

    public FCSourceConnectorConfig(final boolean isTask, Map<String, ?> settings) {
        super(isTask, conf(), settings);

        String charsetName = this.getString(FCSourceConnectorConfig.FC_CHARSET);
        this.charset = Charset.forName(charsetName);
    }

    static ConfigDef conf() {
        return SpoolDirSourceConnectorConfig.config()
                .define(
                        ConfigKeyBuilder.of(FC_CHARSET, ConfigDef.Type.STRING)
                                .defaultValue(FC_CHARSET_DEFAULT)
                                .validator(CharsetValidator.of())
                                .importance(ConfigDef.Importance.LOW)
                                .documentation(FC_CHARSET_DOC)
                                .displayName(FC_CHARSET_DISPLAY)
                                .group(CSV_GROUP)
                                .width(ConfigDef.Width.LONG)
                                .build()
                );
    }

    @Override
    public boolean schemasRequired() {
        return false;
    }

    static class CharsetValidator implements ConfigDef.Validator {
        static CharsetValidator of() {
            return new CharsetValidator();
        }

        @Override
        public void ensureValid(String s, Object o) {
            try {
                Preconditions.checkState(o instanceof String);
                String input = (String) o;
                Charset.forName(input);
            } catch (IllegalArgumentException e) {
                throw new DataException(String.format("Charset '%s' is invalid for %s", o, s), e);
            }
        }

        @Override
        public String toString() {
            return Joiner.on(",").join(Charset.availableCharsets().keySet());
        }
    }
}
