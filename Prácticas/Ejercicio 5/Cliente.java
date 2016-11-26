import java.io.IOException;
import java.util.IllegalFormatException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/*
  Guarda la información de cada cliente y gestiona la recepción de sus 
  mensajes en el servidor.
*/
public class Cliente extends Thread {
  private final String name;
  private Socket socket = null;
  private ObjectOutputStream outStream;
  private ObjectInputStream inStream;

  public Cliente(Socket socket) throws IOException, IllegalNameException, ClassNotFoundException {
    this.socket = socket;
    outStream = new ObjectOutputStream(socket.getOutputStream());
    inStream = new ObjectInputStream(socket.getInputStream());
    
    // Obten el nombre del cliente
    Mensaje primerMensaje = (Mensaje) inStream.readObject();
    if (primerMensaje.getCodigo() == 1000 && primerMensaje.getContenido().length() <= 20)
      name = primerMensaje.getContenido();
    else
      throw new IllegalNameException(primerMensaje.getContenido());
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
  public void sendMessage(Mensaje mensaje){
    System.out.println("Enviando mensaje al cliente " + name);
    try{
   	outStream.writeObject(mensaje);
   	}catch(IOException e){
   	  System.err.println("Error en el envío al cliente " + name);
   	}
  }
  
  // Escucha los mensajes
  private void listen() {
    Mensaje mensaje;
    
    try{
       do {
        mensaje = (Mensaje) inStream.readObject();
        System.out.println("Recibido: \"" + mensaje.getContenido() + "\" del cliente " + name);
        if (mensaje != null)
          if (!parse(mensaje))
            mensaje = null;
      } while(mensaje != null);
   	} catch (IOException e) {
			System.err.println("Error en la recepción del cliente (name = " + name + ")");
		}
		catch (ClassNotFoundException e) {
			System.err.println("Error en la recepción del cliente (name = " + name + ")");
		}

		System.out.println("El cliente " + name + " se ha desconectado");
  }
  
  // Evalúa el mensaje y actúa. Devuelve false si se debe cerrar la conexión
  private boolean parse(Mensaje mensaje){
    String destinatario = mensaje.getConversacion();
    switch(mensaje.getCodigo()){
      case 1001:
        mensaje.setUsuario(name);
        if(!ServidorChat.sendToClient(destinatario, mensaje))
          ServidorChat.sendToClient(name, new Mensaje(2001,destinatario));
        break;
      case 1002:
        mensaje.setUsuario(name);
        if (!ServidorChat.sendToGroup(destinatario, mensaje))
          ServidorChat.sendToClient(name, new Mensaje(2002,destinatario));
        break;
      case 1003: 
        ServidorChat.addClientToGroup(mensaje.getGrupo(), mensaje.getUsuario(), name);
        break;
      case 1999:
        // TODO: avisar a otros clientes de la desconexión
        try {
          socket.shutdownOutput();
        } catch (IOException e) {}
        return false;
      default: 
        System.err.println("Error: Código no reconocido \""+ mensaje.getCodigo() +"\"");
        ServidorChat.sendToClient(name, new Mensaje(2004,""));
        break;
    }
    return true;
  }
}
