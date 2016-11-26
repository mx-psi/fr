import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Scanner;
import java.util.Date;

import java.text.SimpleDateFormat;


/*
  Escucha e interpreta los mensajes enviados por el servidor
*/
class Escuchador extends Thread {
  private BufferedReader in = null;

  public Escuchador(BufferedReader br) {
    in = br;
  }

  private void parse(String mensaje) {
    try {
      int codigo = Integer.parseInt(mensaje.substring(0,4), 10);
      String[] datos = mensaje.substring(4).split(";");
      Mensaje m = null;

      switch(codigo){
        case 2001: 
          System.err.println("Error: El usuario " + datos[0] + " no existe");
          break;
        case 2002: 
          System.err.println("Error: El grupo " + datos[0] + " no existe");
          break;
        case 2004:
          System.err.println("Error: El último mensaje estaba mal formado");
          break;
        case 1004:
          m = new Mensaje(datos[0],datos[1],datos[2]);
          Contactos.addMensaje(datos[0], m);
          break;
        case 1005:
          m =  new Mensaje(datos[1],datos[2],datos[3],datos[0]);
          Contactos.addMensaje(datos[1],m);
          break;
        default: 
          System.err.println("Error: Tipo de mensaje no reconocido");
          break;
      }
      
      if(m != null && Contactos.getConvActual().equals(datos[0]))
        System.out.println(m);
        
    } catch(java.lang.ArrayIndexOutOfBoundsException e){
      System.err.println("Error: Mensaje mal formado \""+ mensaje +"\"");
    }
    catch(NumberFormatException e){
      System.err.println("Error: Código mal formado \""+ mensaje +"\"");
    }
  }

  public void run() {
    String line;
    try {
      while((line = in.readLine()) != null) {
        parse(line);
      }
    }
    catch (IOException e) {
      System.err.println("Error de entrada/salida.");
    }
  }
}

/*
  Gestiona el envío de mensajes de un cliente en la parte del cliente.
*/
public class ClienteChat {
  private static String host = "localhost";
  private static int port = 8989;
  private static Socket socketServicio = null;
  private static Escuchador esc = null;
  private static PrintWriter outPrinter = null;

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
		  socketServicio = new Socket(host,port);	
			outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);
      outPrinter.println("1000" + nombre);
      
      // Empieza a escuchar mensajes en paralelo
      esc = new Escuchador(new BufferedReader(new InputStreamReader(socketServicio.getInputStream())));
      esc.start();
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
		
   	SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
    String mensaje = "Esto es un mensaje de prueba";
 		String buferEnvio;
 		
 		// Envía mensajes hasta que haya uno vacío
    do {
      buferEnvio = "1001NombreDePrueba;" + ft.format(new Date()) + ";" + mensaje;
      outPrinter.println(buferEnvio);
      mensaje = scanner.nextLine();
    } while (!mensaje.equals(""));

    outPrinter.println("1999bye");
	}
}
