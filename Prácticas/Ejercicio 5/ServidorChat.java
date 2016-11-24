import java.io.IOException;
import java.util.HashMap;
import java.net.ServerSocket;

public class ServidorChat {
  private static int port = 8989; // Puerto por defecto, puede cambiarse en los parámetros
  private static ServerSocket socketServidor;
  private static HashMap<Integer, Cliente> clientes;
  private static final int maxExpectedClients = 32768;
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
    while (true)
      addClient();
  }

  // Añade un cliente
  private static int addClient() {
    try {
      clientes.put(siguienteId, new Cliente(siguienteId, socketServidor.accept()));
      clientes.get(siguienteId).start();
    } catch(IOException e) {
      System.out.println("Error: no se pudo aceptar la conexión solicitada (id = " + siguienteId + ")");
    }
    return siguienteId++;
  }
  
  // Envía un mensaje al cliente con id "id"
  public static boolean sendMessageToClient(String codigo, int id, String message){
    if(!clientes.containsKey(id))
      return false;
    clientes.get(id).sendMessage(codigo + id + ";" + message);
    return true;
  }
  
	public static void main(String[] args) {
    if (args.length >= 1)
      port = Integer.parseInt(args[0]);

    clientes = new HashMap(2*maxExpectedClients, (float) 1/2);
    if (initializeServerSocket()) {
      System.out.println("Esperando conexiones a través del puerto " + port + "...");
      keepListening();
    }
	}
}
