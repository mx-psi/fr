import java.io.IOException;   // De momento no se usa, pero probablemente termine siendo necesario
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

  public Cliente(int id, Socket socket) {
    this.id = id;
    this.socket = socket;
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
  }
  
  // Envía el mensaje mensaje al cliente
  public void sendMessage(String mensaje){
    System.out.println("Enviando mensaje al cliente " + id);
   	outPrinter.println(mensaje);
  }
  
  // Escucha los mensajes
  private void listen() {
    String mensaje = "";
    
    try{
      while(mensaje != null){
        mensaje = inReader.readLine();
        System.out.println("Recibido: \"" + mensaje + "\" del cliente " + id);
        if(mensaje != null)
          parse(mensaje);
      }
   	} catch (IOException e) {
			System.err.println("Error en la recepción del cliente (id = " + id + ")");
		}
		
		System.out.println("El cliente id = " + id + " se ha desconectado");
  }
  
  // Evalúa el mensaje y actúa en consecuencia
  private void parse(String mensaje){
    int codigo = Integer.parseInt(mensaje.substring(0,4));
    String[] datos = mensaje.substring(4).split(";");
    
    switch(codigo){
      case 1001: 
        ServidorChat.sendMessageToClient(Integer.parseInt(datos[0]),datos[1],datos[2]);
        break;
      case 1002: 
        // Envía mensaje a grupo
        break;
      case 1003: 
        // Añade usuario al grupo
        break;
      default: 
        // Error, mensaje no reconocido
        break;
    }
  }
}
