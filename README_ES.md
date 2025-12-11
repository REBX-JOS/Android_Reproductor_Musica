# Reproductor de Música para Android

Una aplicación moderna de reproducción de música para Android que escanea y organiza automáticamente tu biblioteca musical.

[English Version](README.md)

## Características

- 🎵 Escaneo automático de biblioteca musical
- 📁 Escanea todos los archivos de audio en tu dispositivo
- ⭐ Gestión de favoritos
- 📋 Creación y gestión de listas de reproducción
- 🔍 Funcionalidad de búsqueda
- 🎨 Visualización de carátulas de álbumes
- 🔄 Modos de repetición y aleatorio
- 📱 Reproducción en segundo plano con notificaciones

## Cómo Agregar Música

### Método 1: Copiar Archivos de Música a tu Dispositivo

La aplicación escanea automáticamente tu almacenamiento en busca de archivos de audio. Para agregar música:

1. **Conecta tu dispositivo a una computadora** mediante USB
2. **Copia tus archivos de música** (MP3, AAC, FLAC, OGG, WAV, etc.) a cualquier carpeta de tu dispositivo:
   - Carpeta `Music/` (recomendado)
   - Carpeta `Download/`
   - Cualquier carpeta personalizada como `MiMusica/` o `Canciones/`
3. **Abre la aplicación** y detectará automáticamente los nuevos archivos
4. Si las canciones no aparecen, **desliza hacia abajo** en la pantalla de biblioteca para actualizar manualmente

### Método 2: Descargar Música Directamente en el Dispositivo

1. Descarga archivos de música usando el navegador de tu dispositivo o cualquier aplicación de gestor de archivos
2. Guarda los archivos en el almacenamiento de tu dispositivo (cualquier carpeta)
3. Abre la aplicación de reproductor de música
4. **Desliza hacia abajo para actualizar** la biblioteca si es necesario

### Método 3: Transferir vía Bluetooth/WiFi

1. Recibe archivos de música vía Bluetooth desde otro dispositivo, o
2. Usa aplicaciones de transferencia WiFi (como Files de Google, ShareIt, etc.)
3. Guarda los archivos en el almacenamiento de tu dispositivo
4. Abre la aplicación y desliza hacia abajo para actualizar

## Formatos de Audio Soportados

La aplicación soporta todos los formatos que Android MediaStore reconoce:
- MP3
- AAC / M4A
- FLAC
- OGG / OGG Vorbis
- WAV
- WMA
- Y más...

## Uso de la Aplicación

### Configuración Inicial

1. **Otorgar Permisos**: En el primer inicio, la aplicación solicitará permiso para acceder al almacenamiento de tu dispositivo. Haz clic en "Permitir" para que la aplicación pueda escanear archivos de música.
2. **Esperar el Escaneo**: La aplicación escaneará automáticamente tu dispositivo en busca de archivos de música. Esto puede tomar unos momentos dependiendo de cuántos archivos tengas.
3. **Explorar tu Biblioteca**: Una vez completado el escaneo, verás todas tus canciones en la pestaña Biblioteca.

### Actualización Manual de la Biblioteca

Si agregas nuevos archivos de música y no aparecen automáticamente:

1. Ve a la pestaña **Biblioteca**
2. **Desliza hacia abajo** en la pantalla para actualizar
3. La aplicación volverá a escanear el almacenamiento de tu dispositivo
4. Las nuevas canciones aparecerán en tu biblioteca

### Organizando tu Música

- **Favoritos**: Toca el ícono de corazón en cualquier canción para agregarla a tus favoritos
- **Listas de Reproducción**: Crea listas personalizadas yendo a la pestaña Listas de reproducción
- **Pestañas**: Cambia entre Todas las canciones, Favoritos y Recientes usando las pestañas en la parte superior
- **Búsqueda**: Usa el ícono de búsqueda para encontrar canciones, artistas o álbumes específicos

### Controles de Reproducción

- Toca cualquier canción para comenzar a reproducir
- Usa los controles de notificación para reproducir/pausar sin abrir la aplicación
- Accede a los controles completos del reproductor tocando el mini reproductor en la parte inferior
- Activa los modos aleatorio o repetición desde la pantalla del reproductor

## Solución de Problemas

### Las Canciones No Aparecen

1. **Verificar Permisos**: Asegúrate de haber otorgado el permiso de almacenamiento
   - Ve a Ajustes → Aplicaciones → Reproductor de Música → Permisos → Almacenamiento → Permitir
2. **Verificar Formato de Archivo**: Asegúrate de que tus archivos de música estén en un formato soportado
3. **Verificar Ubicación del Archivo**: Los archivos deben estar en almacenamiento accesible (no en carpetas ocultas o del sistema)
4. **Actualización Manual**: Desliza hacia abajo en la pestaña Biblioteca para activar un nuevo escaneo

### Permiso No Otorgado

Si denegaste el permiso de almacenamiento:
1. Ve a los **Ajustes** de tu dispositivo
2. Navega a **Aplicaciones** → **Reproductor de Música** → **Permisos**
3. Activa el permiso de **Almacenamiento** o **Medios**
4. Vuelve a abrir la aplicación y actualiza la biblioteca

### Archivos de Música No Detectados

- Asegúrate de que los archivos sean realmente archivos de audio (no renombrados o corruptos)
- Verifica si los archivos están en una carpeta accesible para Android MediaStore
- Evita almacenar música en carpetas del sistema o ocultas (carpetas que comienzan con `.`)
- Intenta mover los archivos a la carpeta estándar `Music/`

## Requisitos

- Android 7.0 (API 24) o superior
- Permiso de almacenamiento para leer archivos de audio
- Permiso de notificaciones para controles de reproducción (Android 13+)

## Privacidad

Esta aplicación:
- Solo accede a archivos de audio en tu dispositivo
- No recopila ni transmite ningún dato personal
- No requiere conexión a internet (excepto para descargar música)
- Almacena todos los datos localmente en tu dispositivo

## Detalles Técnicos

La aplicación utiliza:
- **API MediaStore** para descubrimiento rápido de archivos de música
- **Base de datos Room** para almacenamiento local de datos
- **ExoPlayer (Media3)** para reproducción de audio
- **Lectura de etiquetas ID3** para extracción detallada de metadatos

---

Hecho con ❤️ para los amantes de la música
