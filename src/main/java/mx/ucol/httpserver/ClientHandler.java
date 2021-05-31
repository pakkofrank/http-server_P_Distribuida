package mx.ucol.httpserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientHandler implements Runnable {
  final Socket socket;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    DataOutputStream output = null;
    BufferedReader input = null;

    try {
      output = new DataOutputStream(socket.getOutputStream());
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
          try {
            Path filePath = Paths.get(path);
            boolean fileExist = Files.exists(filePath, LinkOption.NOFOLLOW_LINKS);

            if (!fileExist)
              filePath = Paths.get("./www/not_found.html");

            String response = null;
            byte[] fileContent = null;
            int contentLength = 0;

            if (fileExist) {
              response = "HTTP/1.1 200 OK\r\n";
            } else {
              response = "HTTP/1.1 404\r\n";
            }

            fileContent = Files.readAllBytes(filePath);
            contentLength = fileContent.length;
            String mimeType = Files.probeContentType(filePath);

            response += "ContentType: " + mimeType;
            response += "Content-Length: " + String.valueOf((contentLength)) + "\r\n\r\n";

            // This line should not be modified just yet
            output.writeBytes(response);
            output.write(fileContent, 0, contentLength);
            // We already sent the response, break the loop
            break;
          } catch ( Exception e) {
            System.err.println(e);
          }
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