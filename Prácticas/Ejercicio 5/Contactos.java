import java.util.HashMap;
import java.util.ArrayList;

/*
  Lista de contactos y grupos con mensajes
*/
public class Contactos {
  private static HashMap<String,Conversacion> conversaciones = new HashMap<String,Conversacion>();
  private static String convActual;

  // Determina si se ha recibido un mensaje de una conversación o se ha abierto
  public static boolean hayConversacionCon(String conv) {
    return conversaciones.containsKey(conv);
  }

  // Marca una conversación como iniciada. Devuelve false si ya estaba iniciada
  public static boolean iniciaConversacionCon(String conv, boolean es_grupo) {
    if (!hayConversacionCon(conv)) {
      conversaciones.put(conv, new Conversacion(es_grupo));
      return true;
    }
    return false;
  }

  // Añade un mensaje a la lista de un contacto
  public synchronized static void addMensaje(String conv, Mensaje mensaje){
    if (!hayConversacionCon(conv))
      conversaciones.put(conv, new Conversacion(mensaje.esDeGrupo()));

    conversaciones.get(conv).add(mensaje);
  }

  // Añade un mensaje a todos los grupos en común con un usuario
  public static void addMensajeToCommonGroups(Mensaje mensaje, String usuario) {
    for (String conv:conversaciones.keySet())
      if (conversaciones.get(conv).esGrupo() && ClienteChat.isInGroup(usuario, conv))
        addMensaje(conv, mensaje);
  }

  // Obtiene los mensajes de un contacto
  public static Conversacion getMensajes(String conv){
    return conversaciones.get(conv);
  }

  // Obtiene los mensajes de la conversación actual
  public static Conversacion getMensajes() {
    return getMensajes(convActual);
  }

  public static void mostrarMensajes(String conv) {
    if (getMensajes(conv) != null)
      getMensajes(conv).mostrar();
  }

  public static void mostrarMensajes() {
    mostrarMensajes(convActual);
  }

  public static String getConvActual(){
    return convActual;
  }

  public static boolean convActualEsGrupo() {
    return getMensajes() != null && getMensajes().esGrupo();
  }

  public static ArrayList<String> getConversaciones() {
    return new ArrayList<String>(conversaciones.keySet());
  }

  // Devuelve 1 en caso de éxito, 0 si no ha cambiado la conversación
  public static boolean setConvActual(String conv){
    if (conv == convActual)
      return false;

    convActual = conv;
    return true;
  }
}

