import java.io.IOException;
import java.util.IllegalFormatException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

/*
  Guarda la información de cada cliente y gestiona la recepción de sus 
  mensajes en el servidor.
*/
public class Cliente extends Thread {
  private final String name;
  private Socket socket = null;
  private PrintWriter outPrinter;
 	private BufferedReader inReader;

  public Cliente(Socket socket) throws IOException, IllegalNameException {
    this.socket = socket;
    outPrinter = new PrintWriter(socket.getOutputStream(), true);
    inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    // Obten el nombre del cliente
    String primerMensaje = inReader.readLine();
    String codigo = primerMensaje.substring(0,4);
    if (codigo.equals("1000") && primerMensaje.length() <= 24)
      name = primerMensaje.substring(4);
    else
      throw new IllegalNameException(primerMensaje);
  }
  
  // Devuelve el nombre del cliente
  public String getClientName() {
    return name;
  }
  
  // Devuelve el socket asociado al cliente
  public Socket getSocket() {
    return socket;
  }
  
  // Hebra que se encarga de recibir datos de este cliente
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
      while(mensaje != null) {
        mensaje = inReader.readLine();
        System.out.println("Recibido: \"" + mensaje + "\" del cliente " + name);
        if (mensaje != null)
          if (!parse(mensaje))
            mensaje = null;
      }
   	} catch (IOException e) {
			System.err.println("Error en la recepción del cliente (name = " + name + ")");
		}

		System.out.println("El cliente " + name + " se ha desconectado");
  }
  
  // Evalúa el mensaje y actúa. Devuelve false si se debe cerrar la conexión
  private boolean parse(String mensaje){
    try{
    int codigo = Integer.parseInt(mensaje.substring(0,4), 10);
    String[] datos = mensaje.substring(4).split(";");
    
    switch(codigo){
      case 1001: 
        if(!ServidorChat.sendToClient("1004", datos[0], name + ";" + datos[1] + ";" + datos[2]))
          ServidorChat.sendToClient("2001", name, datos[0]);
        break;
      case 1002: 
        if (!ServidorChat.sendToGroup("1005", datos[0], name + ";" + datos[1] + ";" + datos[2]))
          ServidorChat.sendToClient("2002", name, datos[0]);
        break;
      case 1003: 
        ServidorChat.addClientToGroup(datos[0], datos[1], name);
        break;
      case 1999:
        // TODO: avisar a otros clientes de la desconexión
        try {
          socket.shutdownOutput();
        } catch (IOException e) {}
        return false;
      default: 
        System.err.println("Error: Código no reconocido \""+ codigo +"\"");
        ServidorChat.sendToClient("2004", name, "");
        break;
    }
    } catch(java.lang.ArrayIndexOutOfBoundsException e){
      System.err.println("Error: Mensaje mal formado \""+ mensaje +"\"");
      ServidorChat.sendToClient("2004", name, "");
    }
    catch(NumberFormatException e){
      System.err.println("Error: Código mal formado \""+ mensaje +"\"");
      ServidorChat.sendToClient("2004", name, "");
    }
    return true;
  }
}
