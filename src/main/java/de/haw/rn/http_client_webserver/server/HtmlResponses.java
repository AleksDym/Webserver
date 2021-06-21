package de.haw.rn.http_client_webserver.server;

public class HtmlResponses {
    static final String NOT_FOUND_HTML = "<!DOCTYPE html>\n" +
            "<html style=\"height:100%\">\n" +
            "\n" +
            "<head>\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
            "\t<title> 404 File Not Found\n" +
            "\t</title>\n" +
            "</head>\n" +
            "\n" +
            "<body\n" +
            "\tstyle=\"color: #444; margin:0;font: normal 14px/20px Arial, Helvetica, sans-serif; height:100%; background-color: #fff;\">\n" +
            "\t<div style=\"height:auto; min-height:100%; \">\n" +
            "\t\t<div style=\"text-align: center; width:800px; margin-left: -400px; position:absolute; top: 30%; left:50%;\">\n" +
            "\t\t\t<h1 style=\"margin:0; font-size:150px; line-height:150px; font-weight:bold;\">404</h1>\n" +
            "\t\t\t<h2 style=\"margin-top:20px;font-size: 30px;\">File Not Found\n" +
            "\t\t\t</h2>\n" +
            "\t\t\t<p>File Not Found.\n" +
            "\t\t\t</p>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "</body>\n" +
            "\n" +
            "</html>";
    static final String NOT_IMPL_HTML = "<!DOCTYPE html>\n" +
            "<html style=\"height:100%\">\n" +
            "\n" +
            "<head>\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
            "\t<title> 501 Not Implemented\n" +
            "\t</title>\n" +
            "</head>\n" +
            "\n" +
            "<body\n" +
            "\tstyle=\"color: #444; margin:0;font: normal 14px/20px Arial, Helvetica, sans-serif; height:100%; background-color: #fff;\">\n" +
            "\t<div style=\"height:auto; min-height:100%; \">\n" +
            "\t\t<div style=\"text-align: center; width:800px; margin-left: -400px; position:absolute; top: 30%; left:50%;\">\n" +
            "\t\t\t<h1 style=\"margin:0; font-size:150px; line-height:150px; font-weight:bold;\">501</h1>\n" +
            "\t\t\t<h2 style=\"margin-top:20px;font-size: 30px;\">Not Implemented\n" +
            "\t\t\t</h2>\n" +
            "\t\t\t<p>Not Implemented.\n" +
            "\t\t\t</p>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "</body>\n" +
            "\n" +
            "</html>";
    static final String BAD_REQUEST_HTML = "<!DOCTYPE html>\n" +
            "<html style=\"height:100%\">\n" +
            "\n" +
            "<head>\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
            "\t<title> 400 Bad Request\n" +
            "\t</title>\n" +
            "</head>\n" +
            "\n" +
            "<body\n" +
            "\tstyle=\"color: #444; margin:0;font: normal 14px/20px Arial, Helvetica, sans-serif; height:100%; background-color: #fff;\">\n" +
            "\t<div style=\"height:auto; min-height:100%; \">\n" +
            "\t\t<div style=\"text-align: center; width:800px; margin-left: -400px; position:absolute; top: 30%; left:50%;\">\n" +
            "\t\t\t<h1 style=\"margin:0; font-size:150px; line-height:150px; font-weight:bold;\">400</h1>\n" +
            "\t\t\t<h2 style=\"margin-top:20px;font-size: 30px;\">Bad Request\n" +
            "\t\t\t</h2>\n" +
            "\t\t\t<p>Bad Request.\n" +
            "\t\t\t</p>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "</body>\n" +
            "\n" +
            "</html>";
    static final String UNSATISFIABLE_REQUEST_HTML = "<!DOCTYPE html>\n" +
            "<html style=\"height:100%\">\n" +
            "\n" +
            "<head>\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
            "\t<title> 416 Requested range not satisfiable\n" +
            "\t</title>\n" +
            "</head>\n" +
            "\n" +
            "<body\n" +
            "\tstyle=\"color: #444; margin:0;font: normal 14px/20px Arial, Helvetica, sans-serif; height:100%; background-color: #fff;\">\n" +
            "\t<div style=\"height:auto; min-height:100%; \">\n" +
            "\t\t<div style=\"text-align: center; width:800px; margin-left: -400px; position:absolute; top: 30%; left:50%;\">\n" +
            "\t\t\t<h1 style=\"margin:0; font-size:150px; line-height:150px; font-weight:bold;\">416</h1>\n" +
            "\t\t\t<h2 style=\"margin-top:20px;font-size: 30px;\">Requested range not satisfiable\n" +
            "\t\t\t</h2>\n" +
            "\t\t\t<p>None of the range specified overlap the current extent of the selected resource.\n" +
            "\t\t\t</p>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "</body>\n" +
            "\n" +
            "</html>";

}
