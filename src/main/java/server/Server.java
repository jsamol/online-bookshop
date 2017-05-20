package server;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server {
    private final String[] dbs = {"booksDB1.txt", "booksDB2.txt"};
    private final String ordersFileName = "orders.txt";
    private final String dbPath = "files/db/";
    private final String ordersPath = "files/";
    private final String booksPath = "files/books/";

    private final int allBooks = 1000;
    private final String currency = "PLN";

    private Server(boolean generateBooks) {
        if (generateBooks) {
            generateBooks();
        }
    }

    private void start() {

    }

    public static void main(String[] args) {
        Server server = new Server(true);
        server.start();
    }

    private void generateBooks() {
        List<String> entries = new ArrayList<String>();
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

        List<Set<String>> subEntries = new ArrayList<Set<String>>();
        for (int i = 0; i < dbs.length; i++) {
            subEntries.add(new HashSet<String>());
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
}
