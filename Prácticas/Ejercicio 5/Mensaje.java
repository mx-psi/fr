import java.util.Date;
import java.text.SimpleDateFormat;

public class Mensaje implements java.io.Serializable {
  private int codigo;       // Código
  private Date date;        // Fecha de creación del mensaje
  private String usuario;   // Usuario que envía
  private String grupo;     // Grupo al que se envía
  private String ruta;      // Ruta de contenido
  private byte[] contenido; // Contenido del mensaje
  public boolean esDeChat;  // indica si se trata de un mensaje de chat
  private static SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
  
  // Constructor de mensajes informativos
  public Mensaje(int codigo, String contenido){
    this.codigo    = codigo;
    this.contenido = contenido.getBytes();
    this.ruta = "/string";
    esDeChat = false;
  }
  
  // Constructor de mensajes a un solo contacto
  public Mensaje(String usuario, String contenido){
    this.codigo = 1001;
    this.usuario = usuario;
    this.date = new Date();
    this.ruta = "/string";
    this.contenido = contenido.getBytes();
    esDeChat = true;
  }
    
  // Constructor de mensajes en grupo
  public Mensaje(String usuario, String contenido, String grupo) {
    this.codigo = 1002;
    this.usuario = usuario;
    this.date = new Date();
    this.ruta = "/string";
    this.contenido = contenido.getBytes();
    this.grupo = grupo;
    esDeChat = true;
  }

  // Envío de ficheros
  // TODO: Cómo hacer la versión para grupos?
  public Mensaje(String usuario, byte[] contenido, String ruta){
    this.codigo = 1004;
    this.usuario = usuario;
    this.date = new Date();
    this.ruta = ruta;
    this.contenido = contenido;
    this.grupo = grupo;
    esDeChat = false;   // No interesa guardarlo
  }

  // Pasa a String
  public String toString() {
    return (date == null ? "" : "[" + ft.format(date) + "] ")
         + (grupo == null ? "" : "(" + grupo + ") ")
         + (esDeChat ? " " + usuario + ": " : "")
         + (ruta.equals("/string") ? getContenido()
                : " " + usuario + " te ha mandado el fichero " + ruta);
  }

  // Establece el usuario
  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }

  // Obtiene el código del mensaje
  public int getCodigo() {
    return codigo;
  }

  // Obtiene el contenido en forma de string
  public String getContenido() {
    if (ruta.equals("/string"))
      return new String(contenido, 0, contenido.length);

    return "*Fichero: " + ruta + "*";
  }

  public byte[] getRawContenido() {
    return contenido;
  }

  public String getRuta() {
    return ruta;
  }

  // Indica si es un mensaje dirigido a un grupo
  public boolean esDeGrupo() {
    return grupo != null;
  }

  // Obtiene el grupo al que va dirigido
  public String getGrupo(){
    return grupo;
  }

  // Obtiene el usuario
  public String getUsuario(){
    return usuario;
  }

  // Obtiene la conversación (grupo/usuario)
  public String getConversacion(){
    if(grupo != null)
      return grupo;

    return usuario;
  }
}
