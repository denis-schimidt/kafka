package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

import static java.lang.String.format;

public class NewGenerateOrderServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        try {
            batchDispatcher.send("ecommerce.all.users.requested", LocalDate.now().toString(), new CorrelationId(NewGenerateOrderServlet.class), "ecommerce.user.report.requested");

            String message = format("Successfully Scheduled User Read Report (Date: %s)", LocalDate.now().toString());

            System.out.println(message);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(message);

        } catch (Exception e) {
            throw new ServletException(e.getCause());
        }
    }
}
