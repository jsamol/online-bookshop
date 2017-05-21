package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import scala.concurrent.duration.Duration;
import server.Server;
import server.utilities.LocalMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class StreamActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LocalMessage.class, localMessage -> {
                    final Path book = Paths.get(Server.getBooksPath() + localMessage.getMessage() + ".txt");
                    if (Files.exists(book)) {
                        final Materializer materializer = ActorMaterializer.create(context());
                        FileIO.fromPath(book)
                                .via(Framing.delimiter(ByteString.fromString("."), 256))
                                .throttle(1, Duration.create(1, TimeUnit.SECONDS),
                                        1, ThrottleMode.shaping())
                                .runForeach(
                                        sentence -> localMessage.getActorRef().
                                                tell(ByteString.fromString(sentence.decodeString("UTF-8") + "."),
                                                getSelf()), materializer);
                    }
                    else {
                        localMessage.getActorRef().tell(ByteString.fromString("Unknown title."), getSelf());
                    }
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
