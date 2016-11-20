# Mensajes que intervienen

## Cliente

- **SEND** + *username/groupname* + tipo + *contenido*
- **GET** + *username*
- **ADD** + *groupname* + **USER** + *usuario*


## Servidor

- Errores
  - El grupo/usuario no existe
  - El tipo de mensaje no se ha reconocido
- **MESSAGE** + *username* + (**AT** + *groupname*) + **TIME** + *time* + *mensaje*

