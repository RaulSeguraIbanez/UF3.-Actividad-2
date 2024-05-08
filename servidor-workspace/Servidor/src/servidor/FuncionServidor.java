package servidor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class FuncionServidor {

    public Socket socket;
    public ServerSocket serverSocket;
    public final int PUERTO = 1241;
    public DataOutputStream dataOutputStream;
    public String mensaje;

    public FuncionServidor() throws IOException {
        this.serverSocket = new ServerSocket(PUERTO); // Inicializamos el servidor
        this.socket = new Socket();
    }

    public void runServer() throws IOException {
        System.out.print("Conexion del cliente...");

        this.socket = this.serverSocket.accept(); // Esperando a que algún cliente se conecte

        System.out.println("OK");

        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream()); // Flujo donde se guarda lo que se envía al cliente
        this.dataOutputStream.writeUTF("Conexión aceptada\n"); // Asegúrate de incluir el salto de línea

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        // Hilo para leer la entrada de la consola y enviar al cliente
        Thread enviarMensajes = new Thread(() -> {
            try {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String consoleInput;
                while ((consoleInput = consoleReader.readLine()) != null) {
                    // Solo envía el mensaje si la entrada proviene del servidor
                    if (!consoleInput.isEmpty()) {
                        dataOutputStream.writeUTF(consoleInput + "\n"); // Asegúrate de incluir el salto de línea
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        enviarMensajes.start();

        String receivedMessage;
        while (true) {
            receivedMessage = bufferedReader.readLine(); // Cambiado a readLine
            if (receivedMessage != null && !receivedMessage.trim().isEmpty()) {
                // Imprime el mensaje recibido del cliente
                System.out.println("Recibido del Cliente: " + receivedMessage);
            }
        }

        // Nota: Este código no cerrará correctamente los recursos y la conexión porque el bucle while es infinito.
        // Deberías considerar implementar una condición de salida y cerrar los recursos fuera del bucle.
    }

    public static void main(String[] args) {
        try {
            FuncionServidor servidor = new FuncionServidor();
            servidor.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}