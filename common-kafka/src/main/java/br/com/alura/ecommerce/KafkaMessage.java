package br.com.alura.ecommerce;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class KafkaMessage<T> {
    private final CorrelationId correlationId;
    private final T payload;

    public KafkaMessage(CorrelationId correlationId, T payload) {
        this.correlationId = correlationId;
        this.payload = payload;
    }

    public CorrelationId getCorrelationId() {
        return correlationId;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("correlationId", correlationId)
                .append("payload", payload)
                .toString();
    }
}
