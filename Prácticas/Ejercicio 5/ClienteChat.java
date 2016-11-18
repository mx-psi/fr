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

public class ClienteChat {

	public static void main(String[] args) {
		
		String buferEnvio;
		String buferRecepcion;
		int bytesLeidos=0;
		
		String host="localhost";
		int port=8989;
		Socket socketServicio=null;
		
		try {
		  socketServicio = new Socket(host,port);	
		  
      BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);
			
			buferEnvio="Al monte del volcán debes ir sin demora";
			outPrinter.println(buferEnvio);
      			
			buferRecepcion = inReader.readLine();
			System.out.println("Recibido: ");
			System.out.println(buferRecepcion);
			
      socketServicio.close();
			
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
