---
title: HTTP Live Streaming
author: Pablo Baeyens \and José Manuel Muñoz
header-includes:
  - \usepackage[spanish]{babel}
  - \selectlanguage{spanish}
  - \newcommand{\HLS}{\textit{HTTP Live Streaming}}
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
- Cómo se le da soporte en dispositivos y software no Apple
-->


## Visión general de la arquitectura

La arquitectura de un streaming o video bajo demanda transmitido utilizando el protocolo \HLS se compone de las siguientes partes:

- En primer lugar un **codificador** de archivos multimedia convierte la(s) entrada(s) de audio o vídeo a los formatos aceptados. El estándar tal y como viene definido en el *Request for Comment* no se indica la necesidad de utilizar un formato concreto; en la práctica se utiliza MEPG-2 <!--TODO: Cuáles son/con qué características?-->
- A continuación un **segmentador de flujo** divide la entrada de audio o vídeo en segmentos cortos (de unos 10 segundos de duración usualmente)
- Para la distribución el servidor web genera un **archivo de índice** que incluye los segmentos mencionados anteriormente así como información sobre estos: su calidad, *bitrate* y otros metadatos que necesite el cliente para reproducir el contenido
- Este archivo así como los segmentos se transmiten mediante un **servidor HTTP** usual, recargando el archivo índice cuando se generen nuevos segmentos
- El cliente **reconstruye el vídeo** a partir de los segmentos descargados de tal forma que la reproducción sea fluida

Este esquema general puede complicarse utilizando otras características del protocolo como la transmisión de vídeo en distintos formatos o calidades para conexiones más lentas o como respaldo, el añadido de subtítulos o metadatos adicionales y la encriptación del contenido enviado.

# Funcionamiento del protocolo

## Codificación y segmentación

## Archivos de índice

Una vez generados los segmentos de vídeo se genera a su vez uno o varios archivos de índice. Estos archivos indican la duración, calidad y orden en el que aparecen los distintos segmentos de tal manera que el cliente pueda solicitarlos ordenadamente al servidor.

Los archivos de índice se organizan a través de un **archivo de índice maestro**, que indica la localización del resto.

### Formato y sintaxis

El formato utilizado es `m3u8`, una extensión del formato `m3u`. `m3u` es un estándar *de facto* para la creación de listas de reproducción, bien de archivos disponibles en el cliente o bien de flujos de audio o vídeo transmitidos por Internet.

Los archivos `m3u` y `m3u8` consiste en ficheros de texto plano. La diferencia entre ambos radica en que mientras `m3u` utiliza la codificación por defecto del sistema `m3u8` utiliza UTF-8 para su codificación. De esta forma se permite la utilización de caracteres en una gran cantidad de alfabetos y lenguajes. Los archivos índices que no utilicen UTF-8 deben ser rechazadas por el cliente según el estándar.

Los archivos índices sólo pueden tener líneas en blanco, URIs (usualmente URLs) o líneas que empiecen or `#`. Estas últimas son las líneas que añaden información sobre las URLs o el índice en cuestión.

La estructura de todo archivo índice debe ser:

<!--TODO: Añadir ejemplo y explicarlo-->
https://developer.apple.com/library/content/technotes/tn2288/_index.html

### Archivo índice maestro


## Configuración del servidor y transmisión
## Tipos de sesión
## Encriptación
## Alternativas de Streaming
## Añadir metadatos adicionales

# Cómo se relaciona con el resto de elementos HTML
## El elemento `video`
## Utilización en dispositivos que no sean Apple

\input{bibliografia}
