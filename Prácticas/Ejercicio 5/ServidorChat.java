import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.HashMap;
import java.net.ServerSocket;

/*
  Guarda la información de todos los clientes y grupos y escucha nuevas conexiones
*/
public class ServidorChat {
  private static int port = 8989; // Puerto por defecto
  private static ServerSocket socketServidor;
  private static HashMap<String, Cliente> clientes;
  private static HashMap<String, Grupo> grupos;
  private static final int maxExpectedClients = 32768;
  private static final int maxExpectedGroups = 64;

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
  private static void addClient() {
    String nombre = "[no se llegó a leer el nombre]";
    try {
      Cliente nuevo = new Cliente(socketServidor.accept());
      nombre = nuevo.getClientName();
      clientes.put(nombre, nuevo);
      nuevo.start();
    } catch(IOException e) {
      System.out.println("Error: no se pudo aceptar la conexión solicitada (nombre = " + nombre + ")");
    } catch(IllegalNameException e) {
      System.out.println("Error: solicitud de nombre inválida (mensaje = " + e.what() + ")");
    }
    catch(ClassNotFoundException e) {
      System.out.println("Error: no se pudo aceptar la conexión (nombre = " + nombre + ")");
    }
  }

  // Añade un grupo
  public static void addGroup(String nombre, Cliente creador) {
    grupos.put(nombre, new Grupo(nombre, creador));
  }

  // Añade un cliente a un grupo
  public static boolean addClientToGroup(String grupo, String cliente, String solicitante) {
    if (!grupos.containsKey(grupo))
      addGroup(grupo, clientes.get(solicitante));

    return grupos.get(grupo).addMember(clientes.get(cliente));
  }

  // Envía un mensaje al cliente con nombre "destino"
  public static boolean sendToClient(String destino, Mensaje message) {
    if (!clientes.containsKey(destino))
      return false;

    clientes.get(destino).sendMessage(message);
    return true;
  }
  
  // Envía un mensaje al grupo con nombre "nombregrupo"
  public static boolean sendToGroup(String nombregrupo, Mensaje message) {
    if (!grupos.containsKey(nombregrupo))
      return false;

    grupos.get(nombregrupo).sendMessage(message);
    return true;
  }
  
	public static void main(String[] args) {
    if (args.length >= 1)
      port = Integer.parseInt(args[0]);

    clientes = new HashMap<String,Cliente>(2*maxExpectedClients, (float) 1/2);
    grupos   = new HashMap<String,Grupo>(2*maxExpectedGroups , (float) 1/2);
    if (initializeServerSocket()) {
      System.out.println("Esperando conexiones a través del puerto " + port + "...");
      keepListening();
    }
	}
}
