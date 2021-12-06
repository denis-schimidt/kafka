package br.com.alura.ecommerce;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class User {
    private String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, JSON_STYLE);
    }
}
