import java.io.IOException;
import java.net.ServerSocket;

public class ServidorChat {
  private static int port = 8989; // Puerto por defecto, puede cambiarse en los parámetros
  private static ServerSocket socketServidor;
  private static Cliente[] clientes;
  private static final int maxClientes = 65536;
  private static int siguienteId = 0;

  private static boolean initializeServerSocket() {
    try {
      socketServidor = new ServerSocket(port);
    } catch(IOException e) {
			System.err.println("Error al escuchar en el puerto " + port);
      return false;
    }
    return true;
  }

  // Escucha nuevos clientes
  private static void keepListening() {
    while (addClient() + 1 < maxClientes);
  }

  // Añade un cliente
  private static int addClient() {
    try {
      clientes[siguienteId] = new Cliente(siguienteId, socketServidor.accept());
      clientes[siguienteId].start();
    } catch(IOException e) {
      System.out.println("Error: no se pudo aceptar la conexión solicitada (id = " + siguienteId + ")");
    }
    return siguienteId++;
  }
  
  public static void sendMessage(int id, String message){
    clientes[id].sendMessage(message);
  }
  
	public static void main(String[] args) {
    if (args.length >= 1)
      port = Integer.parseInt(args[0]);

    clientes = new Cliente[maxClientes];
    if (initializeServerSocket()) {
      System.out.println("Esperando conexiones a través del puerto " + port + "...");
      keepListening();
    }
	}
}
