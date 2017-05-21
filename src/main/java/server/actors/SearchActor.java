package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import server.Server;
import server.workers.Searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

public class SearchActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, string -> {
                    String[] stringAsArray = string.split("/@@@");
                    String searchResult = search(stringAsArray[0]);
                    getSender().tell(stringAsArray[1] + "/@@@" + searchResult, getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    private String search(String title) {
        int n = Server.getDbs().length;
        try {
            return new Searcher(title, n).search();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
