package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.UUID;

public class NewOrderServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        try (var orderDispatcher = new KafkaDispatcher<>()) {
            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));

            var userId = UUID.randomUUID().toString();
            var correlationId = new CorrelationId(NewOrderServlet.class);

            var order = new Order(userId, amount, email);
            orderDispatcher.send("ecommerce.new.order", userId, correlationId, order);

            System.out.println("New order sent successfully");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent");

        } catch (Exception e) {
            throw new ServletException(e.getCause());
        }
    }
}
