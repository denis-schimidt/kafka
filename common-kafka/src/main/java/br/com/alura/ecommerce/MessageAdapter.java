package br.com.alura.ecommerce;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<KafkaMessage>, JsonDeserializer<KafkaMessage> {

    @Override
    public JsonElement serialize(KafkaMessage kafkaMessage, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("correlationId", context.serialize(kafkaMessage.getCorrelationId()));
        jsonObject.add("payload", context.serialize(kafkaMessage.getPayload()));
        jsonObject.addProperty("type", kafkaMessage.getPayload().getClass().getName());

        return jsonObject;
    }

    @Override
    public KafkaMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        var jsonObject = jsonElement.getAsJsonObject();
        var payloadType = jsonObject.get("type").getAsString();

        try {
            var payload = context.deserialize(jsonObject.get("payload"), Class.forName(payloadType));
            CorrelationId correlationId = (CorrelationId) context.deserialize(jsonObject.get("correlationId"), CorrelationId.class);

            return new KafkaMessage(correlationId, payload);

        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getCause());
        }
    }
}
