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

public class ClienteChat {
  
  private static void parse(String mensaje){
    try{
      int codigo = Integer.parseInt(mensaje.substring(0,4));
      String[] datos = mensaje.substring(4).split(";");

    switch(codigo){
      case 2001: 
        System.err.println("Error: El usuario " + datos[0] + "no existe");
        break;
      case 2002: 
        System.err.println("Error: El grupo " + datos[0] + "no existe");
        break;
      case 2004:
        System.err.println("Error: El último mensaje estaba mal formado");
        break;
      case 1004:
        printMessage(Integer.parseInt(datos[0]),datos[1],datos[2],null);
        break;
      case 1005:
        printMessage(Integer.parseInt(datos[0]),datos[2],datos[3],datos[1]);
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
  
  // Imprime mensaje
  private static void printMessage(int id, String date, String mensaje, String grupo){
    System.out.println("Cliente " + id + " dice: " + mensaje);
    if(grupo != null)
      System.out.println("\ten el grupo " + grupo);
    System.out.println("\ta las " + date);
  }
  
	public static void main(String[] args) {
		String buferEnvio;
		String buferRecepcion;
		int bytesLeidos=0;
    Scanner scanner = new Scanner(System.in);

		String host = "localhost";
    int port = 8989;

    System.out.print("Introduce la dirección del servidor: ");
    String nl = scanner.nextLine();
    if (nl.contains(":")) {
      host = nl.split(":")[0];
      port = Integer.parseInt(nl.split(":")[1]);
    }
    else if (!nl.isEmpty())
      host = nl;

		Socket socketServicio=null;

		try {
		  socketServicio = new Socket(host,port);	

      BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);
      
   	  buferEnvio="10010;fecha;Esto es un mensaje de prueba";
 			outPrinter.println(buferEnvio);
 			
   		System.out.println("Esperando mensaje del servidor...");
  		buferRecepcion = inReader.readLine();
  		parse(buferRecepcion);
     	
      socketServicio.close();
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
