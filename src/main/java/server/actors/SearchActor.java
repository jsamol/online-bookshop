package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import server.Server;

import java.util.*;
import java.util.concurrent.*;

public class SearchActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, string -> {
                    String searchResult = search(string);
                    getSender().tell(searchResult, getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    private String search(String title) {
        int n = Server.getDbs().length;
        ExecutorService executorService = Executors.newFixedThreadPool(n);
        List<CompletableFuture<String>> completableFutures = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            executorService.submit(() -> {
                // TODO: do search
                String result = "";
                completableFuture.complete(result);
            });
            completableFutures.add(completableFuture);
        }
        Set<String> searchResults = new HashSet<>();
        for (CompletableFuture<String> completableFuture : completableFutures) {
            try {
                searchResults.add(completableFuture.get());
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            } // TODO: handle exceptions
        }

        if (searchResults.size() > 1) {
            for (String searchResult : searchResults) {
                if (searchResult.startsWith(title)) {
                    return searchResult;
                }
            }
        }
        else {
            return searchResults.iterator().next();
        }

        return null;
    }
}
