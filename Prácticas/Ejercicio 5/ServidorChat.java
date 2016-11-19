import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorChat {
	public static void main(String[] args) {
    int port = 8989;
    if (args.length == 1)
      port = Integer.parseInt(args[0]);

		try {
			 ServerSocket socketServidor = new ServerSocket(port);

			while (true) {
				Socket socketServicio = null;
				try {
  				socketServicio = socketServidor.accept();
				} catch(IOException e){
				  System.out.println("Error: no se pudo aceptar la conexión solicitada");
				}

				ProcesadorChat procesador=new ProcesadorChat(socketServicio);
				procesador.start();
			}
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto " + port);
		}
	}
}
