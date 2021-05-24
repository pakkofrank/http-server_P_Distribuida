package mx.ucol.httpserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientHandler implements Runnable {
  final Socket socket;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    PrintWriter output = null;
    BufferedReader input = null;

    try {
      output = new PrintWriter(socket.getOutputStream(), true);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String received;
      while ((received = input.readLine()) != null) {
        String requestArray[] = received.split(" ");

        if (requestArray[0].equals("GET")) {
          // Get the resource name and read its contents in the /www folder
          // If the resource equals "/" it should open index.html
          System.out.println("Resource: " + requestArray[1]);
          String resource = requestArray[1];
          String path = "./www";
          if ((resource.substring(resource.length() - 1)).equals("/")) {
            path += resource + "index.html";
          } else {
            path += resource;
          }

          System.out.println("Response resource: " + path);

          File view = new File(path);

          if (view.exists()) {
            Scanner reader = null;
            String viewContent = "";

            try {
              reader = new Scanner(view);
              while (reader.hasNextLine()) {
                viewContent += reader.nextLine();
              }
              reader.close();
            } catch (FileNotFoundException e) {
              System.out.println("An error occurred, " + e.getMessage());
            } finally {
              if (reader != null) {
                reader.close();
              }
            }
            // Update the htmlResponse variable with the file contents
            String htmlResponse = viewContent;
            int contentLength = htmlResponse.length();

            // This line should not be modified just yet
            output.write("HTTP/1.1 200 OK\r\nContent-Length: " +
                    String.valueOf(contentLength) + "\r\n\r\n" + htmlResponse);
          } else {
            // Update the htmlResponse variable with the file contents
            String htmlResponse = "<h1>Error 505</h1>Page not found";
            int contentLength = htmlResponse.length();

            // This line should not be modified just yet
            output.write("HTTP/1.1 200 OK\r\nContent-Length: " +
                    String.valueOf(contentLength) + "\r\n\r\n" + htmlResponse);
          }


          // We already sent the response, break the loop
          break;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        output.close();
        input.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}