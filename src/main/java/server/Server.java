package server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import server.actors.Manager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server {
    private static final String[] dbs = {"booksDB1.txt", "booksDB2.txt"};
    private static final String ordersFileName = "orders.txt";
    private static final String dbPath = "files/db/";
    private static final String ordersPath = "files/";
    private static final String booksPath = "files/books/";

    private final int allBooks = 1000;
    private final String currency = "PLN";

    private final Map<String, String> roles;

    private Server(boolean generateBooks) {
        if (generateBooks) {
            generateBooks();
        }
        roles = new HashMap<>();
        roles.put("find", "searchActor");
        roles.put("order", "orderActor");
        roles.put("read", "streamActor");
    }

    private void start() {
        File configFile = new File("config/server.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem actorSystem = ActorSystem.create("server", config);
        final ActorRef manager = actorSystem.actorOf(Manager.props(roles), "manager");

        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in)
        );

        while (true) {
            try {
                String command = input.readLine();
                if ("\\exit".equals(command)) {
                    break;
                }
                else {
                    ByteString bs = ByteString.fromString(command);
                    manager.tell(bs, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        actorSystem.terminate();
    }

    public static void main(String[] args) {
        Server server = new Server(true);
        server.start();
    }

    private void generateBooks() {
        List<String> entries = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < allBooks; i++) {
            String name = "Book #" + i;
            try {
                PrintWriter file = new PrintWriter(booksPath + name + ".txt", "UTF-8");
                for (int j = 0; j < random.nextInt(1500) + 500; j++) {
                    file.print(name + " content. ");
                }
                file.close();
                float price = (random.nextFloat() * 90) + 10;
                String entry = String.format("%s | %.2f %s", name, price, currency);
                entries.add(entry);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        List<Set<String>> subEntries = new ArrayList<>();
        for (int i = 0; i < dbs.length; i++) {
            subEntries.add(new HashSet<>());
        }

        for (String entry : entries) {
            int rand = random.nextInt((int) Math.pow(2, (double) dbs.length) - 1) + 1;
            int i = 0;
            while (rand != 0) {
                if (rand % 2 == 1) {
                    subEntries.get(i).add(entry);
                }
                rand >>= 1;
                i++;
            }
        }

        for (int i = 0; i < dbs.length; i++) {
            try {
                Files.write(Paths.get(dbPath + dbs[i]), subEntries.get(i), Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] getDbs() {
        return dbs;
    }

    public static String getOrdersPath() {
        return ordersPath;
    }

    public static String getOrdersFileName() {
        return ordersFileName;
    }
}
