---
title: Ejercicio 5 - Protocolo de aplicación
author: Pablo Baeyens \and José Manuel Muñoz
date: Fundamentos de Redes
---

# Descripción de la aplicación, funcionalidad y actores que intervienen

Nuestro protocolo de aplicación consiste en la implementación de un **servicio de mensajería instantánea en tiempo real** con una arquitectura cliente-servidor. De esta forma, los actores que intervienen son:

- **Usuarios** (clientes): Los usuarios mandan mensajes a otros usuarios a través del servidor, tanto a usuarios individuales como a grupos de usuarios que definen previamente
- **Servidor**: El servidor recibe los mensajes de los clientes y gestiona su envío a su destinatario o destinatarios, incluyendo la información necesaria para su correcta decodificación

<!--TODO: Los usuarios se loguean en el servicio?-->

Para dar soporte a este servicio el protocolo implementado consta de 5 tipos de mensajes: mensajes enviados de los usuarios al servidor, petición de creación de grupos y de reserva de nombres de usuario, mensajes enviados del servidor a los usuarios y mensajes de error (cuando no existe un grupo o usuario o no ha sido posible el envío de un mensaje).

# Diagrama de estados del servidor

<!-- TODO: Hacer el diagrama como con los autómatas?-->

# Mensajes que intervienen

## Cliente

| **Código** | **Cuerpo** | **Descripción**|
|------------|------------|----------------|
| 1000       | *username* | El cliente solicita que su nombre sea *username*. |
| 1001       | *username* + ; + *time* + ; + *contenido* | Solicita el envío de *contenido* al **usuario** de nombre *username* |
| 1002       | *groupname* + ; + *time* + ; + *contenido* | Solicita el envío de *contenido* al **grupo** de nombre *groupname* |
| 1003       | *groupname* + ; + *username*| El cliente solicita que se añada *username* a *groupname*. Si el grupo no existe se creará. |
| 1004       | *usuario* + ; + *fichero*| El cliente solicita que se añada *username* a *groupname*. Si el grupo no existe se creará. |
| 1999       | *bye* | El cliente solicita su desconexión |

## Servidor

| **Código** | **Cuerpo** | **Descripción** |
|------------|------------|-----------------|
| 2001 | *username* | El usuario *username* no existe |
| 2002 | *groupname* | El grupo *groupname* no existe |
| 2003 | *tipo* | El tipo *tipo* no se ha reconocido como un tipo de mensaje válido |
| 2004 | *ERROR* | El último mensaje enviado estaba mal formado |
| 2005 | *groupname* | *username* | El usuario *username* ya estaba en el grupo *groupname* |
| 2006 | *groupname* | El grupo *groupname* está lleno |
| 2007 | *username* | El nombre *username* es inválido |
| 2008 | *username* | El nombre *username* está siendo usado |
| 1000 | *username* | Petición de nombre de usuario aceptada |
| 1001 | *username* + ; + *time* + ; + *mensaje* | El usuario *username* ha enviado en el tiempo *time* el mensaje *mensaje* |
| 1002 | *groupname* + ; + *username* + ; + *time* + ; + *mensaje* | El usuario *username* ha enviado en el grupo *groupname* en el tiempo *time* el mensaje *mensaje* |
| 1996 | *username* + ; + *groupname* | Indica la entrada de *username* al grupo *groupname* |
| 1997 | *username* | Indica a un cliente la conexión de otro cliente *username* |
| 1998 | *username* | Indica a un cliente la desconexión de otro cliente *username* |


# Evaluación de la aplicación
