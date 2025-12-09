# Práctica JOGL Base - Renderizado 3D en Java

Este repositorio contiene un proyecto de práctica desarrollado en Java utilizando la librería **JOGL** (Java Binding for the OpenGL API) para la materia de Graficación. El objetivo principal es demostrar conceptos fundamentales de computación gráfica como renderizado 3D, mapeo de texturas, iluminación y animación jerárquica.

## 📋 Descripción

El proyecto implementa una escena 3D interactiva que simula un entorno estilo "Minecraft". Incluye:
* Un personaje ("Steve") construido mediante modelado jerárquico (cubos transformados).
* Texturizado de bloques y personajes.
* Un sistema de iluminación interactivo.
* Animaciones básicas (caminata).
* Control de cámara libre.

## 🛠️ Tecnologías

* **Lenguaje:** Java (JDK 8 o superior).
* **Librería Gráfica:** JOGL (JOGAMP) - OpenGL 2.0 (Pipeline de función fija).
* **IDE Recomendado:** NetBeans (el proyecto contiene la carpeta `nbproject`) o IntelliJ IDEA.

## 🚀 Instalación y Configuración

Dado que las librerías JOGL suelen ser archivos `.jar` externos, se necesita configurarlas en un entorno local para compilar el proyecto.

1.  **Clonar el repositorio:**
    ```bash
    git clone <url-de-tu-repositorio>
    ```

2.  **Configurar Librerías JOGL:**
    El proyecto requiere los siguientes archivos `.jar` (asegurarse de tenerlos descargados de [JogAmp.org](https://jogamp.org/)):
    * `gluegen-rt.jar`
    * `jogl-all.jar`
    * Librerías nativas correspondientes a tu sistema operativo.

    *Nota: Si se usa IntelliJ o NetBeans, se deberá ir a la configuración del proyecto ("Libraries" o "Dependencies") y agregar estos .jar manualmente, ya que las rutas originales en el archivo `.iml` apuntan a una ruta local específica (`D:/Documentos/Librerias_JOGL/...`).*

3.  **Ejecución:**
    La clase principal por defecto es `src/JoglBase.java`.

## 🎮 Controles (Clase JoglBase)

Una vez ejecutada la aplicación `JoglBase.java`, se puede usar el teclado para interactuar con la escena:

### Cámara
* **H / F:** Mover cámara izquierda / derecha (Eje X).
* **8 / 2:** Mover cámara arriba / abajo (Eje Y).
* **T / G:** Acercar / Alejar (Eje Z).
* **R:** Rotación de la escena.

### Iluminación (Mover la luz)
* **I / K:** Mover luz en Eje Y.
* **J / L:** Mover luz en Eje X.
* **U / O:** Mover luz en Eje Z.

### Animación
* **C:** Activar/Desactivar animación de "Caminar" (Mueve brazos y piernas).

## 📂 Estructura del Proyecto

* **`src/JoglBase.java`:** Archivo principal. Contiene la lógica de renderizado del personaje Steve, el suelo de pasto y la gestión de eventos.
* **`src/JoglBase2.java` y `src/TJOGL2.java`:** Versiones alternativas o plantillas de práctica con diferentes geometrías.
* **`src/Light.java`:** Clase dedicada a probar configuraciones avanzadas de materiales y luces sobre objetos simples (esferas, teteras).
* **`imagenes/`:** Contiene los recursos gráficos (texturas `.png` y `.jpg`) como la piel de Steve y las texturas de pasto/tierra.

## 👤 Autor

* **Código base:** Genaro Méndez López
* **Modificaciones y Práctica:** Marco Antonio Vargas Valle
