package Homework2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

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

    public static void HandleMultiThreaded(Socket request,ExecutorService executor) {

    }

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

            System.out.println(httpRequest.Path());

            PrintStream httpResponse = new PrintStream(request.getOutputStream());

            File file = new File(httpRequest.Path().substring(1,httpRequest.Path().length()));

            httpResponse.print("HTTP/1.1 200 \r\n");
            httpResponse.print("Content-Type: text/html \r\n");
            httpResponse.print("Connection: close \r\n");
            httpResponse.print("Content-length: " + file.length() + "\r\n");
            httpResponse.print("Content-Disposition: attachment; filename=\"" + file.getName() + "\" \r\n");
            httpResponse.print("\r\n");

            httpResponse.write(fileToByte(file),0,(int)file.length());

            httpResponse.close();
            httpRequestStream.close();
            request.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

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
}
