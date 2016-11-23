import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class Cliente extends Thread {
  private int id;
  private Socket socket = null;
  private PrintWriter outPrinter;
 	private BufferedReader inReader;

  public Cliente(int id, ServerSocket socketServidor) {
    this.id = id;
    try {
      socket = socketServidor.accept();
    } catch(IOException e) {
      System.out.println("Error: no se pudo aceptar la conexión solicitada (id = " + id + ")");
    }
    try {
      outPrinter = new PrintWriter(socket.getOutputStream(), true);
      inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
		}
  }

  public int getClientId() {
    return id;
  }

  public Socket getSocket() {
    return socket;
  }

  public void run() {
    if (!getLoginInfo()) return;

    login();
    listen();
  }

  // Recibe el nombre de usuario (y tal vez más datos) del cliente
  private boolean getLoginInfo() {
    return true;  // TODO
  }

  // Se llama tras un acceso correcto
  private void login() {
    // TODO. Podría mandar una lista de usuarios y/o avisar al resto de usuarios
    // Lo siguiente es temporal, para que se comporte como el ejercicio 3
    ProcesadorChat procesador=new ProcesadorChat(socket);
    procesador.start();
  }
  
  // Envía el mensaje mensaje al cliente
  public void sendMessage(String mensaje){
   	outPrinter.println(mensaje);
  }

  private void listen() {
    //TODO: Versión de prueba para ver si funciona
    String mensaje = "";
    
    try{
      while(mensaje != null){
        mensaje = inReader.readLine();
        System.out.println("Recibido el paquete " + mensaje);
      }
   	} catch (IOException e) {
			System.err.println("Error en la recepción del cliente (id = " + id + ")");
		}
		
		System.out.println("Recibido mensaje nulo del cliente (id = " + id + ")");
  }
}
