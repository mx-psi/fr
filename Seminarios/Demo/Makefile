HLSFLAGS = -hls_time 10 -hls_flags split_by_time -strict -2
VOD      = -hls_playlist_type vod
EVENT    = -hls_playlist_type event

#Ejemplo de vídeo bajo demanda
vod-%: % clean
	ffmpeg -i $< $(HLSFLAGS) $(VOD) demo.m3u8

#Vídeo bajo demanda en múltiples calidades (nativo, 360p, 144p)
multivod-%: % clean
	ffmpeg -i $< $(HLSFLAGS) $(VOD) -b:v 3200k -bufsize 3200k -vf crop=ih/3*4:ih demo_nativa.m3u8
	ffmpeg -i $< $(HLSFLAGS) $(VOD) -b:v 900k -bufsize 900k -vf 'crop=ih/3*4:ih,scale=-1:360' demo_360p.m3u8
	ffmpeg -i $< $(HLSFLAGS) $(VOD) -b:v 280k -bufsize 280k -vf 'crop=ih/3*4:ih,scale=-1:144' demo_144p.m3u8
	echo #EXTM3U > demo.m3u8
	echo #EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=280000 >> demo.m3u8
	echo demo_144p.m3u8 >> demo.m3u8
	echo #EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=900000 >> demo.m3u8
	echo demo_360p.m3u8 >> demo.m3u8
	echo #EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=3200000 >> demo.m3u8
	echo demo_nativa.m3u8 >> demo.m3u8

#Ejemplo de evento en el que se conservan los segmentos anteriores
eventsim-%: % clean
	ffmpeg -re -i $< $(HLSFLAGS) $(EVENT) demo.m3u8

#Simula un streaming a partir de un vídeo. Se almacenarán los últimos 50 segundos
streamsim-%: % clean
	ffmpeg -re -i $< $(HLSFLAGS) demo.m3u8



clean:
	rm -rf *.ts demo*.m3u8		# Linux
#	del /q *.ts demo*.m3u8		# Windows
