package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.ByteString;
import server.Server;
import server.utilities.LocalMessage;
import server.workers.Searcher;

import java.io.FileNotFoundException;
import java.util.concurrent.*;

public class SearchActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LocalMessage.class, localMessage -> {
                    String searchResult = search(localMessage.getMessage());
                    localMessage.getActorRef().tell(ByteString.fromString(searchResult), getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    private String search(String title) throws FileNotFoundException, ExecutionException, InterruptedException {
        int n = Server.getDbs().length;
        return new Searcher(title, n).search();
    }
}
