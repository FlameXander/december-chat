package ru.flamexander.december.chat.client.callbacks;

import java.io.IOException;
import java.util.Scanner;

public class ClientApplicationWithCallbacks {
    public static void main(String[] args) {
        try (Network network = new Network()) {
            network.setOnMessageReceived(arguments -> {
                System.out.println((String)arguments[0]);
            });
            network.connect(8189);
            System.out.println("Подключились к серверу");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                network.sendMessage(message);
                if (message.equals("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
