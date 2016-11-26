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
        Contactos.addMensaje(conv,m);
        break;
      case 1002:
        Contactos.addMensaje(conv,m);
        break;
      default: 
        System.err.println("Error: Tipo de mensaje no reconocido");
        break;
    }
    
    if(m.getCodigo() < 2000 && Contactos.getConvActual().equals(conv))
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

	public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Introduce la dirección del servidor: ");
    String nl = scanner.nextLine();
    if (nl.contains(":")) {
      host = nl.split(":")[0];
      port = Integer.parseInt(nl.split(":")[1]);
    }
    else if (!nl.isEmpty())
      host = nl;

    System.out.print("Escoge un nombre de usuario: ");
    String nombre = scanner.nextLine();
    Contactos.setConvActual(nombre);

		try {
		  // Envía el nombre
		  socket = new Socket(host,port);	
			outStream = new ObjectOutputStream(socket.getOutputStream());
      outStream.writeObject(new Mensaje(1000,nombre));
      
      // Empieza a escuchar mensajes en paralelo
      esc = new Escuchador(new ObjectInputStream(socket.getInputStream()));
      esc.start();
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
		
    String mensaje = "Esto es un mensaje de prueba";
    Mensaje aEnviar;
 		
 		try{
 		// Envía mensajes hasta que haya uno vacío
    do {
      aEnviar = new Mensaje(Contactos.getConvActual(),new Date(),mensaje);
      outStream.writeObject(aEnviar);
      mensaje = scanner.nextLine();
    } while (mensaje != null);

    outStream.writeObject(new Mensaje(1999,"bye"));
    } catch (IOException e) {
			System.err.println("Error de entrada/salida durante el envío.");
		}
	}
}
