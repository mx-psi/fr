import java.io.IOException;
import java.net.ServerSocket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
public class YodafyServidorIterativo {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
		// array de bytes auxiliar para recibir o enviar datos.
		byte []buffer=new byte[256];
		// Número de bytes leídos
		int bytesLeidos=0;
		
		try {
			// Mientras ... siempre!
  		 DatagramSocket socketServidor = new DatagramSocket(port);
			do {
			 // Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			 ProcesadorYodafy procesador=new ProcesadorYodafy(socketServidor);
			 procesador.procesa();
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto " + port);
		}

	}

}
