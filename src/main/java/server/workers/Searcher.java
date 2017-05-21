package server.workers;

import server.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Searcher {
    private String toSearch;
    private int n;

    public Searcher(String toSearch, int n) {
        this.toSearch = toSearch;
        this.n = n;
    }

    public String search() throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(n);
        List<CompletableFuture<String>> completableFutures = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            File db = new File(Server.getDbPath() + Server.getDbs()[i]);
            executorService.submit(() -> {
                try {
                    final Scanner scanner = new Scanner(db);
                    String result = toSearch + " has not been found.";
                    while(scanner.hasNextLine()) {
                        String fileLine = scanner.nextLine();
                        if (fileLine.startsWith(toSearch)) {
                            result = fileLine;
                            break;
                        }
                    }
                    completableFuture.complete(result);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            completableFutures.add(completableFuture);
        }
        Set<String> searchResults = new HashSet<>();
        for (CompletableFuture<String> completableFuture : completableFutures) {
            searchResults.add(completableFuture.get());
        }

        if (searchResults.size() > 1) {
            for (String searchResult : searchResults) {
                if (searchResult.startsWith(toSearch)) {
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
