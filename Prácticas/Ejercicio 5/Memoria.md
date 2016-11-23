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

Para dar soporte a este servicio el protocolo implementado consta de 4 tipos de mensajes: mensajes enviados de los usuarios al servidor, petición de creación de grupos, mensajes enviados del servidor a los usuarios y mensajes de error (cuando no existe un grupo o usuario o no ha sido posible el envío de un mensaje).

# Diagrama de estados del servidor

<!-- TODO: Hacer el diagrama como con los autómatas?-->

# Mensajes que intervienen

## Cliente

| **Código** | **Cuerpo** | **Descripción**|
|------------|------------|----------------|
| 1002       | **SEND** + *id* + tipo + *contenido* | Solicita el envío de *contenido* al usuario o grupo con identificador *id*|
| 1003       | **ADD** + *groupname* + **USER** + *usuario*| El cliente solicita que se añada *usuario* a *groupname*. Si el grupo no existe se creará.|

## Servidor

| **Código** | **Cuerpo** | **Descripción** |
|------------|------------|-----------------|
| 2001 | **ERROR** + *id* | El usuario *id* no existe o no se encuentra conectado en este momento |
| 2002 | **ERROR** + *id* | El grupo *id* no existe |
| 2003 | **ERROR** + *tipo* | El tipo *tipo* no se ha reconocido como un tipo de mensaje válido |
| 1004 | **MESSAGE** + *id* + (**AT** + *group*) + **TIME** + *time* + *mensaje* | El usuario *id* ha enviado (en el grupo *group*) en el tiempo *time* el mensaje *mensaje* |

# Evaluación de la aplicación
