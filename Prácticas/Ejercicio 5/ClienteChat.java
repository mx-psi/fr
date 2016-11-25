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
          printMessage(datos[0],datos[1],datos[2],null);
          break;
        case 1005:
          printMessage(datos[1], datos[2],datos[3],datos[0]);
          break;
        default: 
          System.err.println("Error: Tipo de mensaje no reconocido");
          break;
      }
    } catch(java.lang.ArrayIndexOutOfBoundsException e){
      System.err.println("Error: Mensaje mal formado \""+ mensaje +"\"");
    }
    catch(NumberFormatException e){
      System.err.println("Error: Código mal formado \""+ mensaje +"\"");
    }
  }

  // Imprime un mensaje de un usuario
  private synchronized static void printMessage(String usuario, String date, String mensaje, String grupo) {
    System.out.println("[" + date + "]" + (grupo == null ? "" : " (" + grupo + ")")
                        + " " + usuario + ": " + mensaje);
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
  	SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
		String buferEnvio;
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

		try {
		  socketServicio = new Socket(host,port);	

			outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);

      outPrinter.println("1000" + nombre);
      esc = new Escuchador(new BufferedReader(new InputStreamReader(socketServicio.getInputStream())));
      esc.start();
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}

    String mensaje = "Esto es un mensaje de prueba";
    do {
      buferEnvio = "1001NombreDePrueba;" + ft.format(new Date()) + ";" + mensaje;
      outPrinter.println(buferEnvio);
      mensaje = scanner.nextLine();
    } while (!mensaje.equals(""));

    outPrinter.println("1999bye");
	}
}
