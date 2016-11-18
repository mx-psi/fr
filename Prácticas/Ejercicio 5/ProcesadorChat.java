import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ProcesadorChat extends Thread {

	private Socket socket;
	private BufferedReader inReader;
	private PrintWriter outPrinter;

	public ProcesadorChat(Socket socket) {
		this.socket=socket;
	}
	
	public void run(){

		byte [] datosRecibidos=new byte[1024];
		int bytesRecibidos=0;
		byte [] datosEnviar;
			
		try {
			// Obtiene los flujos de escritura/lectura
			inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outPrinter = new PrintWriter(socket.getOutputStream(),true);
			
			String peticion= inReader.readLine();
			String respuesta= "TODO";
			outPrinter.println(respuesta);
			
		} catch (IOException e) {
			System.err.println("Error al obtener los flujso de entrada/salida.");
		}
	}
}
