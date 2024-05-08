package cliente;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class main {

    public static void main(String[] args) {
        final int PUERTO = 1241;
        final String HOST = "localhost";

        try (Socket socket = new Socket(HOST, PUERTO)) {
            HilosCliente cliente = new HilosCliente(socket);
            cliente.runCliente();
        } catch (UnknownHostException e) {
          System.err.println("No se puede encontrar el host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}