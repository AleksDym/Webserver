package de.haw.rn.http_client_webserver.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class HttpServer {

    protected static final Logger LOG = Logger.getLogger(HttpServer.class.getName());
    private FileHandler fh;




    private static final int LINGER_TIME = 5000;
    private File rootDir = new File("src/main/resources/test-website");
    private String logfile;
    private int port = 8080;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.parseArguments(args);
        server.serve();
    }

    private void serve() {
        try {

            ServerSocket listeningSocket = new ServerSocket(port);
            while (true) {
                //acc
                Socket clientSocket = listeningSocket.accept();
                clientSocket.setSoLinger(true, LINGER_TIME);
                Thread handler = new Thread(new RequestHandler(clientSocket, rootDir));
                handler.setPriority(Thread.MAX_PRIORITY);
                handler.start();
            }
        } catch (IOException ioE) {
            System.out.println("Server failure Can't accept client connection. ");
        }
    }

    private void parseArguments(String[] args) {
        if (args.length < 1) {
            help();
            return;
        }
        if (args[0].equals("-h") || args[0].equals("-help")) {
            help();
            return;
        }

        if (args[0].equals("-p")) {
            try {
                this.port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                //ex.printStackTrace();
                LOG.severe(ex.getMessage());
            }
        }

        if (args[0].equals("-l")) {
            this.logfile = args[1];
        } else
            this.logfile = "standard";


        try {
            fh = new FileHandler("./src/main/resources/logfiles/" + this.logfile + ".log", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fh.setFormatter(new SimpleFormatter());
        LOG.addHandler(fh);
    }

    private void help() {
        System.out.println("Usage : http_server [ OPTIONS ...] DIRECTORY\n" +
                "where\n" +
                "OPTIONS := { -p port | -l logfile | -h[elp] }");
    }
}
