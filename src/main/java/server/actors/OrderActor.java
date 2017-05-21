package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import server.Server;
import server.workers.Searcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;

public class OrderActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, string -> {
                    String[] stringAsArray = string.split("/@@@");
                    System.out.println(this);
                    String returnMessage = "Order [" + stringAsArray[0] + "] status: ";
                    returnMessage += order(stringAsArray[0]);
                    getSender().tell(stringAsArray[1] + "/@@@" + returnMessage, getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    private String order(String title) {
        try {
            String searchResult = new Searcher(title, Server.getDbs().length).search();
            if ((title + " has not been found.").equals(searchResult)) {
                return "failure (" + searchResult + ")";
            }
            Path ordersFile = Paths.get(Server.getOrdersPath() + Server.getOrdersFileName());
            StandardOpenOption openOption;
            if (Files.exists(ordersFile)) {
                openOption = StandardOpenOption.APPEND;
            }
            else {
                openOption = StandardOpenOption.CREATE;
            }
            synchronized (this) {
                Files.write(ordersFile, (title + "\n").getBytes(), openOption);
            }
        } catch (ExecutionException | IOException | InterruptedException e) {
            e.printStackTrace();
            return "failure";
        }
        return "success";
    }
}
