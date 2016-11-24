import java.util.ArrayList;

public class Grupo {
  private String groupName;
  private ArrayList<Cliente> clientes;
  private static final int maxClientes = 512;

	public Grupo(String name, Cliente creador) {
    groupName = name;
		clientes = new ArrayList();
    addMember(creador);
	}

  public String getName() {
    return groupName;
  }

  public void sendMessage(String mensaje) {
    for (Cliente c:clientes)
      c.sendMessage("1005" + getName() + ";" + c.getClientName() + ";" + mensaje);
  }

  // Devuelve true en caso de éxito, false si ya estaba
  public boolean addMember(Cliente nuevo, Cliente solicitante) {
    if (clientes.size() == maxClientes) {
      solicitante.sendMessage("2006" + getName());
      return false;
    }

    if (clientes.contains(nuevo)) {
      solicitante.sendMessage("2005" + getName() + ";" + nuevo.getClientName());
      return false;
    }

    clientes.add(nuevo);
    // TODO: mandar mensaje TCP conveniente a los clientes del grupo y al añadido
    return true;
  }

  public boolean addMember(Cliente c) {
    return addMember(c, c);
  }

  public boolean removeMember(Cliente a_borrar) {
    if (!clientes.remove(a_borrar))
      return false; // No estaba

    // TODO: mandar mensaje TCP conveniente a los clientes del grupo y al expulsado
    // TODO: disolver grupo si no quedan miembros (podría hacerse fuera del método)
    return true;
  }
}
