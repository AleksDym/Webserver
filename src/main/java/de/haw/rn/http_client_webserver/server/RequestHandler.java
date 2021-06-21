package de.haw.rn.http_client_webserver.server;

import de.haw.rn.http_client_webserver.exception.BadRequest;
import de.haw.rn.http_client_webserver.exception.UnsatisfiableRequest;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RequestHandler implements Runnable {
    protected static final Logger LOG = HttpServer.LOG;

    private static final String SERVER_ID_HEADER = "Server: Httpd 1.1";
    private static final String HTTP_GET_METHODE = "GET";
    private static final String HTTP_HEAD_METHODE = "HEAD";
    private static final String HTTP_OK_RESPONSE = "HTTP/1.1 200 OK";
    private static final String NOT_FOUND_RESPONSE = "HTTP/1.1 404 File Not Found";
    private static final String NOT_FOUND_HTML = HtmlResponses.NOT_FOUND_HTML;
    private static final String HTTP_NOT_IMPL_RESPONSE = "HTTP/1.1 501 Not Implemented";
    private static final String NOT_IMPL_HTML = HtmlResponses.NOT_IMPL_HTML;
    private static final String HTTP_BAD_REQ_RESPONSE = "HTTP/1.1 400 Bad Request";
    private static final String BAD_REQUEST_HTML = HtmlResponses.BAD_REQUEST_HTML;
    private static final String HTTP_REQ_UNSATISFIABLE = "HTTP/1.1  416 Requested range not satisfiable";
    private static final String UNSATISFIABLE_REQUEST_HTML = HtmlResponses.UNSATISFIABLE_REQUEST_HTML;

    private final int maxClientAmount = 10;
    private final Semaphore semaphore = new Semaphore(maxClientAmount);

    private final Socket clientSocket;
    private final File rootDir;

    public RequestHandler(Socket clientSocket, File rootDir) {
        this.clientSocket = clientSocket;
        this.rootDir = rootDir;

    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            HttpRequest request = readRequest();

            if (request == null) {
                LOG.severe("Request is null.");
                return;
            }
            LOG.info(getIPAddress() + " " + request.httpMethod + " " + request.path);

            if (request.httpMethod.equals(HTTP_GET_METHODE) || request.httpMethod.equals(HTTP_HEAD_METHODE))
                handleRequest(request);
            else {
                sendErrorMessage(HTTP_NOT_IMPL_RESPONSE, NOT_IMPL_HTML, request.httpVersion);
                LOG.severe(HTTP_NOT_IMPL_RESPONSE);
            }


        } catch (IOException ioe) {
            //ioe.printStackTrace();

        } catch (Exception ex) {
            //e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            semaphore.release();

        }
    }

    private HttpRequest readRequest() throws Exception {
        InputStream inputStream = clientSocket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int character;
        int count = 0;

        while ((character = inputStream.read()) != -1) {
            line.append((char) character);
            if (line.toString().equals("\r\n")) {
                break;
            } else if (character == '\n' && line.charAt(line.length() - 2) == '\r') {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            count++;
            if (count > 1000)
                throw new Exception();
        }

        if (lines.size() == 0)
            throw new BadRequest();

        String requestLine = lines.get(0);
        if (requestLine == null)
            return null;
        String[] requestTokens = requestLine.split(" ");

        HttpRequest request = new HttpRequest(requestTokens[0].trim(), requestTokens[1].trim(), requestTokens[2].trim());

        for (int i = 1; i < lines.size(); i++) {
            request.addHeader(lines.get(i));
        }

        return request;
    }

    private void handleRequest(HttpRequest request) throws IOException {

        try {
            String path = "";
            if (request.path.endsWith("/") && Files.isDirectory(Paths.get(rootDir + request.path))
                    && Files.exists(Paths.get(rootDir + request.path + "index.html"))) {
                path = request.path + "index.html";
            } else{
                path = request.path;
                if (!Files.exists(Paths.get(rootDir + request.path))) {
                    throw new NoSuchFileException("No Such File Exception");
                }
            }
            sendResponse(request, path);

        } catch (NoSuchFileException nsfe) {
            sendErrorMessage(NOT_FOUND_RESPONSE, NOT_FOUND_HTML, request.httpVersion);
            LOG.severe(NOT_FOUND_RESPONSE);

        } catch (FileNotFoundException fnfe) {
            sendErrorMessage(NOT_FOUND_RESPONSE, directoryFilesListing(request.path), request.httpVersion);
            LOG.info("Directory Listing: " + request.path);

        } catch (BadRequest br) {
            sendErrorMessage(HTTP_BAD_REQ_RESPONSE, BAD_REQUEST_HTML, request.httpVersion);
            LOG.severe(HTTP_BAD_REQ_RESPONSE);

        } catch (UnsatisfiableRequest iae2) {
            sendErrorMessage(HTTP_REQ_UNSATISFIABLE, UNSATISFIABLE_REQUEST_HTML, request.httpVersion);
            LOG.severe(HTTP_REQ_UNSATISFIABLE);

        }
    }

    private void sendResponse(HttpRequest request, String path) throws IOException, UnsatisfiableRequest, BadRequest {
        File file = new File(rootDir, removeInitialSlash(path));
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        int to = -1;
        int from = -1;
        boolean hasRange = false;
        for (String head : request.headers) {
            if (head.startsWith("Range:")) {
                hasRange = true;
                Pattern pattern = Pattern.compile("(\\d+)-(\\d*)");
                Matcher matcher = pattern.matcher(head);
                while (matcher.find()) {
                    from = Integer.parseInt(matcher.group(1));
                    if (!matcher.group(2).equals(""))
                        to = Integer.parseInt(matcher.group(2));
                }
            }
        }

        if (from == -1 && from > file.length())
            throw new UnsatisfiableRequest();

        if (to == -1 || to > file.length())
            to = (int) file.length();

        if (from > to)
            throw new BadRequest();

        int contentLength = to - from + 1;
        if (from == to)
            contentLength = 1;

        OutputStream toClient = clientSocket.getOutputStream();
        PrintWriter pw = new PrintWriter(toClient);

        pw.println(HTTP_OK_RESPONSE);
        pw.println("Date:" + LocalDateTime.now());
        pw.println(SERVER_ID_HEADER);
        pw.println("Content-type: " + getMimeFormExtension(path));
        if (hasRange) {
            pw.println("Content-Range: bytes " + from + "-" + to + "/" + file.length());
            pw.println("Content-length: " + contentLength);
        }else {
            pw.println("Content-length: " + file.length());
        }

        pw.println();
        pw.flush();
        LOG.info(HTTP_OK_RESPONSE);

        if (request.httpMethod.equals(HTTP_GET_METHODE)) {
            if (hasRange) {
                bufferedInputStream.skip(from);
                toClient.write(bufferedInputStream.readNBytes((from == to)?1:contentLength));
            } else
                bufferedInputStream.transferTo(toClient);
        }
    }

    private String removeInitialSlash(String source) {
        return (source.length() > 1) ? source.substring(1, source.length()) : "";
    }

    private String directoryFilesListing(String directory) throws IOException {
        StringBuilder result = new StringBuilder();
        Path dirPath = Paths.get(rootDir + directory);

        result.append("<!DOCTYPE html><html lang=\"en\">")
                .append("<head><meta charset=\"UTF-8\"><title>")
                .append(dirPath.toRealPath())
                .append("</title></head>")
                .append("<body>\n\t\t<h1>Contents of ")
                .append(dirPath.getFileName())
                .append(":</h1>\n\t\t<ul>\n").append(
                "<table>\n" +
                        "    <thead>\n" +
                        "        <tr>\n" +
                        "          <th>Name</th>\n" +
                        "          <th>Length</th>\n" +
                        "          <th>Last modified</th>\n" +
                        "        </tr>\n" +
                        "    </thead>\n" +
                        "    <tbody>\n");

        Files.list(dirPath).map(f -> f.toFile())
                .map(v -> "<tr>" +
                        "<td>" + v.getName() + "</td>" +
                        "<td>" + v.length() + "</td>" +
                        "<td>" + new Date(v.lastModified()).toString() + "</td>" +
                        "</tr>").forEach(result::append);
        result.append("</tbody></table></body></html>");

        return result.toString();
    }

    private String getMimeFormExtension(String filename) {
        String ext = Optional.ofNullable(filename).filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");

        return MimeTypes.getMimeType(ext);
    }

    private void sendErrorMessage(String code, String html, String version) throws IOException {
        OutputStream toClient = clientSocket.getOutputStream();
        PrintWriter pw = new PrintWriter(toClient);
        if (version.startsWith("HTTP/")) {
            pw.println(code);
            pw.println("Data:" + (new Date()));
            pw.println(SERVER_ID_HEADER);
            pw.println("Content-type: text/html");
            pw.println("Content-length: " + html.getBytes().length);
            pw.println();
            pw.flush();
        }
        toClient.write(html.getBytes());
    }

    private InetAddress getIPAddress() {
        SocketAddress socketAddress = clientSocket.getRemoteSocketAddress();

        if (socketAddress instanceof InetSocketAddress) {
            return ((InetSocketAddress) socketAddress).getAddress();
        } else {
            System.err.println("Not an internet protocol socket.");
        }
        return null;
    }


    private static class HttpRequest {
        private String httpMethod;
        private String path;
        private String httpVersion;
        private List<String> headers = new ArrayList<>();

        private HttpRequest(String httpMethod, String path, String httpVersion) {
            this.httpMethod = httpMethod;
            // hier wird sichergestellt, dass Parent Dir nicht zugegriffen wird.
            this.path = path.replaceAll("\\.\\.\\/", "");
            this.httpVersion = httpVersion;
        }

        private void addHeader(String header) {
            headers.add(header);
        }
    }
}
