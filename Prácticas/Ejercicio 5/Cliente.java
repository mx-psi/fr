import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Cliente {
  private int id;
  private Socket socket = null;

  public Cliente(int id, ServerSocket socketServidor) {
    this.id = id;
    try {
      socket = socketServidor.accept();
    } catch(IOException e) {
      System.out.println("Error: no se pudo aceptar la conexión solicitada (id = " + id + ")");
    }
  }

  public int getId() {
    return id;
  }

  public Socket getSocket() {
    return socket;
  }
}
