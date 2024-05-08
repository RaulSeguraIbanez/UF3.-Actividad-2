package servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class FuncionServidor {
    private ServerSocket serverSocket;
    private final int PUERTO = 1241;
    private ExecutorService pool;
    private Socket cliente1;
    private Socket cliente2;
    private PrintWriter outCliente1;
    private PrintWriter outCliente2;

    public FuncionServidor(int maxClientes) throws IOException {
        this.serverSocket = new ServerSocket(PUERTO);
        this.pool = Executors.newFixedThreadPool(maxClientes);
    }

    public void runServer() {
        System.out.println("Servidor iniciado en el puerto " + PUERTO);

        try {
            cliente1 = serverSocket.accept();
            cliente2 = serverSocket.accept();
            outCliente1 = new PrintWriter(cliente1.getOutputStream(), true);
            outCliente2 = new PrintWriter(cliente2.getOutputStream(), true);

            pool.execute(new ClientHandler(cliente1, outCliente2));
            pool.execute(new ClientHandler(cliente2, outCliente1));
        } catch (IOException e) {
            System.out.println("Error al aceptar conexiones de clientes: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int maxClientes = args.length > 0 ? Integer.parseInt(args[0]) : 2; // Dos clientes como máximo
        try {
            FuncionServidor servidor = new FuncionServidor(maxClientes);
            servidor.runServer();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;

    public ClientHandler(Socket socket, PrintWriter out) {
        this.clientSocket = socket;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                // Envía el mensaje recibido a todos los clientes conectados
                System.out.println("Mensaje recibido: " + inputLine);
                out.println("Cliente dice: " + inputLine);
            }
        } catch (IOException e) {
            System.out.println("Error al manejar al cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión del cliente: " + e.getMessage());
            }
        }
    }
}
