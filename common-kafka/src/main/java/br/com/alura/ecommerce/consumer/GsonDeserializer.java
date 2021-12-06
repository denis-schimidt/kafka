package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.KafkaMessage;
import br.com.alura.ecommerce.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer implements Deserializer<KafkaMessage> {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(KafkaMessage.class, new MessageAdapter()).create();

    @Override
    public KafkaMessage deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), KafkaMessage.class);
    }
}
