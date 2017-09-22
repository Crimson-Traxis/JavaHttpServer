package Homework2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.stream.Collectors;

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



    public static void main(String[] args) {
        String serverType = GetThreadType();
        System.out.println("Starting up " + serverType + " threaded server");
        ServerSocket httpSocket = null;
        try {
            httpSocket= new ServerSocket(80);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        if(httpSocket != null) {
            while (true) {
                try {
                    Socket handler = httpSocket.accept();

                    System.out.println("Received Request ");

                    BufferedReader httpRequest = new BufferedReader(new InputStreamReader(handler.getInputStream()));

                    String httpRequestString = "";
                    while((httpRequestString = httpRequest.readLine()) != null) {
                        if(httpRequestString.length() == 0) {
                            break;
                        }
                        System.out.println(httpRequestString);
                    }

                    PrintWriter httpResponse = new PrintWriter(handler.getOutputStream());

                    httpResponse.print("HTTP/1.1 200 \r\n");
                    httpResponse.print("Content-Type: text/plain \r\n");
                    httpResponse.print("Connection: close \r\n");
                    httpResponse.print("\r\n");

                    httpResponse.print("Hello From Java Server" + "\r\n");

                    httpResponse.close();
                    httpRequest.close();
                    handler.close();

                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}
