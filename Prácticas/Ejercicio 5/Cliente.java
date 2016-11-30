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
    inStream = new ObjectInputStream(socket.getInputStream());
    outStream = new ObjectOutputStream(socket.getOutputStream());
    
    // Obtiene el nombre del cliente
    Mensaje primerMensaje = (Mensaje) inStream.readObject();
    String nombrePedido = primerMensaje.getContenido();
    int error = 0;
    boolean repetido = false;
    if (primerMensaje.getCodigo() != 1000)
      error = 1;
    else if (nombrePedido.getBytes(StandardCharsets.UTF_8).length() > ServidorChat.MAX_NAME_LENGTH
              || nombrePedido.length() < 1
              || (repetido = ServidorChat.nombreUsado(nombrePedido)))
      error = 2;

    if (error != 0) {
      sendMessage(new Mensaje(repetido ? 2008 : 2007, nombrePedido));
      socket.close();
      if (error == 1)
        throw new IllegalNameException();

      throw new IllegalNameException(nombrePedido, repetido);
    }
    name = primerMensaje.getContenido();
  }

  // Devuelve el nombre del cliente
  public String getClientName() {
    return name;
  }

  // Hebra que se encarga de recibir datos de este cliente
  public void run() {
    if (getLoginInfo()) {
      login();
      listen();
    }
    disconnect();
  }

  // Recibe datos del cliente
  private boolean getLoginInfo() {
    return true;  // TODO
  }

  // Se llama tras un acceso correcto
  private void login() {
    sendMessage(new Mensaje(1000, name));  // Confirma nombre de usuario
    System.out.println("El cliente " + name + " se ha conectado");
    Mensaje m = new Mensaje(1997, "");
    m.setUsuario(name);

    ServidorChat.sendGroupAndUserLists(this);
    ServidorChat.sendToAllClients(m);
    sendMessage(new Mensaje(1994, "end"));
    ServidorChat.addClientToGlobalGroup(name);
  }
  
  // Envía el mensaje mensaje al cliente
  public void sendMessage(Mensaje mensaje){
    System.out.println("Enviando mensaje al cliente " + name);
    try {
      outStream.writeObject(mensaje);
   	} catch(IOException e) {
   	  System.err.println("Error en el envío al cliente " + name);
   	}
  }
  
  // Escucha los mensajes
  private void listen() {
    Mensaje mensaje;
    
    try {
      do {
        mensaje = (Mensaje) inStream.readObject();
        System.out.println("Recibido: \"" + mensaje.getContenido() + "\" del cliente " + name);
        if (mensaje != null)
          if (!parse(mensaje))
            mensaje = null;
      } while(mensaje != null);
   	} catch (IOException e) {
			System.err.println("Error en la recepción del cliente (name = " + name + ")");
		} catch (ClassNotFoundException e) {
			System.err.println("Error en la recepción del cliente (name = " + name + ")");
		}
  }

  private void disconnect() {
		System.out.println("El cliente " + name + " se ha desconectado");
    ServidorChat.removeClient(name);
    Mensaje m = new Mensaje(1998, "");
    m.setUsuario(name);
    ServidorChat.sendToAllClients(m);
  }
  
  // Evalúa el mensaje y actúa. Devuelve false si se debe cerrar la conexión
  private boolean parse(Mensaje mensaje){
    String destinatario = mensaje.getConversacion();
    switch(mensaje.getCodigo()){
      case 1001:
      case 1004:
        mensaje.setUsuario(name);
        if(!ServidorChat.sendToClient(destinatario, mensaje))
          sendMessage(new Mensaje(2001,destinatario));
        break;
      case 1002:
        mensaje.setUsuario(name);
        if (!ServidorChat.sendToGroup(destinatario, mensaje))
          sendMessage(new Mensaje(2002,destinatario));
        break;
      case 1003: 
        ServidorChat.addClientToGroup(mensaje.getGrupo(), mensaje.getUsuario(), name);
        break;
      case 1996:
        ServidorChat.sendGroupMembers(this, mensaje.getContenido());
        break;
      case 1999:
        try {
          socket.shutdownOutput();
        } catch (IOException e) {}
        return false;
      default: 
        System.err.println("Error: Código no reconocido \""+ mensaje.getCodigo() +"\"");
        sendMessage(new Mensaje(2004,""));
        break;
    }
    return true;
  }
}
