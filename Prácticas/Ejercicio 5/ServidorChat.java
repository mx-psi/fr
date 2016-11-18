import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorChat {

	public static void main(String[] args) {

		int port=8989;
		byte []buffer=new byte[256];
		int bytesLeidos=0;
		
		try {
			 ServerSocket socketServidor = new ServerSocket(port);

			do {
				Socket socketServicio = null;
				try{
  				socketServicio = socketServidor.accept();
				} catch(IOException e){
				  System.out.println("Error: no se pudo aceptar la conexión solicitada");
				}				

				ProcesadorYodafy procesador=new ProcesadorYodafy(socketServicio);
				procesador.start();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}
