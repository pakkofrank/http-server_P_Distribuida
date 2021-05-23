package mx.ucol.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

          // Update the htmlResponse variable with the file contents
          String htmlResponse = "<h1>It Works!</h1>The server now sends a response.";
          int contentLength = htmlResponse.length();

          // This line should not be modified just yet
          output.write("HTTP/1.1 200 OK\r\nContent-Length: " +
            String.valueOf(contentLength) + "\r\n\r\n" + htmlResponse);

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