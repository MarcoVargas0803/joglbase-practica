
/**
 *
 * @author gmendez
 * @Modificación realizada por Marco Antonio Vargas Valle
 */

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL2.GL_MODELVIEW;
import static com.jogamp.opengl.GL2.GL_PROJECTION;


/*
*  JoglBase Programa Plantilla (GLCanvas)
*/

//Elimina advertencias
@SuppressWarnings("serial")

public class JoglBase extends GLJPanel implements GLEventListener, KeyListener {
    // Define constants for the top-level container

    private static String TITLE = "Practica Java OpenGL";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    private static final int FPS = 24; // animator's target frames per second
    private static final float factInc = 5.0f; // animator's target frames per second
    private final float fovy = 60.0f;

    // Instancia de GLU (GL Utilities)
    private final GLU glu;
    // Instancia de GLUT (GL Utilities Toolkit)
    private final GLUT glut;


    // Variables para la cámara
    float aspect = 0.0f;
    float rotacion = 0.0f;
    float despl = 0.0f;
    float despX = 0.0f;
    float despY = 0.0f;
    float despZ = 0.0f;
    float camX = 0.0f;
    float camY = 0.0f;
    float camZ = -2.0f;

    // Variables para la posición de la luz (Inician arriba a la derecha)
    float luzX = 10.0f;
    float luzY = 10.0f;
    float luzZ = 10.0f;

    // Variables para la animación de caminar
    float anguloExtremidad = 0.0f;  // Ángulo actual de rotación de brazos/piernas
    float velocidadPaso = 5.0f;     // Qué tan rápido se mueven las extremidades
    boolean swingAdelante = true;   // Controla si la extremidad va hacia adelante o atrás
    boolean estaCaminando = false;  // Interruptor para prender/apagar la animación

    /*
    * Sección para las texturas
    * */

    // Variable para la textura de Steve
    Texture texturaSteve;

    // Variables para las texturas del bloque
    Texture texturaPastoTop;
    Texture texturaPastoLado;
    Texture texturaTierraBot;

    // Método auxiliar para cargar imágenes
    Texture cargarTextura(String imageFile) {
        Texture text1 = null;
        try {
            BufferedImage buffImage = ImageIO.read(new File(imageFile));
            text1 = AWTTextureIO.newTexture(GLProfile.getDefault(), buffImage, false);
        } catch (IOException ioe) {
            System.out.println("Problema al cargar el archivo " + imageFile);
        }
        return text1;
    }
    /**
     * The entry main() method to setup the top-level container and animator
     */
    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the OpenGL rendering canvas
                GLJPanel canvas = new JoglBase();
                canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                // Create the top-level container
                final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
                
                BorderLayout bl = new BorderLayout();
                
                frame.getContentPane().add(canvas,BorderLayout.CENTER);
                frame.addKeyListener((KeyListener) canvas);

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Use a dedicate thread to run the stop() to ensure that the
                        // animator stops before program exits.
                        new Thread() {
                            @Override
                            public void run() {
                                if (animator.isStarted()) {
                                    animator.stop();
                                }
                                System.exit(0);
                            }
                        }.start();
                    }
                });

                frame.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent ev) {
                        Component c = (Component) ev.getSource();
                        // Get new size
                        Dimension newSize = c.getSize();                        
                        canvas.setSize(newSize);
                    }
                });

                frame.setTitle(TITLE);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    /**
     * Constructor to setup the GUI for this Component
     */
    public JoglBase() {
        this.addGLEventListener(this);
        this.addKeyListener(this);

        glu = new GLU(); // get GL Utilities
        glut = new GLUT();
    }

    // ------ Implement methods declared in GLEventListener ------
    /**
     * Called back immediately after the OpenGL context is initialized. Can be
     * used to perform one-time initialization. Run only once.
     */


    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();   // get the OpenGL graphics context

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // set background (clear) color

        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do  

        gl.glMatrixMode(GL_MODELVIEW);  // Escala, Rotacion, Traslacion
        gl.glLoadIdentity(); // reset the model-view matrix

        /*
        * Sección de inicialización de texturas e iluminación.
         */
        // Habilitar texturas
        gl.glEnable(GL2.GL_TEXTURE_2D);

        // Cargar la imagen de Steve
        this.texturaSteve = cargarTextura("imagenes/steve.png");

        // Cargar las texturas del bloque de pasto
        this.texturaPastoTop = cargarTextura("imagenes/pasto_top.png");
        this.texturaPastoLado = cargarTextura("imagenes/pasto_lado.png");
        this.texturaTierraBot = cargarTextura("imagenes/tierra.png");

        // Activar el cálculo de iluminación
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0); // Encendemos la Luz #0

        float[] luzAmbiente = { 0.1f, 0.1f, 0.1f, 1.0f };  // Sombras oscuras
        float[] luzDifusa   = { 1.0f, 1.0f, 1.0f, 1.0f };  // Luz fuerte}
        // la luz de posición (x, y, z, w) se define en el Display para efectos prácticos.

        //propiedades de la iluminación
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, luzDifusa, 0);

        // Como se usó glScalef, las normales se deforman. Esto lo arregla.
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_RESCALE_NORMAL);

        // Materiales: Hacer que el color de la textura reaccione a la luz
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
                
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) {
            height = 1;   // prevent divide by zero
        }
        
        this.aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        
        /*
              | 1   0   0   0 |  Matriz Identidad para Pro 
              | 0   1   0   0 |
              | 0   0   1   0 |
              | 0   0   0   1 |
        */
        
        //glu.gluPerspective(fovy, aspect, 0.1, 20.0); // fovy, aspect, zNear, zFar
        glu.gluLookAt(this.camX, this.camY, this.camZ, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
       
    }
    // Método auxiliar para dibujar UNA cara del cubo con una parte específica de la textura
    // x1, y1 = esquina inferior izquierda del recorte (en la imagen)
    // x2, y2 = esquina superior derecha del recorte (en la imagen)

    void dibujarCara(GL2 gl, float x1, float y1, float x2, float y2,
                     float px, float py, float pz, int cara) {

        float anchoImg = 64.0f;
        float altoImg = 64.0f;

        // Coordenadas de textura
        float u_min = x1 / anchoImg;
        float u_max = x2 / anchoImg;
        float v_min = y1 / altoImg; // Parte Superior de la textura
        float v_max = y2 / altoImg; // Parte Inferior de la textura

        gl.glBegin(GL2.GL_QUADS);

        // Cara Frontal (Z positivo)
        if (cara == 1) {
            gl.glNormal3f(0.0f, 0.0f, -1.0f); // <--- AGREGADO
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz);
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py, pz);
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz);
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz);
        }
        // Cara Trasera (Z negativo)
        else if (cara == 2) {
            gl.glNormal3f(0.0f, 0.0f, 1.0f); // <--- AGREGADO
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz+1);    // Arriba-Izq (En la foto es v_min, pero OpenGL invierte)
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz+1);      // Abajo-Izq
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py, pz+1);    // Abajo-Der
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz+1); // Arriba-Der

        }
        // Cara Izquierda (X negativo) - Oreja izquierda
        else if (cara == 3) {
            gl.glNormal3f(-1.0f, 0.0f, 0.0f); // <--- AGREGADO
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz);      // Arriba-Atrás
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz);        // Abajo-Atrás
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px, py, pz+1);      // Abajo-Frente
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px, py+1, pz+1);    // Arriba-Frente
        }
        // Cara Derecha (X positivo) - Oreja derecha
        else if (cara == 4) {
            gl.glNormal3f(1.0f, 0.0f, 0.0f); // <--- AGREGADO
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px+1, py+1, pz+1);
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px+1, py, pz+1);
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py, pz);
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz);
        }
        // Cara Superior (Y positivo) - La tapa de la cabeza
        else if (cara == 5) {
            gl.glNormal3f(0.0f, 1.0f, 0.0f); // <--- AGREGADO
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py+1, pz);      // Atrás-Izq
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz+1);    // Frente-Izq
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz+1);  // Frente-Der
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py+1, pz);    // Atrás-Der
        }
        else if (cara == 6) {
            gl.glNormal3f(0.0f, -1.0f, 0.0f); // <--- AGREGADO
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz);
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py, pz);
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py, pz+1);
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py, pz+1);
        }
        gl.glEnd();
    }

    void dibujarCabezaSteve(GL2 gl) {
        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

        // Color blanco para no teñir la textura
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        // Ajustamos el centro del cubo para que rote desde el cuello
        // (Nuestro método dibuja desde 0,0,0 hacia positivos, así que centramos con translate)
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.5f, -0.5f);

        // Usamos el método auxiliar: (gl, x_foto, y_foto, x_fin, y_fin, pos_x, pos_y, pos_z, tipo_cara)

        // 1. CARA FRONTAL (La cara de Steve) - Coords: 8,8 a 16,16
        dibujarCara(gl, 8, 8, 16, 16,   0,0,0, 1);

        // 2. CARA DERECHA (Oreja der) - Coords: 0,8 a 8,16
        dibujarCara(gl, 0, 8, 8, 16,    0,0,0, 4);

        // 3. CARA IZQUIERDA (Oreja izq) - Coords: 16,8 a 24,16
        dibujarCara(gl, 16, 8, 24, 16,  0,0,0, 3);

        // 4. CARA TRASERA (Nuca) - Coords: 24,8 a 32,16
        dibujarCara(gl, 24, 8, 32, 16,  0,0,0, 2);

        // 5. CARA SUPERIOR (Pelo arriba) - Coords: 8,0 a 16,8
        dibujarCara(gl, 8, 0, 16, 8,    0,0,0, 5);

        // 6. CARA INFERIOR (Cuello) - Coords: 16,0 a 24,8
        dibujarCara(gl, 16, 0, 24, 8, 0, 0, 0, 6);

        gl.glPopMatrix();

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
    }

    void dibujarTorsoSteve(GL2 gl) {
        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

        // Color blanco para no teñir la textura
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        // Ajustamos el centro del cubo para que rote desde el torso
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.5f, -0.5f);

        // Usamos el método auxiliar: (gl, x_foto, y_foto, x_fin, y_fin, pos_x, pos_y, pos_z, tipo_cara)

        // 1. CARA FRONTAL (Frente del torso) - Coords: 20,20 a 28,32
        dibujarCara(gl, 20, 20, 28, 32, 0, 0, 0, 1);

        // 2. CARA DERECHA (Lado derecho del torso) - Coords: 16,20 a 20,32
        dibujarCara(gl, 16, 20, 20, 32, 0, 0, 0, 4);

        // 3. CARA IZQUIERDA (Lado izquierdo del torso) - Coords: 28,20 a 32,32
        dibujarCara(gl, 28, 20, 32, 32, 0, 0, 0, 3);

        // 4. CARA TRASERA (Espalda del torso) - Coords: 32,20 a 40,32
        dibujarCara(gl, 32, 20, 40, 32, 0, 0, 0, 2);

        // 5. CARA SUPERIOR (Parte superior del torso) - Coords: 20,16 a 28,20
        dibujarCara(gl, 20, 16, 28, 20, 0, 0, 0, 5);

        // 6. CARA INFERIOR (Parte inferior del torso) - Coords: 28,16 a 32,20
        dibujarCara(gl, 28, 16, 32, 20, 0, 0, 0, 6);

        gl.glPopMatrix();

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
    }
    void dibujarBrazoDerechoSteve(GL2 gl) {
        gl.glPushMatrix();

        // 1. ROTAR (El origen actual es el hombro gracias a dibujarPersona)
        gl.glRotatef(-anguloExtremidad, 1.0f, 0.0f, 0.0f);

        // 2. DIBUJAR GEOMETRÍA
        // Bajamos el brazo la mitad de su longitud para que "cuelgue" del hombro
        // Longitud brazo = 0.75. Mitad = 0.3.
        gl.glTranslatef(0.0f, -0.3f, 0.0f);

        // Escalamos al tamaño de un brazo
        gl.glScalef(0.25f, 0.75f, 0.25f);

        // Tu método original dibuja texturas. Como ya está centrado en dibujarCara, esto funciona.
        // NOTA: Usamos las coordenadas de textura del brazo derecho
        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.5f, -0.5f); // Centrar el cubo unitario
        // Coordenadas brazo derecho (según tu textura)
        dibujarCara(gl, 44, 20, 48, 32, 0, 0, 0, 1); // Frente
        dibujarCara(gl, 40, 20, 44, 32, 0, 0, 0, 4); // Der
        dibujarCara(gl, 48, 20, 52, 32, 0, 0, 0, 3); // Izq
        dibujarCara(gl, 52, 20, 56, 32, 0, 0, 0, 2); // Atras
        dibujarCara(gl, 44, 16, 48, 20, 0, 0, 0, 5); // Arriba
        dibujarCara(gl, 48, 16, 52, 20, 0, 0, 0, 6); // Abajo
        gl.glPopMatrix();

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }

        gl.glPopMatrix();
    }

    void dibujarBrazoIzquierdoSteve(GL2 gl) {
        gl.glPushMatrix();
        // Rotación opuesta o igual según quieras (swingAdelante)
        gl.glRotatef(anguloExtremidad, 1.0f, 0.0f, 0.0f);

        gl.glTranslatef(0.0f, -0.3f, 0.0f); // Bajar desde el hombro
        gl.glScalef(0.25f, 0.75f, 0.25f);     // Tamaño brazo

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.5f, -0.5f);
        dibujarCara(gl, 44, 20, 48, 32, 0, 0, 0, 1); // Frente
        dibujarCara(gl, 40, 20, 44, 32, 0, 0, 0, 4); // Der
        dibujarCara(gl, 48, 20, 52, 32, 0, 0, 0, 3); // Izq
        dibujarCara(gl, 52, 20, 56, 32, 0, 0, 0, 2); // Atras
        dibujarCara(gl, 44, 16, 48, 20, 0, 0, 0, 5); // Arriba
        dibujarCara(gl, 48, 16, 52, 20, 0, 0, 0, 6); // Abajo
        gl.glPopMatrix();

        if (texturaSteve != null) texturaSteve.disable(gl);
        gl.glPopMatrix();
    }

    void dibujarPiernaIzquierdaSteve(GL2 gl) {
        gl.glPushMatrix();
        // La pierna rota opuesta al brazo del mismo lado
        gl.glRotatef(-anguloExtremidad, 1.0f, 0.0f, 0.0f);

        gl.glTranslatef(0.0f, -0.375f, 0.0f); // Bajar desde la cadera
        gl.glScalef(0.25f, 0.75f, 0.25f);

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.5f, -0.5f);
        // Coordenadas Pierna
        dibujarCara(gl, 4, 20, 8, 32, 0, 0, 0, 1);
        dibujarCara(gl, 0, 20, 4, 32, 0, 0, 0, 4);
        dibujarCara(gl, 8, 20, 12, 32, 0, 0, 0, 3);
        dibujarCara(gl, 12, 20, 16, 32, 0, 0, 0, 2);
        dibujarCara(gl, 4, 16, 8, 20, 0, 0, 0, 5);
        dibujarCara(gl, 8, 16, 12, 20, 0, 0, 0, 6);
        gl.glPopMatrix();

        if (texturaSteve != null) texturaSteve.disable(gl);
        gl.glPopMatrix();
    }

    void dibujarPiernaDerechaSteve(GL2 gl) {
        gl.glPushMatrix();
        gl.glRotatef(anguloExtremidad, 1.0f, 0.0f, 0.0f);

        gl.glTranslatef(0.0f, -0.375f, 0.0f);
        gl.glScalef(0.25f, 0.75f, 0.25f);

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.5f, -0.5f);
        // Mismas coords o espejo
        dibujarCara(gl, 4, 20, 8, 32, 0, 0, 0, 1);
        dibujarCara(gl, 0, 20, 4, 32, 0, 0, 0, 4);
        dibujarCara(gl, 8, 20, 12, 32, 0, 0, 0, 3);
        dibujarCara(gl, 12, 20, 16, 32, 0, 0, 0, 2);
        dibujarCara(gl, 4, 16, 8, 20, 0, 0, 0, 5);
        dibujarCara(gl, 8, 16, 12, 20, 0, 0, 0, 6);
        gl.glPopMatrix();

        if (texturaSteve != null) texturaSteve.disable(gl);
        gl.glPopMatrix();
    }


    public void dibujarPersona(GL2 gl, GLUT glut) {
        // --- TORSO ---
        gl.glPushMatrix();
        // Escalamos el cubo unitario para que tenga forma de torso
        // Ancho: 0.5, Alto: 0.75, Profundidad: 0.25
        gl.glScalef(0.5f, 0.75f, 0.25f);
        dibujarTorsoSteve(gl);
        gl.glPopMatrix();

        // --- CABEZA ---
        gl.glPushMatrix();
        // Movemos la cabeza ARRIBA del torso.
        // El torso acaba en Y=0.375 (la mitad de 0.75). La cabeza mide 0.5.
        // Centro cabeza = 0.375 (top cuerpo) + 0.25 (mitad cabeza) = 0.625
        gl.glTranslatef(0.0f, 0.625f, 0.0f);
        gl.glScalef(0.5f, 0.5f, 0.5f); // Cabeza cuadrada
        dibujarCabezaSteve(gl);
        gl.glPopMatrix();

        // --- BRAZO DERECHO ---
        gl.glPushMatrix();
        // Posicion Hombro: X = 0.25 (mitad cuerpo) + 0.125 (mitad brazo) = 0.375
        // Y = 0.30 (un poco abajo del top del hombro)
        gl.glTranslatef(-0.38f, 0.30f, 0.0f);
        dibujarBrazoDerechoSteve(gl); // Dentro aplicamos la rotación
        gl.glPopMatrix();

        // --- BRAZO IZQUIERDO ---
        gl.glPushMatrix();
        gl.glTranslatef(0.38f, 0.30f, 0.0f);
        dibujarBrazoIzquierdoSteve(gl);
        gl.glPopMatrix();

        // --- PIERNA IZQUIERDA ---
        gl.glPushMatrix();
        // Posicion Cadera: Y = -0.375 (base del cuerpo)
        // X = Desplazado un poco del centro
        gl.glTranslatef(0.13f, -0.375f, 0.0f);
        dibujarPiernaIzquierdaSteve(gl);
        gl.glPopMatrix();

        // --- PIERNA DERECHA ---
        gl.glPushMatrix();
        gl.glTranslatef(-0.13f, -0.375f, 0.0f);
        dibujarPiernaDerechaSteve(gl);
        gl.glPopMatrix();
    }

    void dibujarBloque(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        // Centramos el cubo
        gl.glTranslatef(-0.5f, -0.5f, -0.5f);

        // Color base blanco para que la luz y textura se mezclen bien
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        // ------------------------------------------------
        // GRUPO 1: CARA SUPERIOR (Pasto Verde)
        // ------------------------------------------------
        if (texturaPastoTop != null) {
            texturaPastoTop.bind(gl);
            texturaPastoTop.enable(gl);
        }

        gl.glBegin(GL2.GL_QUADS);
        // NORMAL HACIA ARRIBA (Y+)
        gl.glNormal3f(0.0f, 1.0f, 0.0f);

        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.0f, 1.0f, 0.0f); // Atrás-Izq
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, 1.0f, 1.0f); // Frente-Izq
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f); // Frente-Der
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, 0.0f); // Atrás-Der
        gl.glEnd();

        if (texturaPastoTop != null) texturaPastoTop.disable(gl);


        // ------------------------------------------------
        // GRUPO 2: CARA INFERIOR (Tierra)
        // ------------------------------------------------
        if (texturaTierraBot != null) {
            texturaTierraBot.bind(gl);
            texturaTierraBot.enable(gl);
        }

        gl.glBegin(GL2.GL_QUADS);
        // NORMAL HACIA ABAJO (Y-)
        gl.glNormal3f(0.0f, -1.0f, 0.0f);

        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glEnd();

        if (texturaTierraBot != null) texturaTierraBot.disable(gl);


        // ------------------------------------------------
        // GRUPO 3: CARAS LATERALES (Pasto Lateral)
        // ------------------------------------------------
        if (texturaPastoLado != null) {
            texturaPastoLado.bind(gl);
            texturaPastoLado.enable(gl);
        }

        gl.glBegin(GL2.GL_QUADS);

        // --- Frente (Z+) ---
        gl.glNormal3f(0.0f, 0.0f, 1.0f); // Apunta hacia la cámara (Z+)
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f);

        // --- Atrás (Z-) ---
        gl.glNormal3f(0.0f, 0.0f, -1.0f); // Apunta al fondo (Z-)
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f, 0.0f);

        // --- Izquierda (X-) ---
        gl.glNormal3f(-1.0f, 0.0f, 0.0f); // Apunta a la izquierda (X-)
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, 1.0f, 1.0f);

        // --- Derecha (X+) ---
        gl.glNormal3f(1.0f, 0.0f, 0.0f); // Apunta a la derecha (X+)
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f);

        gl.glEnd();

        if (texturaPastoLado != null) texturaPastoLado.disable(gl);

        gl.glPopMatrix();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // LIMPIAR PANTALLA AL INICIO
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // CONFIGURAR PROYECCIÓN (El lente de la cámara)
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(fovy, aspect, 0.1, 100.0); // Aumenté el rango de visión a 100.0 para ver más lejos

        // CONFIGURAR VISTA/MODELO (Posición de la cámara y objetos)
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // La cámara se mueve en el mundo (ModelView), no en la proyección
        glu.gluLookAt(this.camX, this.camY, this.camZ,
                0.0, 0.0, 0.0,
                0.0, 1.0, 0.0);

        // POSICIONAR LA LUZ (Después de colocar la cámara)
        float[] luzPosicion = { luzX, luzY, luzZ, 1.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, luzPosicion, 0);

        // DIBUJAR LA ESFERA (Representación visual de la luz)
        gl.glPushMatrix();
        gl.glTranslatef(luzX, luzY, luzZ);
        gl.glDisable(GL2.GL_LIGHTING); // Apagamos luz para que la esfera tenga color propio
        gl.glColor3f(1.0f, 1.0f, 0.0f); // Amarillo
        glut.glutSolidSphere(0.5f, 10, 10);
        gl.glEnable(GL2.GL_LIGHTING);  // Encendemos de nuevo para el resto
        gl.glPopMatrix();

        // LÓGICA DE ANIMACIÓN
        if (estaCaminando) {
            if (swingAdelante) {
                anguloExtremidad += velocidadPaso;
                if (anguloExtremidad > 45.0f) swingAdelante = false;
            } else {
                anguloExtremidad -= velocidadPaso;
                if (anguloExtremidad < -45.0f) swingAdelante = true;
            }
        }

        // 6. DIBUJAR ESCENA
        dibujarPiso(gl);
        gl.glPushMatrix();
        dibujarPersona(gl, glut);
        gl.glPopMatrix();
        gl.glFlush();
    }

    // Método auxiliar para dibujar un piso
    public void dibujarPiso(GL2 gl) {
        // Dibujamos un área de 10x10 bloques
        for(float x = -5; x <= 5; x++) {
            for(float z = -5; z <= 5; z++) {
                // Dibujamos el bloque en X, -1.0 (piso), Z
                // Ajusta la Y (-1.0f) para que quede justo debajo de los pies de Steve
                dibujarBloque(gl, x, -1.5f, z);
            }
        }
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        System.out.println("codigo presionado = " + codigo);

        switch (codigo) {
            case KeyEvent.VK_R:
                this.rotacion += 5.0f;
                break;
            case KeyEvent.VK_8:
                this.camY += 0.2f;
                break;
            case KeyEvent.VK_2:
                this.camY -= 0.2f;
                break;
            case KeyEvent.VK_H:
                this.camX += 0.2f;
                break;
            case KeyEvent.VK_F:
                this.camX -= 0.2f;
                break;
            case KeyEvent.VK_T:
                this.camZ += 0.2f;
                break;
            case KeyEvent.VK_G:
                this.camZ -= 0.2f;
                break;
            case KeyEvent.VK_C: // Tecla C para Caminar
                this.estaCaminando = !this.estaCaminando;
                break;
            // --- CONTROLES DE LA LUZ ---
            case KeyEvent.VK_I:
                this.luzY += 0.5f;
                break;
            case KeyEvent.VK_K:
                this.luzY -= 0.5f;
                break;
            case KeyEvent.VK_J:
                this.luzX -= 0.5f;
                break;
            case KeyEvent.VK_L:
                this.luzX += 0.5f;
                break;
            case KeyEvent.VK_U:
                this.luzZ -= 0.5f;
                break;
            case KeyEvent.VK_O:
                this.luzZ += 0.5f;
                break;

        }
        System.out.println("despX =" + this.despX + " - " + "despY =" + this.despY + " - " + "despZ =" + this.despZ);
        System.out.println("camX =" + this.camX + " - " + "camY =" + this.camY + " - " + "despZ =" + this.despZ);
    }

    @Override
    public void keyReleased(KeyEvent e) {
            
    }

    public static FloatBuffer toFloatBuffer(float[] v) {
        ByteBuffer buf = ByteBuffer.allocateDirect(v.length * 4);
        buf.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = buf.asFloatBuffer();
        buffer.put(v);
        buffer.position(0);
        return buffer;
    }


}
