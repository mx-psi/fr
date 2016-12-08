---
title: HTTP Live Streaming
author: Pablo Baeyens \and José Manuel Muñoz
header-includes:
  - \usepackage[spanish]{babel}
  - \selectlanguage{spanish}
  - \newcommand{\HLS}{\textit{HTTP Live Streaming }}
toc: true                  # Índice
numbersections: true       # Numeración de secciones
fontsize: 12pt              # Tamaño de fuente
geometry: margin=1.5in        # Tamaño de los márgenes
---

\newpage

# Qué es HLS

HLS (del inglés *HTTP Live Streaming*) es un protocolo de transmisión de contenido multimedia para streaming y vídeo bajo demanda (*VOD*). Sus principales características como protocolo de transmisión de vídeo son:

- Se distribuye **a través de HTTP** o HTTPS, es decir, utiliza el protocolo estándar en la web y permite ser transmitido a través de un servidor web estándar.
- **Segmenta** los vídeos en fragmentos más pequeños que pueden ser descargados independientemente e intercambiados, que pueden tener diferentes calidades y *bit rates* de tal manera que se transmita el fragmento más adecuado según la calidad de la conexión en cada momento.

Además, este protocolo nos permite utilizar HTTPS y por tanto encriptar el contenido que se retransmite. Es un protocolo desarrollado por Apple.

## Dispositivos que pueden utilizarlo

Al ser \HLS un protocolo diseñado por Apple los dispositivos de la misma (aquellos con iOS en una versión superior a iOS 3.0 y Apple TV) soportan por defecto este protocolo, así como su navegador Safari. Además, algunos dispositivos con Android soportan también este protocolo.

Sin embargo, los navegadores web más utilizados como Chrome, Firefox u Opera no soportan este protocolo de forma nativa, por lo que deben transformarse los segmentos descargados, usualmente utilizando JavaScript. Este procedimiento se describe en la sección *Utilización en dispositivos que no sean Apple*.

Otro software que permiten la reproducción de vídeo o streaming en este formato son iTunes desde su versión 10.1 y VLC desde su versión 2. Una lista no exhaustiva de productos que permiten parcial o totalmente la reproducción de vídeo transmitido mediante \HLS puede consultarse [aquí](https://en.wikipedia.org/wiki/HTTP_Live_Streaming#Clients).

\newpage

## Visión general de la arquitectura

![Diagrama que muestra la arquitectura necesaria para la creación y transmisión de vídeo mediante HTTP Live Streaming. De Apple](img/visionGeneral.png)

La arquitectura de un streaming o vídeo bajo demanda transmitido utilizando el protocolo \HLS se compone de las siguientes partes:

- En primer lugar un **codificador** de archivos multimedia convierte la(s) entrada(s) de audio o vídeo a los formatos aceptados. El estándar tal y como viene definido en el *Request for Comment* no indica la necesidad de utilizar un formato concreto; en la práctica se utiliza MPEG-2.<!--TODO: Cuáles son/con qué características?-->
- A continuación un **segmentador de flujo** divide la entrada de audio o vídeo en segmentos cortos (de unos 10 segundos de duración usualmente)
- Para la distribución el servidor web genera un **archivo de índice** que incluye los segmentos mencionados anteriormente así como información sobre estos: su calidad, *bitrate* y otros metadatos que necesite el cliente para reproducir el contenido.
- Este archivo así como los segmentos se transmiten mediante un **servidor HTTP** usual, recargando el archivo índice cuando se generen nuevos segmentos
- El cliente **reconstruye el vídeo** a partir de los segmentos descargados de tal forma que la reproducción sea fluida.

Este esquema general puede complicarse utilizando otras características del protocolo como la transmisión de vídeo en distintos formatos o calidades para conexiones más lentas o como respaldo, el añadido de subtítulos o metadatos adicionales y la encriptación del contenido enviado.

# Funcionamiento del protocolo

En esta sección explicamos el funcionamiento de las distintas partes del protocolo detalladas en la visión general de la arquitectura.

## Codificación y segmentación

El vídeo se codifica en un formato que pueda ser reproducido por los posibles clientes y se segmenta en archivos `ts` junto con un archivo de índice `m3u8`. Esto puede efectuarse de una sola vez sobre un archivo de vídeo o audio preparado, o sobre la marcha en caso de una retransmisión en directo, en cuyo caso cada vez que un segmento es completado se actualiza el índice. El cliente podrá usar reconstruir el archivo o el flujo a partir de los segmentos.

El protocolo soporta posibles discontinuidades en las propiedades del contenido multimedia, como la codificación o las dimensiones. En estos casos se termina un segmento en la discontinuidad y en el índice se indica que el siguiente segmento, que comienza tras la discontinuidad, tiene una configuración distinta.

![Diagrama que muestra el cambio a través del tiempo entre segmentos de distintas calidades en función de la calidad de la conexión. Fallback sólo tiene audio con una imagen estática. De encoding.com](img/timestamp.png)

## Archivos de índice

Una vez generados los segmentos de vídeo se genera a su vez uno o varios archivos de índice. Estos archivos indican la duración, calidad y orden en el que aparecen los distintos segmentos de tal manera que el cliente pueda solicitarlos ordenadamente al servidor.

Los archivos de índice se organizan a través de un **archivo de índice maestro** que indica la localización del resto.

### Formato y sintaxis

El formato utilizado es `m3u8`, una extensión del formato `m3u`. `m3u` es un estándar *de facto* para la creación de listas de reproducción, bien de archivos disponibles en el cliente o bien de flujos de audio o vídeo transmitidos por Internet.

Los archivos `m3u` y `m3u8` consisten en ficheros de texto plano. La diferencia entre ambos radica en que `m3u` utiliza la codificación por defecto del sistema mientras que `m3u8` utiliza UTF-8 para su codificación. De esta forma `m3u8` permite la utilización de caracteres en una gran cantidad de alfabetos y lenguajes. Los archivos índice que no utilicen UTF-8 deben ser rechazados por el cliente según el estándar.

Los archivos índice sólo pueden tener líneas en blanco, URIs (usualmente URLs) o líneas que empiecen por `#`. Estas últimas son las líneas que añaden información sobre las URLs o el índice en cuestión.

La estructura de todo archivo índice debe ser:

 - `#EXTM3U`: la primera línea debe ser la etiqueta que determina que el formato es `m3u8`.
 - Un subconjunto de la siguiente lista de etiquetas:
   - `EXT-X-PLAYLIST-TYPE:tipo`, donde `tipo` es `VOD` (Vídeo bajo demanda, en cuyo caso el índice no debe cambiar) o `EVENT` (usado en retransmisiones que permitan al usuario volver atrás, en cuyo caso el índice solo puede ser cambiado añadiendo fragmentos al final del mismo). Para una retransmisión en vivo que no garantice el almacenamiento de segmentos antiguos, puede no usarse esta etiqueta.
   - `EXT-X-VERSION:N`, con `N` la versión del protocolo usada. Esta etiqueta permite retrocompatibilidad. Si no se usa, se supondrá que se usa la primera versión del protocolo.
   - `EXT-X-TARGETDURATION:N`, siendo `N` la máxima duración que puede tener un segmento.
   - `EXT-X-MEDIA-SEQUENCE:N`, con `N` el número de secuencia que se corresponde con el primer segmento. Puede no ser `0` si se trata de una retransmisión en vivo que no almacena los segmentos antiguos, en cuyo caso cada vez que se elimine el primer segmento este número debe incrementarse en 1.
 - Una lista con un elemento de la siguiente forma para cada segmento:
   - `#EXTINF:N`, siendo `N` un número en punto flotante que indica la duración del segmento. Típicamente es `10.0` para todos los segmentos salvo el último.
   - Una URL que apunte al segmento. Puede ser relativa o absoluta.

   Si dos de estos elementos usan segmentos con distinta codificación, puede informarse de ello al cliente mediante una línea la etiqueta `#EXT-X-DISCONTINUITY` entre ambos.
 - Opcionalmente puede terminar por la etiqueta `#EXT-X-ENDLIST`. La presencia de esta etiqueta indica que no se añadirán más fragmentos al índice.

Un ejemplo de un archivo índice para un vídeo bajo demanda con 4 segmentos de 10 segundos de duración es:

\vspace*{1cm}

```
#EXTM3U
#EXT-X-PLAYLIST-TYPE:VOD
#EXT-X-TARGETDURATION:10
#EXT-X-VERSION:3
#EXT-X-MEDIA-SEQUENCE:0
#EXTINF:10.0,
http://example.com/movie1/fileSequenceA.ts
#EXTINF:10.0,
http://example.com/movie1/fileSequenceB.ts
#EXTINF:10.0,
http://example.com/movie1/fileSequenceC.ts
#EXTINF:9.0,
http://example.com/movie1/fileSequenceD.ts
#EXT-X-ENDLIST
```

\vspace*{1cm}

Si las rutas estuvieran expresadas de forma relativa (tal y como recomienda el estándar) deberían indicarse simplemente el nombre de cada fichero `.ts`.

### Archivo índice maestro

![Diagrama mostrando la estructura de un vídeo con un archivo índice maestro y 3 archivos índices con calidad para red móvil, wifi y Apple TV. De Apple](img/indiceMaestro.png)

Para crear estructuras más complejas puede utilizarse una **lista de reproducción maestra**. Este archivo índice maestro (guardado también en `.m3u8`) proporciona una lista de flujos que los clientes pueden utilizar en función de sus preferencias. Algunas de las características posibles especificadas por el estándar e implementadas en el software de Apple son:

- Proporcionar vídeo en distintas resoluciones, *bit rates* y formatos
- Proporcionar flujos de respaldo en distintos servidores para evitar fallos
- Proporciona audio en distintos idiomas o con distintos vídeos (distintos ángulos de cámara etc.)

El cliente es responsable de cambiar entre estos flujos en función de sus preferencias y la calidad de su conexión. En el caso de la calidad, el primer flujo descrito en el archivo índice maestro será el que se utilice para realizar una prueba de la calidad de la conexión. La relación de aspecto de los distintos flujos ofrecidos debe ser la misma (4:3, 16:9), aunque la resolución puede variar. Esta se indica en el campo `RESOLUTION` de `EXT-X-STREAM-INF` para ayudar al cliente a decidir.

Los flujos de respaldo se utilizan en el caso de que alguno de los flujos dé un fallo 404 o cualquier otro error. En caso de error el cliente escoge el siguiente flujo con más ancho de banda, escogiendo el que se encuentre antes en la lista si hay empate.

Un ejemplo de un archivo índice maestro con dos flujos con distinta calidad y flujos de respaldo (extraído de la documentación de Apple) es:

\vspace*{1cm}

```
#EXTM3U
#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=200000, RESOLUTION=720x480
http://ALPHA.mycompany.com/lo/prog_index.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=200000, RESOLUTION=720x480
http://BETA.mycompany.com/lo/prog_index.m3u8

#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=500000, RESOLUTION=1920x1080
http://ALPHA.mycompany.com/md/prog_index.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=500000, RESOLUTION=1920x1080
http://BETA.mycompany.com/md/prog_index.m3u8
```

\vspace*{1cm}

La primera línea identifica el archivo como un archivo índice. A continuación se listan las URLs de los archivos índice de los distintos flujos. Los primeros flujos indican una resolución baja mientras que los segundos tienen una resolución 1080p. El servidor `BETA` sirve de respaldo al servidor `ALPHA`. El estándar no limita el posible número de flujos de respaldo.

Aunque no es obligatorio, el estándar recomienda que el audio sea el mismo entre los distintos flujos aunque la calidad de la imagen varíe. Esto permite la transición entre estos sin *glitches* auditivos.

### Archivos índice durante la retransmisión en directo

Durante la retransmisión de un *streaming* se utiliza una única lista de reproducción que se va actualizando. Un ejemplo de un archivo índice en un cierto momento del *streaming* es:

\vspace*{1cm}

```
#EXTM3U
#EXT-X-TARGETDURATION:10
#EXT-X-VERSION:3
#EXT-X-MEDIA-SEQUENCE:2
#EXTINF:10,
fileSequence2.ts
#EXTINF:10,
fileSequence3.ts
#EXTINF:10,
fileSequence4.ts
#EXTINF:10,
fileSequence5.ts
#EXTINF:10,
fileSequence6.ts
```
\vspace*{1cm}

Como podemos ver `EXT-X-MEDIA-SEQUENCE` indica que el primer segmento disponible es el tercero ya que el *streaming* ha avanzado. Además observamos que no existe la etiqueta `EXT-X-ENDLIST` para indicar que esta lista se actualizará conforme avance el *streaming*. No se indica la etiqueta `EXT-X-PLAYLIST-TYPE` por lo que se asume *streaming*.

## Configuración del servidor y transmisión
## Tipos de sesión
## Encriptación
## Alternativas de Streaming
## Añadir metadatos adicionales

# Cómo se relaciona con el resto de elementos HTML
## El elemento `video`

## Utilización en dispositivos que no sean Apple

\HLS tiene soporte por defecto en dispositivos Apple, en Safari y en las versiones para iOS y Android del navegador Google Chrome. Para el resto de dispositivos es necesario adaptar el protocolo para asegurar la reproducción utilizando JavaScript.

La biblioteca más común es `hls.js`, desarrollada por Dailymotion. Esta librería convierte los segmentos de vídeo recibidos para utilizar MediaSource, una extensión de HTML5 que permite la transmisión de audio y vídeo. Las siguientes subsecciones describen brevemente el funcionamiento de esta librería.

<!--
TODO:
- Cómo funciona MediaSource
- Cómo funciona hls.js
-->
- [Media Source Extensions™](https://www.w3.org/TR/media-source/)
- [Introducing hls.js](http://engineering.dailymotion.com/introducing-hls-js/)
- [dailymotion/hls.js: MSE-based HLS client - http://dailymotion.github.io/hls.js/demo](https://github.com/dailymotion/hls.js)
- [Adaptive Streaming with HLS in HTML5](https://www.jwplayer.com/blog/hls-in-html5/)
- [MediaSource - Web API reference](https://developer.mozilla.org/es/docs/Web/API/MediaSource)


\input{bibliografia}
