import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Scanner;
import java.util.Date;


/*
  Escucha e interpreta los mensajes enviados por el servidor
*/
class Escuchador extends Thread {
  private ObjectInputStream in = null;

  public Escuchador(ObjectInputStream br) {
    in = br;
  }

  private void parse(Mensaje m) {
    String conv = m.getConversacion();
    boolean esMensaje = false;

    switch(m.getCodigo()){
      case 2001: 
        System.err.println("Error: El usuario " + m.getContenido() + " no existe");
        break;
      case 2002: 
        System.err.println("Error: El grupo " + m.getContenido() + " no existe");
        break;
      case 2004:
        System.err.println("Error: El último mensaje estaba mal formado");
        break;
      case 1001:
        esMensaje = true;
        Contactos.addMensaje(conv,m);
        break;
      case 1002:
        esMensaje = true;
        Contactos.addMensaje(conv,m);
        break;
      default: 
        System.err.println("Error: Tipo de mensaje no reconocido");
        break;
    }
    
    if(esMensaje && Contactos.getConvActual().equals(conv))
      System.out.println(m);
  }

  public void run() {
    Mensaje m;
    try {
      while((m = (Mensaje) in.readObject()) != null) {
        parse(m);
      }
    }
    catch (IOException e) {
      System.err.println("Error de entrada/salida.");
    }
    catch(ClassNotFoundException e){
      System.err.println("Clase no encontrada");
    }
  }
}

/*
  Gestiona el envío de mensajes de un cliente en la parte del cliente.
*/
public class ClienteChat {
  private static String host = "localhost";
  private static int port = 8989;
  private static Socket socket = null;
  private static Escuchador esc = null;
  private static ObjectOutputStream outStream = null;
  private static String nombre = null;

  // Imprime un mensaje relacionado con el programa. No incluye salto de línea
  private static void programMessage(String mensaje) {
    System.out.print(mensaje);
  }

  // Imprime un mensaje de error
  private static void error(String mensaje) {
    System.err.println(mensaje);
  }

  private static boolean esComando(String mensaje) {
    return mensaje.charAt(0) == '/';
  }

  private static boolean parseComando(String mensaje) {
    // Obtiene comando y argumentos, si los hay
    String comando = mensaje.substring(1);
    String argumentos = "";
    int separador = comando.indexOf(" ");
    if (separador != -1) {
      argumentos = comando.substring(separador+1);
      comando = comando.substring(0, separador).toLowerCase();
    }

    // Interpreta el comando
    switch(comando) {
      case "close":
      case "exit":
      case "quit":
      case "q":
      case "salir":
        programMessage("Cerrando la conexión con el servidor...\n");
        return false;
      case "conversacion":
      case "c":
        if (!Contactos.setConvActual(argumentos))
          programMessage("Ya estás conversando " + (Contactos.convActualEsGrupo() ? "en" : "con")
                            + " " + argumentos + ".\n");
        else {
          // TODO: ¿limpiar chat y mostrar mensajes de la nueva conversación?
          // TODO: ¿debería fallar si el objetivo no está conectado?
          programMessage("Ahora estás hablando con " + argumentos + ".\n");
        }
        return true;
      // TODO: comandos para crear grupo, añadir a alguien a un grupo y tal vez ver quién hay en un grupo
      default:
        programMessage("Comando desconocido.\n");
        return true;
    }
  }

	public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    programMessage("Introduce la dirección del servidor: ");
    String nl = scanner.nextLine();
    if (nl.contains(":")) {
      host = nl.split(":")[0];
      port = Integer.parseInt(nl.split(":")[1]);
    }
    else if (!nl.isEmpty())
      host = nl;

    programMessage("Escoge un nombre de usuario: ");
    nombre = scanner.nextLine();
    Contactos.setConvActual(nombre);

		try {
		  // Envía el nombre
		  socket = new Socket(host,port);	
			outStream = new ObjectOutputStream(socket.getOutputStream());
      outStream.writeObject(new Mensaje(1000,nombre));

      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      Mensaje login = (Mensaje) ois.readObject();
      if (login.getCodigo() != 1000 || !login.getContenido().equals(nombre)) {
        if (login.getCodigo() == 2007)
          error("Error: el nombre " + login.getContenido() + " es inválido");
        else if (login.getCodigo() == 2008)
          error("Error: el nombre " + login.getContenido() + " está siendo usado");
        else
          error("Error desconocido");
        
        return;
      }

      programMessage("¡Conectado con éxito!\n");
      // TODO: si el servidor va a mandar más información, escucharla aquí

      // Empieza a escuchar mensajes en paralelo
      esc = new Escuchador(ois);
      esc.start();
		} catch (UnknownHostException e) {
			error("Error: Nombre de host no encontrado");
      return;
		} catch (IOException e) {
			error("Error de entrada/salida al abrir el socket");
      return;
		} catch(ClassNotFoundException e){
      error("Clase no encontrada");
    }

    String mensaje = scanner.nextLine();
    Mensaje aEnviar;
    boolean cierre = false;   // true si se ha introducido un comando para cerrar el chat
 		
 		try{
      do {
        if (esComando(mensaje))
          cierre = parseComando(mensaje);
        else {
          aEnviar = new Mensaje(Contactos.getConvActual(),new Date(),mensaje);
          outStream.writeObject(aEnviar);
        }
        mensaje = scanner.nextLine();
      } while (!cierre && mensaje != null);

      outStream.writeObject(new Mensaje(1999,"bye"));
    } catch (IOException e) {
			error("Error de entrada/salida durante el envío.");
		}
	}
}
