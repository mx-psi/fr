import java.io.IOException;
import java.util.IllegalFormatException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class Cliente extends Thread {
  private final String name;
  private Socket socket = null;
  private PrintWriter outPrinter;
 	private BufferedReader inReader;

  public Cliente(Socket socket) throws IOException, IllegalNameException {
    this.socket = socket;

    outPrinter = new PrintWriter(socket.getOutputStream(), true);
    inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String primerMensaje = inReader.readLine();

    String codigo = primerMensaje.substring(0,4);
    if (codigo.equals("1004") && primerMensaje.length() <= 24)
      name = primerMensaje.substring(4);
    else
      throw new IllegalNameException(primerMensaje);
  }

  public String getClientName() {
    return name;
  }

  public Socket getSocket() {
    return socket;
  }

  public void run() {
    if (!getLoginInfo()) return;

    login();
    listen();
  }

  // Recibe datos del cliente
  private boolean getLoginInfo() {
    return true;  // TODO
  }

  // Se llama tras un acceso correcto
  private void login() {
    // TODO. Podría mandar una lista de usuarios y/o avisar al resto de usuarios
  }
  
  // Envía el mensaje mensaje al cliente
  public void sendMessage(String mensaje){
    System.out.println("Enviando mensaje al cliente " + name);
   	outPrinter.println(mensaje);
  }
  
  // Escucha los mensajes
  private void listen() {
    String mensaje = "";
    
    try{
      while(mensaje != null){
        mensaje = inReader.readLine();
        System.out.println("Recibido: \"" + mensaje + "\" del cliente " + name);
        if(mensaje != null)
          parse(mensaje);
      }
   	} catch (IOException e) {
			System.err.println("Error en la recepción del cliente (name = " + name + ")");
		}
		
		System.out.println("El cliente " + name + " se ha desconectado");
  }
  
  // Evalúa el mensaje y actúa en consecuencia
  private void parse(String mensaje){
    try{
    int codigo = Integer.parseInt(mensaje.substring(0,4), 10);
    String[] datos = mensaje.substring(4).split(";");
    
    switch(codigo){
      case 1001: 
        if(!ServidorChat.sendMessageToClient("1004", datos[0], name + ";" + datos[1] + ";" + datos[2]))
          ServidorChat.sendMessageToClient("2001", name, datos[0]);
        break;
      case 1002: 
        if (!ServidorChat.sendMessageToGroup("1005", datos[0], name + ";" + datos[1] + ";" + datos[2]))
          ServidorChat.sendMessageToClient("2002", name, datos[0]);
        break;
      case 1003: 
        ServidorChat.addClientToGroup(datos[0], datos[1], name);
        break;
      default: 
        System.err.println("Error: Código no reconocido \""+ codigo +"\"");
        ServidorChat.sendMessageToClient("2004", name, "");
        break;
    }
    } catch(java.lang.ArrayIndexOutOfBoundsException e){
      System.err.println("Error: Mensaje mal formado \""+ mensaje +"\"");
      ServidorChat.sendMessageToClient("2004", name, "");
    }
    catch(NumberFormatException e){
      System.err.println("Error: Código mal formado \""+ mensaje +"\"");
      ServidorChat.sendMessageToClient("2004", name, "");
    }
  }
}
