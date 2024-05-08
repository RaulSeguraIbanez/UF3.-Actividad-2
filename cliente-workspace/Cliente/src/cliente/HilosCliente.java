package cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HilosCliente {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private BufferedReader inputReader;

    public HilosCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void runCliente() throws IOException {
        try {
            System.out.println("Conexión realizada. OK");

            // Hilo para recibir mensajes del servidor
            Thread recibirMensajes = new Thread(() -> {
                try {
                    while (true) {
                        String mensaje = dataInputStream.readUTF();
                        System.out.println("Recibido del servidor: " + mensaje);
                        // Código adicional si es necesario
                    }
                } catch (IOException e) {
                    System.out.println("Error al recibir mensajes del servidor: " + e.getMessage());
                }
            });
            recibirMensajes.start();

            // Hilo para enviar mensajes al servidor desde la consola del cliente
            Thread enviarMensajes = new Thread(() -> {
                try {
                    String consoleInput;
                    while ((consoleInput = inputReader.readLine()) != null) {
                        // Envía el mensaje al servidor con un salto de línea al final
                        dataOutputStream.writeUTF(consoleInput + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar mensaje al servidor: " + e.getMessage());
                }
            });
            enviarMensajes.start();

            // Espera a que ambos hilos terminen
            recibirMensajes.join();
            enviarMensajes.join();

        } catch (InterruptedException e) {
            System.out.println("Error en la comunicación con el servidor: " + e.getMessage());
        } finally {
            try {
                inputReader.close();
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
                System.out.println("Conexión cerrada. OK");
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("direccion_ip_del_servidor", 1235);
            HilosCliente cliente = new HilosCliente(socket);
            cliente.runCliente();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}