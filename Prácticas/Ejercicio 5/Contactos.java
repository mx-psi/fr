import java.util.HashMap;
import java.util.ArrayList;

/*
  Lista de contactos y grupos con mensajes
*/
public class Contactos {
  private static HashMap<String,ArrayList<Mensaje>> mensajes = 
  new HashMap<String,ArrayList<Mensaje>>();
  private static String convActual;
  
  // Añade un mensaje a la lista de un contacto
  public static void addMensaje(String conv, Mensaje mensaje){
    if (!mensajes.containsKey(conv))
      mensajes.put(conv, new ArrayList<Mensaje>());
    mensajes.get(conv).add(mensaje);
  }

  // Obtiene los mensajes de un contacto
  public static ArrayList<Mensaje> getMensajes(String conv){
    return mensajes.get(conv);
  }

  public static String getConvActual(){
    return convActual;
  }

  public static void setConvActual(String conv){
    convActual = conv;
  }
}

