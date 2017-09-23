package Homework2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * The Httpserver sends a file to the client if it can find it.
 *
 * @author  Trent Killinger
 * @version 1.0
 * @since   9-22-2017
 */
public class Main {

    /**
     * This is the main method which makes use of addNum method.
     * @param args Unused.
     * @return Nothing.
     * @exception IOException On input error.
     * @see IOException
     */
    public static void main(String[] args) {

        String serverType = GetThreadType();

        System.out.println("Starting up " + serverType + " threaded server");

        ExecutorService executor = null;
        ServerSocket httpSocket = null;

        if(serverType.equals("Multiple")) {
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        try {
            httpSocket= new ServerSocket(80);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        if(httpSocket != null) {
            while (true) {
                try {
                    Socket request = httpSocket.accept();

                    if(executor != null) {
                        HandleMultiThreaded(request,executor);
                    } else {
                        HandleSingleThreaded(request);
                    }

                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    /**
     * This method runs code in a thread pool.
     * @param request socket
     * @param executor threadpool to execute threads
     * @return Nothing.
     */
    public static void HandleMultiThreaded(Socket request,ExecutorService executor) {
        executor.execute(() -> {
            System.out.println("Received Request ");
            try {
                BufferedReader httpRequestStream = new BufferedReader(new InputStreamReader(request.getInputStream()));

                String httpRequestString = "";
                String line;
                while((line = httpRequestStream.readLine()) != null) {
                    if(line.length() == 0) {
                        break;
                    }
                    httpRequestString += line + "\r\n";
                }
                httpRequestString += "\r\n";

                HTTPRequest httpRequest = new HTTPRequest(httpRequestString);

                File file = new File(httpRequest.Path().substring(1,httpRequest.Path().length()));

                HTTPResponse httpResponse = new HTTPResponse();

                if(file.exists()) {
                    httpResponse.GeneralHeader("HTTP/1.1 200");
                    httpResponse.Body(fileToByte(file));
                    httpResponse.AddHeader("Content-length","" + fileToByte(file).length);
                    httpResponse.AddHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                    httpResponse.AddHeader("Content-Type","application/octet-stream");
                }
                else {
                    httpResponse.GeneralHeader("HTTP/1.1 404");
                    httpResponse.Body("<HTML><head><head><body>404 Error, File was not found</body></HTML>");
                    httpResponse.AddHeader("Content-length","" + "<HTML><head><head><body>404 Error, File was not found</body></HTML>".length());
                    httpResponse.AddHeader("Content-Type","text/html");
                }
                httpResponse.AddHeader("Connection","close");

                httpResponse.SendResponse(request.getOutputStream());

                httpRequestStream.close();
                request.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });
    }

    /**
     * This method runs the response code on the main thread
     * @param request socket
     * @return Nothing.
     */
    public static void HandleSingleThreaded(Socket request) {
        System.out.println("Received Request ");
        try {
            BufferedReader httpRequestStream = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String httpRequestString = "";
            String line;
            while((line = httpRequestStream.readLine()) != null) {
                if(line.length() == 0) {
                    break;
                }
                httpRequestString += line + "\r\n";
            }
            httpRequestString += "\r\n";

            HTTPRequest httpRequest = new HTTPRequest(httpRequestString);

            File file = new File(httpRequest.Path().substring(1,httpRequest.Path().length()));

            HTTPResponse httpResponse = new HTTPResponse();

            if(file.exists()) {
                httpResponse.GeneralHeader("HTTP/1.1 200");
                httpResponse.Body(fileToByte(file));
                httpResponse.AddHeader("Content-length","" + fileToByte(file).length);
                httpResponse.AddHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                httpResponse.AddHeader("Content-Type","application/octet-stream");
            }
            else {
                httpResponse.GeneralHeader("HTTP/1.1 404");
                httpResponse.Body("<HTML><head><head><body>404 Error, File was not found</body></HTML>");
                httpResponse.AddHeader("Content-length","" + "<HTML><head><head><body>404 Error, File was not found</body></HTML>".length());
                httpResponse.AddHeader("Content-Type","text/html");
            }
            httpResponse.AddHeader("Connection","close");

            httpResponse.SendResponse(request.getOutputStream());

            httpRequestStream.close();
            request.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * This method converts a file into a byte array
     * @param file file to convert into byte array
     * @return byte[].
     */
    private static byte[] fileToByte (File file)
    {
        FileInputStream fileInputStream = null;
        byte[] result = new byte[(int) file.length()];
        try
        {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(result);
            fileInputStream.close();
        }
        catch (Exception e)
        {

        }
        return result;
    }

    /**
     * Prompts the user for thread type
     * @param none
     * @return thread type.
     */
    private static String GetThreadType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please specify \"Single\" or \"Multiple\" for single or multiple threaded server");
        String userInput = "";
        while(!userInput.equals("Single") && !userInput.equals("Multiple")) {
            //Keep Reading Lines Until Valid Input
            userInput = scanner.nextLine();
        }
        return  userInput;
    }
}
