import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Scanner;
import java.util.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;


/*
  Escucha e interpreta los mensajes enviados por el servidor
*/
class Escuchador extends Thread {
  private ObjectInputStream in = null;

  public Escuchador(ObjectInputStream br) {
    in = br;
  }

  private boolean Soy(String n) {
    return ClienteChat.getNombre().equals(n);
  }

  private void parse(Mensaje m) {
    String conv = m.getConversacion();
    String nombre;
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
      case 1002:
        esMensaje = true;
        Contactos.addMensaje(conv,m);
        break;
      case 1004:
        esMensaje = true;
        leerFichero(m);
        break;
      case 1996:
        nombre = m.getUsuario();
        String nGrupo = m.getGrupo();
        esMensaje = !Soy(nombre);
        if (esMensaje) {
          m = new Mensaje(0, nombre + " se ha incorporado al grupo");
          if (Contactos.hayConversacionCon(nGrupo))
            Contactos.addMensaje(nGrupo, m);
          // TODO: añadir al usuario a la lista de miembros de nGrupo
        } else {
          Contactos.iniciaConversacionCon(nGrupo, true);
          // TODO: ¿cómo avisar al usuario de que ha entrado al grupo?
        }
        break;
      case 1997:
      case 1998:
        nombre = m.getUsuario();
        if (Soy(nombre)) return;
        esMensaje = !Contactos.convActualEsGrupo() /* || TODO: método que dé si el usuario estaba en el grupo */;
        boolean conectado = m.getCodigo() == 1997;
        m = new Mensaje(0, nombre + " se ha " + (conectado ? "" : "des") + "conectado."); // Mensaje que se mostrará al cliente
        if (Contactos.hayConversacionCon(nombre))
          Contactos.addMensaje(conv, m);
        // TODO: almacenar m en todos los grupos en común con el usuario
        // TODO: si 1997, añadir al usuario a la lista de conectados
        // TODO: si 1998, quitar usuario de la lista de conectados y de los grupos en los que estuviese
        break;
      default: 
        System.err.println("Error: Tipo de mensaje no reconocido");
        break;
    }
    
    if(esMensaje && Contactos.getConvActual().equals(conv))
      System.out.println(m);
  }

  private void leerFichero(Mensaje m) {
    try {
      FileOutputStream fos = new FileOutputStream("./Recibidos/" + m.getRuta());
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      byte[] contenido = m.getRawContenido();
      bos.write(contenido, 0, contenido.length);
      bos.flush();
    } 
    catch (FileNotFoundException e){
      System.err.println("Fichero no encontrado en la recepción " + m.getRuta());
    }
    catch (IOException e){
      System.err.println("Error al intentar recibir el fichero " + m.getRuta());
    }
  }

  public void run() {
    Mensaje m;
    try {
      while((m = (Mensaje) in.readObject()) != null) {
        parse(m);
      }
    }
    catch (IOException e) {
      System.err.println("Error de entrada.");
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
  private static Scanner scanner = new Scanner(System.in);

  public static String getNombre() {
    return nombre;
  }

  // Pregunta algo al cliente, que responde desde teclado
  public static String ask(String texto) {
    programMessage(texto + ": ");
    return scanner.nextLine();
  }

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

  private static void enviaFichero(String ruta) {
    try {
      File file = new File(ruta);
      byte [] rawFichero = new byte [(int)file.length()];
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      bis.read(rawFichero,0,rawFichero.length);
      // TODO: Comprobar si es grupo
      outStream.writeObject(new Mensaje(Contactos.getConvActual(),rawFichero,file.getName()));
    }
    catch (FileNotFoundException e) {
      error("Fichero no encontrado: " + ruta);
    }
    catch (IOException e) {
      error("Error al intentar mandar el fichero " + ruta);
    }
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
          // TODO: ¿debería fallar si el usuario o grupo objetivo no está conectado?
          // TODO: ¿debería fallar si no está en el grupo?
          // TODO: clear?
          boolean es_grupo = false; // TODO
          programMessage("\nAhora estás hablando " + (es_grupo ? "en" : "con") + " " + argumentos + ".\n");
          Contactos.iniciaConversacionCon(argumentos, es_grupo);
          Contactos.mostrarMensajes();
        }
        return true;
      case "g":
      case "grupo":
      case "group":
      case "newgroup":
        programMessage("TODO"); // TODO: comando para crear grupo
        return true;
      case "a":
      case "add":
      case "anadir":
        programMessage("TODO"); // TODO: comando para añadir a alguien al grupo actual
        return true;
      // TODO: comandos para ver la lista de conectados y la de miembros de un grupo en particular
      case "h":
      case "?":
      case "help":
      case "comandos":
        programMessage("Lista de comandos:\n"
                     + "/a usuario: añadir a un usuario al grupo actual"
                     + "/c usuario/grupo: pasar a hablar con un usuario o grupo\n"
                     + "/g grupo: crear un grupo"
                     + "/s archivo: mandar un archivo\n"
                     + "/q: salir\n");  // TODO: añadir el resto de comandos
        return true;
      case "s":
      case "send":
      case "envia":
        enviaFichero(argumentos);
        return true;
      default:
        programMessage("Comando desconocido.\n");
        return true;
    }
  }

	public static void main(String[] args) {
    String nl = ask("Introduce la dirección del servidor");
    if (nl.contains(":")) {
      host = nl.split(":")[0];
      port = Integer.parseInt(nl.split(":")[1]);
    }
    else if (!nl.isEmpty())
      host = nl;

    nombre = ask("Escoge un nombre de usuario");
    Contactos.iniciaConversacionCon("Global", true);    // Entra en el grupo Global
    Contactos.setConvActual("Global");

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

      programMessage("¡Conectado con éxito!\nUsa /help para ver la lista de comandos\n\n");
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

    String mensaje;
    Mensaje aEnviar;
    boolean persiste = true;   // false si se ha introducido un comando para cerrar el chat
 		
 		try {
      do {
        mensaje = scanner.nextLine().trim();
        if (mensaje != null && !mensaje.equals("")) {
          if (esComando(mensaje))
            persiste = parseComando(mensaje);
          else {
            aEnviar = new Mensaje(Contactos.getConvActual(), mensaje, Contactos.convActualEsGrupo());
            outStream.writeObject(aEnviar);
          }
        }
      } while (persiste);

      outStream.writeObject(new Mensaje(1999,"bye"));
    } catch (IOException e) {
			error("Error durante el envío");
		}
	}
}
