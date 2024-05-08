package servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FuncionServidor {

    private ServerSocket serverSocket;
    private final int PUERTO = 1241;
    private List<DataOutputStream> clientStreams = new ArrayList<>();
    private int maxClientes;

    public FuncionServidor(int maxClientes) throws IOException {
        this.maxClientes = maxClientes;
        this.serverSocket = new ServerSocket(PUERTO); // Inicializamos el servidor
    }

    public void runServer() throws IOException {
        System.out.println("Esperando conexiones...");

        while (clientStreams.size() < maxClientes) {
            Socket clientSocket = serverSocket.accept(); // Esperando a que algún cliente se conecte
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            clientStreams.add(dataOutputStream);

            // Enviar mensaje de bienvenida al cliente
            dataOutputStream.writeUTF("Conexión aceptada\n");

            // Crear un nuevo hilo para manejar la conexión con el cliente
            Thread clientHandler = new Thread(new ClientHandler(clientSocket));
            clientHandler.start();
        }
        System.out.println("Se ha alcanzado el número máximo de clientes.");
    }

    // Clase interna para manejar cada cliente
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader bufferedReader;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String receivedMessage;
                while ((receivedMessage = bufferedReader.readLine()) != null) {
                    if (!receivedMessage.trim().isEmpty()) {
                        System.out.println("Recibido del Cliente: " + receivedMessage);
                        broadcastMessage(receivedMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Método para enviar el mensaje a todos los clientes conectados
        private void broadcastMessage(String message) {
            for (DataOutputStream out : clientStreams) {
                try {
                    out.writeUTF(message + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese el número máximo de clientes:");
        int maxClientes = scanner.nextInt();

        try {
            FuncionServidor servidor = new FuncionServidor(maxClientes);
            servidor.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
