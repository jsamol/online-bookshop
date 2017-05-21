package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import client.actors.ClientActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    public static void main(String[] args) {
        File configFile = new File("config/client.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem actorSystem = ActorSystem.create("client", config);
        final ActorRef clientActor = actorSystem.actorOf(Props.create(ClientActor.class), "clientActor");

        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in)
        );
        System.out.println(
                "\nAvailable options:\n" +
                "find [book title]\t-> check whether the title can be found in our database\n" +
                "order [book title]\t-> make an order\n" +
                "read [book title]\t-> get content of the book\n"
        );
        while (true) {
            try {
                System.out.print("> ");
                System.out.flush();
                String command = input.readLine();
                if ("exit".equals(command)) {
                    break;
                }
                else if (command.startsWith("find") || command.startsWith("order") || command.startsWith("read")) {
                    clientActor.tell(command, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        actorSystem.terminate();
    }
}
