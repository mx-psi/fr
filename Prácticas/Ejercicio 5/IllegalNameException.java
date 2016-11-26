
/*
  Excepción que ocurre cuando un nombre no cumple las condiciones
*/
public class IllegalNameException extends Exception {
  private String nombre;
  private boolean repetido; // true si el motivo del error es que el nombre estaba en uso
  private boolean codigo_erroneo;

  public IllegalNameException(String nombre, boolean repetido) {
    this.nombre = nombre;
    this.repetido = repetido;
    this.codigo_erroneo = false;
  }

  public IllegalNameException() {
    this.codigo_erroneo = true;
  }

  public String what() {
    if (codigo_erroneo)
      return "código erróneo";

    if (repetido)
      return "nombre repetido: " + nombre;

    return "nombre = " + nombre;
  }
}
