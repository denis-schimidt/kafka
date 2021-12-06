package br.com.alura.ecommerce;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CorrelationId {
    private final UUID uuid;
    private final List<String> sequenceComponents;
    private final String targetTopic;

    public CorrelationId(Class<?> componentClass) {
        this(UUID.randomUUID(), List.of(componentClass.getSimpleName()), null);
    }

    public CorrelationId(Class<?> componentClass, String targetTopic) {
        this(UUID.randomUUID(), List.of(componentClass.getSimpleName()), targetTopic);
    }

    private CorrelationId(UUID uuid, List<String> components, String targetTopic){
        this.uuid = uuid;
        this.sequenceComponents = components;
        this.targetTopic = targetTopic;
    }

    public CorrelationId continueWith(Class<?> componentClass) {
        this.sequenceComponents.add(componentClass.getSimpleName());

        return new CorrelationId(uuid, sequenceComponents, targetTopic);
    }

    public CorrelationId continueWith(String targetTopic) {
        return new CorrelationId(uuid, sequenceComponents, targetTopic);
    }

    @Override
    public String toString() {
        String allComponents = sequenceComponents.stream().collect(Collectors.joining(","));
        var template = StringUtils.isBlank(targetTopic) ? "%s (%s)" : "%s (%s) -> %s";

        return String.format(template, uuid, allComponents, targetTopic);
    }
}
