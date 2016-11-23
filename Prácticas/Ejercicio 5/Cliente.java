import java.io.IOException;   // De momento no se usa, pero probablemente termine siendo necesario
import java.net.Socket;

public class Cliente extends Thread {
  private int id;
  private Socket socket = null;

  public Cliente(int id, Socket socket) {
    this.id = id;
    this.socket = socket;
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

  private void listen() {
    return; // TODO. En bucle, esperar paquetes del cliente y ejecutar acciones en función de estos
  }
}
