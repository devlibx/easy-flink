package io.github.devlibx.easy.flink.utils.v2.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {
    private EnvironmentConfig environment;
    private Map<String, SourceConfig> sources;
    private RuleEngineConfig ruleEngine;
}