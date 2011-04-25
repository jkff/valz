package org.valz.viewer;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.model.AggregateFormat;
import org.valz.model.AggregateRegistry;
import org.valz.backends.ReadBackend;
import org.valz.backends.RemoteReadException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.valz.viewer.HtmlBuilder.html;
import static org.valz.viewer.HtmlBuilder.text;

public class ValzWebHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzWebHandler.class);

    private final ReadBackend readBackend;
    private final AggregateRegistry aggregateRegistry;

    public ValzWebHandler(ReadBackend readBackend, AggregateRegistry aggregateRegistry) {
        this.readBackend = readBackend;
        this.aggregateRegistry = aggregateRegistry;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
            throws IOException, ServletException
    {
        try {
            HtmlBuilder tbody = html("tbody");

            tbody.addChild(html("tr").children(
                        html("td").children(text("Name")),
                        html("td").children(text("Aggregate name")),
                        html("td").children(text("Aggregate data")),
                        html("td").children(text("Value"))));
            for(String var : readBackend.listVals()) {
                tbody.addChild(html("tr").children(
                        html("td").children(text(var)),
                        html("td").children(text(readBackend.getValue(var).getAggregate().getName())),
                        html("td").children(text(AggregateFormat.toJson(aggregateRegistry, readBackend.getValue(var).getAggregate()))),
                        html("td").children(text(readBackend.getValue(var).getValue()))));
            }

            HtmlBuilder table = html("table", "border", 1).children(tbody);


            response.getWriter().write(
                html("html").children(
                        html("head").children(html("title").children(text("Valz"))),
                        html("body").children(table))
                    .toString(new StringBuilder()).toString());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (RemoteReadException e) {
            log.error("RemoteReadException: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        } finally {
            ((Request)request).setHandled(true);
        }
        log.info("The request was completed.");
    }
}
