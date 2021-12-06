package br.com.alura.ecommerce;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.math.BigDecimal;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Order {

    private final String orderId;
    private final BigDecimal amount;
    private final String email;

    public Order(String orderId, BigDecimal amount, String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getOrderId() {
        return orderId;
    }

    public boolean hasAmountGreaterThan(BigDecimal target) {
        return amount.compareTo(target) > 0;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, JSON_STYLE);
    }
}
