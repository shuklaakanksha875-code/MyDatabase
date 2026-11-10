package mydb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class MyDBServer {

    private static Database db;
    private static StorageManager storage;
    private static BPlusTree tree;

    public static void main(String[] args) throws Exception {

        // ==============================
        // DATABASE
        // ==============================

        db = new Database("data/mydatabase.db");

        db.open();

        storage = new StorageManager(db);

        tree = new BPlusTree(storage);

        // Load existing records from disk
        try {
            storage.loadRecordsIntoTree(tree);
            System.out.println("Existing records loaded.");
        } catch (Exception e) {
            System.out.println(
                    "Could not load existing records: "
                            + e.getMessage()
            );
        }


        // ==============================
        // HTTP SERVER
        // ==============================

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );


        // INSERT
        server.createContext(
                "/insert",
                MyDBServer::handleInsert
        );


        // SEARCH
        server.createContext(
                "/search",
                MyDBServer::handleSearch
        );


        // UPDATE
        server.createContext(
                "/update",
                MyDBServer::handleUpdate
        );


        // DELETE
        server.createContext(
                "/delete",
                MyDBServer::handleDelete
        );


        // SHOW
        server.createContext(
                "/show",
                MyDBServer::handleShow
        );
        // TREE
server.createContext(
        "/tree",
        MyDBServer::handleTree
);

// SIZE
server.createContext(
        "/size",
        MyDBServer::handleSize
);


        server.setExecutor(null);

        server.start();

        System.out.println();
        System.out.println("==============================");
        System.out.println("       MYDB SERVER");
        System.out.println("==============================");
        System.out.println(
                "Server running at:"
        );
        System.out.println(
                "http://localhost:8080"
        );
        System.out.println("==============================");
    }


    // ==========================================
    // INSERT
    // ==========================================

    private static void handleInsert(
            HttpExchange exchange
    ) throws IOException {

        String query =
                exchange.getRequestURI()
                        .getQuery();

        int key =
                getIntParameter(
                        query,
                        "key"
                );

        String value =
                getParameter(
                        query,
                        "value"
                );

        if (value == null) {
            sendResponse(
                    exchange,
                    "Value is required."
            );
            return;
        }

        tree.insert(key, value);

        sendResponse(
                exchange,
                "Inserted: "
                        + key
                        + " -> "
                        + value
        );
    }


    // ==========================================
    // SEARCH
    // ==========================================

    private static void handleSearch(
            HttpExchange exchange
    ) throws IOException {

        String query =
                exchange.getRequestURI()
                        .getQuery();

        int key =
                getIntParameter(
                        query,
                        "key"
                );

        String result =
                tree.search(key);

        if (result == null) {

            sendResponse(
                    exchange,
                    "Key "
                            + key
                            + " not found."
            );

        } else {

            sendResponse(
                    exchange,
                    "Found: "
                            + key
                            + " -> "
                            + result
            );
        }
    }


    // ==========================================
    // UPDATE
    // ==========================================

    private static void handleUpdate(
            HttpExchange exchange
    ) throws IOException {

        String query =
                exchange.getRequestURI()
                        .getQuery();

        int key =
                getIntParameter(
                        query,
                        "key"
                );

        String value =
                getParameter(
                        query,
                        "value"
                );

        if (value == null) {

            sendResponse(
                    exchange,
                    "Value is required."
            );

            return;
        }

        tree.update(key, value);

        sendResponse(
                exchange,
                "Updated: "
                        + key
                        + " -> "
                        + value
        );
    }


    // ==========================================
    // DELETE
    // ==========================================

    private static void handleDelete(
            HttpExchange exchange
    ) throws IOException {

        String query =
                exchange.getRequestURI()
                        .getQuery();

        int key =
                getIntParameter(
                        query,
                        "key"
                );

        tree.delete(key);

        sendResponse(
                exchange,
                "Deleted key: "
                        + key
        );
    }


    // ==========================================
    // SHOW
    // ==========================================

    private static void handleShow(
        HttpExchange exchange
) throws IOException {

    String records =
            tree.getAllRecords();

    if (records.isEmpty()) {
        records = "No records found.";
    }

    sendResponse(
            exchange,
            records
    );
}


    // ==========================================
    // GET PARAMETER
    // ==========================================

    private static String getParameter(
            String query,
            String name
    ) {

        if (query == null) {
            return null;
        }

        String[] parameters =
                query.split("&");

        for (String parameter :
                parameters) {

            String[] pair =
                    parameter.split(
                            "=",
                            2
                    );

            if (
                    pair.length == 2
                            && pair[0].equals(name)
            ) {

                return URLDecoder.decode(
                        pair[1],
                        StandardCharsets.UTF_8
                );
            }
        }

        return null;
    }


    // ==========================================
    // GET INTEGER
    // ==========================================

    private static int getIntParameter(
            String query,
            String name
    ) {

        String value =
                getParameter(
                        query,
                        name
                );

        if (value == null) {
            throw new IllegalArgumentException(
                    name + " is required."
            );
        }

        return Integer.parseInt(value);
    }


    // ==========================================
    // SEND RESPONSE
    // ==========================================

    private static void sendResponse(
            HttpExchange exchange,
            String response
    ) throws IOException {

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Origin",
                        "*"
                );

        exchange.getResponseHeaders()
                .add(
                        "Content-Type",
                        "text/plain; charset=UTF-8"
                );

        byte[] data =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        try (var output =
                     exchange.getResponseBody()) {

            output.write(data);
        }
    }
}