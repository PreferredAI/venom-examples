package ai.preferred.crawler;

import ai.preferred.venom.Session;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.response.VResponse;

public interface VenomHandler {
    void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker);
}
