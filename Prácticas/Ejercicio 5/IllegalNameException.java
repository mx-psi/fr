
/*
  Excepción que ocurre cuando un nombre no cumple las condiciones
*/
public class IllegalNameException extends Exception {
  String nombre;

  public IllegalNameException(String nombre) {
    this.nombre = nombre;
  }

  public String what() {
    return nombre;
  }
}
