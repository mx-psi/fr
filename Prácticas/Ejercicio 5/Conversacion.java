import java.util.ArrayList;

/*
  Conversación con un usuario o un grupo
*/
public class Conversacion {
  private ArrayList<Mensaje> mensajes;
  private static final int maxMensajes = 1024;
  private boolean grupo;

  public Conversacion(boolean g) {
    grupo = g;
    mensajes = new ArrayList<Mensaje>();
  }

  public boolean esGrupo() {
    return grupo;
  }

  // Añade un mensaje
  public void add(Mensaje m) {
    if (mensajes.size() == maxMensajes)
      mensajes.remove(0);

    mensajes.add(m);
  }

  public void mostrar() {
    for (Mensaje m:mensajes)
      System.out.println(m);
  }
}