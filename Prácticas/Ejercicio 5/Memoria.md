---
title: Ejercicio 5 - Protocolo de aplicación
author: Pablo Baeyens \and José Manuel Muñoz
date: Fundamentos de Redes
---

# Descripción de la aplicación, funcionalidad y actores que intervienen

Nuestro protocolo de aplicación consiste en la implementación de un **servicio de mensajería instantánea en tiempo real** con una arquitectura cliente-servidor. De esta forma, los actores que intervienen son:

- **Usuarios** (clientes): Los usuarios mandan mensajes a otros usuarios a través del servidor, tanto a usuarios individuales como a grupos de usuarios que definen previamente
- **Servidor**: El servidor recibe los mensajes de los clientes y gestiona su envío a su destinatario o destinatarios, incluyendo la información necesaria para su correcta decodificación

Para dar soporte a este servicio el protocolo implementado consta de 4 tipos de mensajes: mensajes de texto usuales, petición de creación de grupos y de reserva de nombres de usuario y mensajes de error (cuando no existe un grupo o usuario o no ha sido posible el envío de un mensaje).

\newpage

# Diagrama de estados del servidor

<!-- TODO: Hacer el diagrama como con los autómatas?-->

\newpage

# Mensajes que intervienen

## Cliente

| **Código** | **Cuerpo** | **Descripción**|
|------------|-----------------------|--------------------------------|
| 1000       | *user* | El cliente solicita el nombre *user* |
| 1001       | *user*, *time*, *contenido* | Envío de *contenido* a *user* |
| 1002       | *group*, *time*, *contenido* | Envío de *contenido* a *group* |
| 1003       | *group*, *user* | Solicita que se añada *user* a *group*. Si el grupo no existe se creará. |
| 1004       | *user*  *fichero* | Solicita el envío de *fichero* a *user* |
| 1996       | *group* | Solicita la lista de usuarios del grupo *groupname* |
| 1999       | bye | El cliente solicita su desconexión |

## Servidor

| **Código** | **Cuerpo** | **Descripción** |
|------------|------------------------|---------------------------------|
| 2001 | *user* | El usuario *user* no existe |
| 2002 | *group* | El grupo *group* no existe |
| 2004 | *ERROR* | El último mensaje enviado estaba mal formado |
| 2005 | *group* | *user* | El usuario *user* ya estaba en el grupo *group* |
| 2006 | *group* | El grupo *group* está lleno |
| 2007 | *user* | El nombre *user* es inválido |
| 2008 | *user* | El nombre *user* está siendo usado |
| 1000 | *user* | Petición de nombre de usuario aceptada |
| 1001 | *user*, *time*, *mensaje* | *user* ha enviado en el tiempo *time* el mensaje *mensaje* |
| 1002 | *group*, *user*, *time*, *mensaje* | *user* ha enviado en *group* en el tiempo *time* el mensaje *mensaje* |
| 1994 | *end* | Indica el fin de la recepción de información de login |
| 1995 | *group* | Indica la existencia o la creación del grupo *group* |
| 1996 | *user*, *group* | *user* ha entrado al grupo *group* |
| 1997 | *user* | *user* se ha conectado |
| 1998 | *user* | *user* se ha desconectado |

\newpage

# Evaluación de la aplicación

## Conexión y comandos

En primer lugar los usuarios deben conectarse al servicio, proporcionando la dirección del servidor, un nombre de usuario válido y su contraseña. A continuación pueden utilizar los siguientes comandos para navegar:

- `/close`, `/exit`, `/quit`, `/q`, `/salir`: Cierra la conexión
- `conversacion`, `c`: Cambia la conversación a la indicada

La conexión se muestra así:

<!-- Imagen del proceso de conexión completo y entrar en un chat-->

## Grupos

El sistema permite la creación de grupos. Los comandos adecuados son:

<!--Lista de comandos-->

Los grupos se muestran así:

<!-- Imagen de creación y añadido en un grupo con usuarios conectándose y desconectándose-->

## Envío de ficheros

El envío de ficheros se realiza con el comando `/send`. Los usuarios receptores deben disponer de una carpeta *Recibidos* que no tenga un archivo con el mismo nombre del enviado. El envío se muestra así en cada usuario:

<!--Dos imágenes que muestren el envío con send y la recepción-->
