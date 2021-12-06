package br.com.alura.ecommerce;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.math.BigDecimal;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Order {

    private final String userId;
    private final BigDecimal amount;
    private final String email;

    public Order(String userId, BigDecimal amount, String email) {
        this.userId = userId;
        this.amount = amount;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, JSON_STYLE);
    }
}
