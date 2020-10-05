package ai.preferred.crawler.example;


import ai.preferred.venom.*;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.*;

public class Example {

    private static class VenomHandler implements Handler {

        @Override
        public void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker) {

            // String about = response.getJsoup().select(".sub-title b").text();
            // System.out.println("ABOUT: " + about);

            // String about = response.getJsoup().select(".info-title").text();
            // System.out.println("info-title: " + about);

            String selector = ".hestia-info p";
            String about = response.getJsoup().select(selector).text();
            System.out.println(selector + " : " + about);
        }
    }

    public static void main(String[] args) throws Exception {
        try (Crawler c = Crawler.buildDefault().start()) {
            Request r = new VRequest("https://venom.preferred.ai");
            c.getScheduler().add(r, new VenomHandler());
        }
    }

}
