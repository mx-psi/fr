import java.util.LinkedHashSet;

/*
  Gestiona la información y envío de mensajes en un grupo
*/
public class Grupo {
  private String groupName;
  private LinkedHashSet<Cliente> clientes;
  private static final int maxClientes = 512;
  private boolean permanente;

	public Grupo(String name, Cliente creador) {
    groupName = name;
		clientes = new LinkedHashSet<Cliente>();
    permanente = creador == null;
    ServidorChat.sendToAllClients(new Mensaje(1995, "").setGrupo(name));
    if (!permanente)
      addMember(creador);
	}

  // Obtiene el nombre del grupo
  public String getName() {
    return groupName;
  }

  // Determina si un grupo es permanente
  public boolean esPermanente() {
    return permanente;
  }

  // Determina si un grupo está vacío
  public boolean isEmpty() {
    return clientes.isEmpty();
  }

  // Envía mensaje a todos los clientes del grupo
  public void sendMessage(Mensaje mensaje) {
    for (Cliente c:clientes)
      c.sendMessage(mensaje);
  }

  // Envía la lista de miembros del grupo
  public void sendMembers(Cliente usuario) {
    for (Cliente c:clientes)
      usuario.sendMessage(new Mensaje(1996, "").setGrupo(groupName).setUsuario(c.getClientName()));
  }

  // Devuelve true en caso de éxito, false si ya estaba
  public boolean addMember(Cliente nuevo, Cliente solicitante) {
    if (clientes.size() == maxClientes) {
      solicitante.sendMessage(new Mensaje(2006,getName()));
      return false;
    }

    if (!clientes.add(nuevo)) {
      solicitante.sendMessage(new Mensaje(2005, "").setUsuario(nuevo.getClientName()).setGrupo(getName()));
      return false;
    }

    Mensaje m = new Mensaje(1996, "");
    m.setUsuario(nuevo.getClientName());
    m.setGrupo(groupName);
    sendMessage(m);
    return true;
  }

  public boolean addMember(Cliente c) {
    return addMember(c, c);
  }

  // Elimina del grupo a un cliente que se haya desconectado
  public boolean removeMember(Cliente a_borrar) {
    if (!clientes.remove(a_borrar))
      return false; // No estaba

    if (!permanente && isEmpty())
      ServidorChat.removeGroup(groupName);

    return true;
  }
}
