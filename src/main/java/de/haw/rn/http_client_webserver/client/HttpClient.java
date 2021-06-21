package de.haw.rn.http_client_webserver.client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public class HttpClient {
    private static final int PORT = 8080;
    private final ClientArguments arguments = new ClientArguments();

    public static void main(String[] args) {
        HttpClient client = new HttpClient();
        client.start(args);
    }

    private void start(String[] args) {
        List<String> argsList = Arrays.asList(args.clone());
        parseArguments(argsList);
        String hostname = arguments.url.getHost();

        try (Socket socket = new Socket(hostname, PORT)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // Construct a HTTP GET request
            // The end of HTTP GET request should be \r\n\r\n
            String request = "GET " + arguments.url.getPath() + " HTTP/1.1\r\n"
                    + "Accept: */*\r\n"
                    + "Host: " + hostname + "\r\n"
                    + "Connection: Close\r\n";

            if (arguments.hasRange) {
                //test Link: -r 1024-2047 http://manpages.courier-mta.org/htmlman5/gai.conf.5.html
                int from = arguments.rangeStart;
                int to = arguments.rangeEnd;

                if (from > to && to != -1) {
                    System.err.println("invalid range...");
                    help();
                    return;
                }
                if (to == -1)
                    request += "Range: bytes=" + from + "-" + "\r\n";
                else
                    request += "Range: bytes=" + from + "-" + to + "\r\n";

            }

            request += "\r\n"; // request End


            if (arguments.hasSlowMotion && arguments.bytes > 0 && arguments.timeout > 0) {
                byte[] buffer = request.getBytes(StandardCharsets.UTF_8);
                for (int i = 0; i < buffer.length; i += arguments.bytes) {
                    int offset;
                    if (buffer.length - i >= arguments.bytes)
                        offset = i + arguments.bytes;
                    else
                        offset = buffer.length;

                    // copy subarray of buffer from i to offset
                    byte[] part = Arrays.copyOfRange(buffer, i, offset);
                    System.out.print(new String(part));
                    output.write(part);
                    output.flush();

                    try {
                        Thread.sleep(arguments.timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } else {

                System.out.println(request);
                // Sends off HTTP GET request
                output.write(request.getBytes());
                output.flush();
            }

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            int character;
            StringBuilder data = new StringBuilder();

            while ((character = reader.read()) != -1) {
                data.append((char) character);
            }

            System.out.println(data);

        } catch (UnknownHostException ex) {
            System.err.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("I/O error: " + ex.getMessage());
        }
    }

    public void parseArguments(List<String> argsList) {
        if (argsList.size() < 1) {
            help();
            return;
        }
        argsList.forEach(arg -> {
            // help arg handle
            if (arg.equals("-h") || arg.equals("-help")){
                help();
                return;
            }
            // range arg handle
            if (arg.equals("-r") && argsList.size() ==3) {
                String range = argsList.get(argsList.indexOf(arg) + 1);
                if (range.matches("(\\d+)-(\\d*)")) {
                    String[] rangeArr = range.split("-");
                    arguments.rangeStart = Integer.parseInt(rangeArr[0]);
                    if (rangeArr.length == 2 && range.split("-")[1] != null)
                        arguments.rangeEnd = Integer.parseInt(rangeArr[1]);
                    arguments.hasRange = true;
                } else {
                    System.err.println("Client Error: wrong range.");
                    help();
                }
            }
            // slow motion handle
            if (arg.equals("-s") && argsList.size() == 4) {
                String slowMotionBytes = argsList.get(argsList.indexOf(arg) + 1);
                String slowMotionTimeout = argsList.get(argsList.indexOf(arg) + 2);
                if (slowMotionBytes.matches("(\\d+)") && slowMotionTimeout.matches("(\\d+)")) {
                    arguments.bytes = Integer.parseInt(slowMotionBytes);
                    arguments.timeout = Integer.parseInt(slowMotionTimeout);
                    arguments.hasSlowMotion = true;
                } else {
                    System.err.println("Client Error: wrong params.");
                    help();
                }
            }
            // http Method handle
            if (arg.equals("-X") && argsList.size() == 3) {
                String httpMethod = argsList.get(argsList.indexOf(arg) + 1);

                if (httpMethod.equals("GET") || httpMethod.equals("HEAD")) {
                    arguments.httpMethod = httpMethod;
                } else {
                    System.err.println("Client Error: wrong params.");
                    help();
                }
            }
        });

        // URL handle
        String urlRegex = "^(https?\\:)\\/\\/(([^:\\/?#]*)(?:\\:([0-9]+))?)([\\/]{0,1}[^?#]*)(\\?[^#]*|)(#.*|)$";
        String url = argsList.get(argsList.size() - 1);
        if (url.matches(urlRegex)) {
            try {
                arguments.url = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            System.err.println("Client Error: wrong URL.");
            help();
        }
    }

    private void help() {
        System.out.println("Usage : http_client [ OPTIONS ...] URL\n" +
                "where\n" +
                "OPTIONS := { -r range | -s bytes timeout | -h[elp] }");

        System.exit(0); // is a method that causes JVM to exit.
    }

    private static class ClientArguments {
        private boolean hasRange;
        private int rangeStart;
        private int rangeEnd = -1;
        private URL url;
        private boolean hasSlowMotion;
        private int bytes;
        private int timeout;
        private String httpMethod;
    }
}