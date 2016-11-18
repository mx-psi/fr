//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

public class YodafyClienteTCP {

	public static void main(String[] args) {
		
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		DatagramPacket paquete;
		
		// Socket para la conexión TCP
		DatagramSocket socketServicio=null;
		
		try {
			InetAddress direccion= InetAddress.getByName(host);
		  socketServicio = new DatagramSocket();
			
			buferEnvio="Al monte del volcan debes ir sin demora".getBytes();
			paquete = new DatagramPacket(buferEnvio,buferEnvio.length, direccion, port);
      socketServicio.send(paquete);
            			
			// Leemos la respuesta del servidor
			paquete = new DatagramPacket(buferRecepcion, buferRecepcion.length);
			socketServicio.receive(paquete);
		  paquete.getData();
		  paquete.getAddress();
		  paquete.getPort();
			
			// Mostremos la cadena de caracteres recibidos:
			System.out.println("Recibido: ");
			for(int i=0;i<buferRecepcion.length;i++){
				System.out.print((char)buferRecepcion[i]);
			}
			
			// Una vez terminado el servicio, cerramos el socket
      socketServicio.close();
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
