import java.util.Date;

public class Mensaje{
  private int codigo;
  private String date;
  private String usuario;
  private String grupo;
  private String contenido;
  
  // Constructor de mensajes de error
  public Mensaje(int codigo, String contenido){
    this.codigo    = codigo;
    this.contenido = contenido;
  }
  
  // Constructor de mensajes a un solo contacto
  public Mensaje(String usuario, String date, String contenido){
    this.usuario = usuario;
    this.date = date;
    this.contenido = contenido;
  }
    
  // Constructor de mensajes en grupo
  public Mensaje(String usuario, String date, String contenido, String grupo){
    this.usuario = usuario;
    this.date = date;
    this.contenido = contenido;
    this.grupo = grupo;
  }
  
  // Imprime el mensaje
  public void print(){
    System.out.println("[" + date + "]" + (grupo == null ? "" : " (" + grupo + ")")
                        + " " + usuario + ": " + contenido);
  }
}
