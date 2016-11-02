---
title: HTTP Live Streaming
author: Pablo Baeyens \and José Manuel Muñoz
header-includes:
  - \usepackage[spanish]{babel}
  - \selectlanguage{spanish}
toc: true                  # Índice
numbersections: true       # Numeración de secciones
fontsize: 12pt              # Tamaño de fuente
geometry: margin=1.5in        # Tamaño de los márgenes
---

# Qué es HLS

HLS (del inglés *HTTP Live Streaming*) es un protocolo de transmisión de contenido multimedia para streaming y video bajo demanda (*VOD*). Sus principales características como protocolo de transmisión de video son:

- Se distribuye **a través de HTTP** o HTTPS: es decir, utiliza el protocolo estándar en la web y permite ser transmitido a través de un servidor web estándar.
- **Segmenta** los vídeos en fragmentos más pequeños que pueden ser descargados independientemente e intercambiados, que pueden tener diferentes calidades y *bit rates* de tal manera que se transmita el fragmento más adecuado según la calidad de la conexiñon en cada momento.

Además, este protocolo nos permite utilizar HTTPS y por tanto encriptar el contenido que se retransmite. Es un protocolo desarrollado por Apple

<!--
- Qué dispositivos lo soportan y cómo
- Qué empresas lo utilizan
-->


## Arquitectura

# Cómo se envía el vídeo
## Codificación y segmentación
## Configuración del servidor
## Tipos de sesión
## Encriptación
## Alternativas de Streaming
## Añadir metadatos adicionales

# Cómo se relaciona con el resto de elementos HTML
## El elemento `video`
## Utilización en dispositivos que no sean Apple

\input{bibliografia}
