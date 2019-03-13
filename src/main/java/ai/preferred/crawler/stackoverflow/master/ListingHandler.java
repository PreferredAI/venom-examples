/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.preferred.crawler.stackoverflow.master;

import ai.preferred.crawler.EntityCSVStorage;
import ai.preferred.crawler.stackoverflow.entity.Listing;
import ai.preferred.venom.Handler;
import ai.preferred.venom.Session;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.VResponse;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ween Jiann Lee
 */
public class ListingHandler implements Handler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListingHandler.class);

  @Override
  public void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker) {
    LOGGER.info("Processing {}", request.getUrl());

    // Get the job listing array list we created
    final AtomicInteger listingCount = session.get(ListingCrawler.LISTING_COUNT_kEY);

    // Get the CSV printer we created
    final EntityCSVStorage<Listing> csvStorage = session.get(ListingCrawler.CSV_STORAGE_KEY);

    // Get HTML
    final String html = response.getHtml();

    // JSoup
    final Document document = response.getJsoup();

    // We will use a parser class
    final ListingParser.FinalResult finalResult = ListingParser.parse(response);

    // Use this wrapper for every IO task, this maintains CPU utilisation to speed up crawling
    worker.executeBlockingIO(() ->
        finalResult.getListings().forEach(listing -> {
          LOGGER.info("Found job: {} in {} [{}]", listing.getName(), listing.getCompany(), listing.getUrl());
          try {
            // Write record in CSV
            csvStorage.append(listing);
            // Add to the count
            listingCount.incrementAndGet();
          } catch (IOException e) {
            LOGGER.error("Unable to store listing.", e);
          }
        })
    );

    // Crawl another page if there's a next page
    if (finalResult.getNextPage() != null) {
      final String nextPageURL = finalResult.getNextPage();

      // Schedule the next page
      scheduler.add(new VRequest(nextPageURL), this);
    }

  }
}
