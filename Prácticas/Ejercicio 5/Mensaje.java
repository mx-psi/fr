import java.util.Date;
import java.text.SimpleDateFormat;

public class Mensaje implements java.io.Serializable{
  private int codigo;
  private Date date;
  private String usuario;
  private String grupo;
  private String contenido;
  private static SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");
  
  // Constructor de mensajes no usuales
  public Mensaje(int codigo, String contenido){
    this.codigo    = codigo;
    this.contenido = contenido;
  }
  
  // Constructor de mensajes a un solo contacto
  public Mensaje(String usuario, Date date, String contenido){
    this.codigo = 1001;
    this.usuario = usuario;
    this.date = date;
    this.contenido = contenido;
  }
    
  // Constructor de mensajes en grupo
  public Mensaje(String usuario, Date date, String contenido, String grupo){
    this.codigo = 1002;
    this.usuario = usuario;
    this.date = date;
    this.contenido = contenido;
    this.grupo = grupo;
  }
  
  // Pasa a String
  public String toString(){
    return "[" + ft.format(date) + "]" + (grupo == null ? "" : " (" + grupo + ")")
                        + " " + usuario + ": " + contenido;
  }
  
  public void setUsuario(String usuario){
    this.usuario = usuario;
  }
  
  public int getCodigo(){
    return codigo;
  }
  
  public String getContenido(){
    return contenido;
  }
  
  public String getGrupo(){
    return grupo;
  }
  
  public String getUsuario(){
    return usuario;
  }
  
  public String getConversacion(){
    if(grupo != null)
      return grupo;
    else
      return usuario;
  }
}
