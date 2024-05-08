package servidor;
import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException{
		
		System.out.print("Servidor de chat en el puerto: 1236");
		
		System.out.print("Iniciando servidor...");
		
		FuncionServidor chat = new FuncionServidor(2);
		
		System.out.println("OK");
		chat.runServer();
	}
}	