import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorChat {
  private static int port = 8989; // Puerto por defecto, puede cambiarse en los parámetros
  private static ServerSocket socketServidor;

  private static boolean initializeServerSocket() {
    try {
      socketServidor = new ServerSocket(port);
    } catch(IOException e) {
			System.err.println("Error al escuchar en el puerto " + port);
      return false;
    }
    return true;
  }

  private static boolean keepListening() {
    while (true) {
      Socket socketServicio = null;
      try {
        socketServicio = socketServidor.accept();
      } catch(IOException e){
        System.out.println("Error: no se pudo aceptar la conexión solicitada");
      }

      ProcesadorChat procesador=new ProcesadorChat(socketServicio);
      procesador.start();
    }
  }
  
	public static void main(String[] args) {
    if (args.length >= 1)
      port = Integer.parseInt(args[0]);

    System.out.println("Esperando conexiones a través del puerto " + port + "...");

    if (initializeServerSocket())
      keepListening();
	}
}
