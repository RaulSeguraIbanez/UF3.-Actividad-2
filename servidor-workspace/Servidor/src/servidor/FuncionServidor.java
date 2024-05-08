package servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class FuncionServidor {
    private ServerSocket serverSocket;
    private final int PUERTO = 1241;
    private final String PALABRA_CLAVE_SERVIDOR = "Cleopatra";
    private ExecutorService pool;

    public FuncionServidor(int maxClientes) throws IOException {
        this.serverSocket = new ServerSocket(PUERTO);
        this.pool = Executors.newFixedThreadPool(maxClientes);
    }

    public void runServer() {
        System.out.println("Servidor iniciado en el puerto " + PUERTO);

        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket, PALABRA_CLAVE_SERVIDOR));
            }
        } catch (IOException e) {
            System.out.println("Error al aceptar conexiones de clientes: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void main(String[] args) {
        int maxClientes = args.length > 0 ? Integer.parseInt(args[0]) : 5; // Número máximo de clientes como argumento
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
    private String palabraClaveServidor;

    public ClientHandler(Socket socket, String palabraClaveServidor) {
        this.clientSocket = socket;
        this.palabraClaveServidor = palabraClaveServidor;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String inputLine;

            out.println("Conexión aceptada. Escribe tu palabra clave para cerrar el chat.");

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase(palabraClaveServidor)) {
                    out.println("Palabra clave del servidor detectada. Cerrando servidor.");
                    System.exit(0);
                } else if (inputLine.equalsIgnoreCase("Marc Antoni") || inputLine.equalsIgnoreCase("César")) {
                    out.println("Palabra clave de cliente detectada. Cerrando chat.");
                    break;
                } else {
                    out.println("Eco: " + inputLine);
                }
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