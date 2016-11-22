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
    int nuevoCliente;
    while ((nuevoCliente = addClient()) + 1 < maxClientes)
      sendLoginData(nuevoCliente);
  }

  // Añade un cliente
  private static int addClient() {
    clientes[siguienteId] = new Cliente(siguienteId, socketServidor);
    return siguienteId++;
  }

  // Manda información a un cliente tras su conexión
  private static void sendLoginData(int id) {
    ProcesadorChat procesador=new ProcesadorChat(clientes[id].getSocket());
    procesador.start();
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
