import java.util.ArrayList;

/*
  Gestiona la información y envío de mensajes en un grupo
*/
public class Grupo {
  private String groupName;
  private ArrayList<Cliente> clientes;
  private static final int maxClientes = 512;

	public Grupo(String name, Cliente creador) {
    groupName = name;
		clientes = new ArrayList<Cliente>();
    addMember(creador);
	}
  
  // Obtiene el nombre del grupo
  public String getName() {
    return groupName;
  }
  
  // Envía mensaje a todos los clientes del grupo
  public void sendMessage(Mensaje mensaje) {
    for (Cliente c:clientes)
      c.sendMessage(mensaje);
  }

  // Devuelve true en caso de éxito, false si ya estaba
  public boolean addMember(Cliente nuevo, Cliente solicitante) {
    if (clientes.size() == maxClientes) {
      solicitante.sendMessage(new Mensaje(2006,getName()));
      return false;
    }

    if (clientes.contains(nuevo)) {
      solicitante.sendMessage(new Mensaje(2005, getName() + ";" + nuevo.getClientName()));
      return false;
    }

    clientes.add(nuevo);
    Mensaje m = new Mensage(1996, "");
    m.setUsuario(nuevo.getName());
    m.setGrupo(groupName);
    sendMessage(m);
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
