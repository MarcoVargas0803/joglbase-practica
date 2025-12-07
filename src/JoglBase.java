
/**
 *
 * @author gmendez
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


// Importaciones para Texturas
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.jogamp.opengl.GLProfile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


/**
 * JoglBase Programa Plantilla (GLCanvas)
 */
@SuppressWarnings("serial")
public class JoglBase extends GLJPanel implements GLEventListener, KeyListener {
    // Define constants for the top-level container

    private static String TITLE = "Plantilla Base java open gl";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    private static final int FPS = 24; // animator's target frames per second
    private static final float factInc = 5.0f; // animator's target frames per second
    private final float fovy = 60.0f;

    private final GLU glu;  // for the GL Utility
    private final GLUT glut;
    
    float aspect = 0.0f;

    float rotacion = 0.0f;
    float despl = 0.0f;
    float despX = 0.0f;
    float despY = 0.0f;
    float despZ = 0.0f;

    float camX = 0.0f;
    float camY = 0.0f;
    float camZ = -2.0f;

    // --- Variables para la animación de caminar ---
    float anguloExtremidad = 0.0f;  // Ángulo actual de rotación de brazos/piernas
    float velocidadPaso = 5.0f;     // Qué tan rápido se mueven las extremidades
    boolean swingAdelante = true;   // Controla si la extremidad va hacia adelante o atrás
    boolean estaCaminando = false;  // Interruptor para prender/apagar la animación

    // --- Variable para la textura ---
    Texture texturaSteve;

    // --- Método auxiliar para cargar imágenes ---
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

        glu = new GLU();                        // get GL Utilities
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
        // 1. Habilitar texturas
        gl.glEnable(GL2.GL_TEXTURE_2D);
        // 2. Cargar la imagen de Steve
        // Asegúrate de que la ruta coincida con tu archivo
        this.texturaSteve = cargarTextura("imagenes/steve.png");

        // (Opcional) Configurar cómo se mezcla la textura con el color de fondo
        // gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        // Si usas REPLACE, se verá la foto tal cual. Si usas MODULATE (por defecto), se mezcla con el color del material.
        
        /*
              | 1   0   0   0 |  Matriz Identidad para ModelView 
              | 0   1   0   0 |
              | 0   0   1   0 |
              | 0   0   0   1 |
        */
                
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
    // Método auxiliar para dibujar UNA cara del cubo
    // Método auxiliar corregido para coordenadas Y invertidas
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

        // NOTA: He intercambiado v_min y v_max en los vértices para enderezar la imagen

        // Cara Frontal (Z positivo)
        if (cara == 1) {
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz);
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py, pz);
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz);
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz);
        }
        // Cara Trasera (Z negativo)
        else if (cara == 2) {
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz+1);    // Arriba-Izq (En la foto es v_min, pero OpenGL invierte)
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz+1);      // Abajo-Izq
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py, pz+1);    // Abajo-Der
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py+1, pz+1); // Arriba-Der

        }
        // Cara Izquierda (X negativo) - Oreja izquierda
        else if (cara == 3) {
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz);      // Arriba-Atrás
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py, pz);        // Abajo-Atrás
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px, py, pz+1);      // Abajo-Frente
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px, py+1, pz+1);    // Arriba-Frente
        }
        // Cara Derecha (X positivo) - Oreja derecha
        else if (cara == 4) {
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px+1, py+1, pz+1);
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px+1, py, pz+1);
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py, pz);
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz);
        }
        // Cara Superior (Y positivo) - La tapa de la cabeza
        else if (cara == 5) {
            gl.glTexCoord2f(u_min, v_max); gl.glVertex3f(px, py+1, pz);      // Atrás-Izq
            gl.glTexCoord2f(u_min, v_min); gl.glVertex3f(px, py+1, pz+1);    // Frente-Izq
            gl.glTexCoord2f(u_max, v_min); gl.glVertex3f(px+1, py+1, pz+1);  // Frente-Der
            gl.glTexCoord2f(u_max, v_max); gl.glVertex3f(px+1, py+1, pz);    // Atrás-Der
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

        // 6. CARA INFERIOR (Cuello - opcional, podemos usar la misma del pelo o dejarla vacía)
        // dibujarCara(gl, 16, 0, 24, 8, 0,0,0, 6); // (requeriría implementar caso 6)

        gl.glPopMatrix();

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
    }
    void dibujarBrazoDerechoSteve(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(-0.35f, 0.2f, 0.0f); // 1. Ir al hombro
        gl.glRotatef(-anguloExtremidad, 1.0f, 0.0f, 0.0f); // 2. ROTAR
        gl.glTranslatef(0.0f, -0.25f, 0.0f); // 3. Ajustar centro del brazo

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

// Dibujar el brazo derecho con textura
        dibujarCara(gl, 44, 20, 48, 32, 0, 0, 0, 1); // Frente
        dibujarCara(gl, 40, 20, 44, 32, 0, 0, 0, 4); // Lado derecho
        dibujarCara(gl, 48, 20, 52, 32, 0, 0, 0, 3); // Lado izquierdo
        dibujarCara(gl, 52, 20, 56, 32, 0, 0, 0, 2); // Espalda
        dibujarCara(gl, 44, 16, 48, 20, 0, 0, 0, 5); // Superior
        dibujarCara(gl, 48, 16, 52, 20, 0, 0, 0, 6); // Inferior

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
        gl.glPopMatrix();
    }
    void dibujarBrazoIzquierdoSteve(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(-0.35f, 0.2f, 0.0f); // 1. Ir al hombro
        gl.glRotatef(anguloExtremidad, 1.0f, 0.0f, 0.0f); // 2. ROTAR
        gl.glTranslatef(0.0f, -0.25f, 0.0f); // 3. Ajustar centro del brazo (opcional, para que rote desde arriba)

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

// Dibujar el brazo izquierdo con textura
        dibujarCara(gl, 44, 20, 48, 32, 0, 0, 0, 1); // Frente
        dibujarCara(gl, 40, 20, 44, 32, 0, 0, 0, 4); // Lado derecho
        dibujarCara(gl, 48, 20, 52, 32, 0, 0, 0, 3); // Lado izquierdo
        dibujarCara(gl, 52, 20, 56, 32, 0, 0, 0, 2); // Espalda
        dibujarCara(gl, 44, 16, 48, 20, 0, 0, 0, 5); // Superior
        dibujarCara(gl, 48, 16, 52, 20, 0, 0, 0, 6); // Inferior

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
        gl.glPopMatrix();
    }


    void dibujarPiernaIzquierdaSteve(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(-0.121f, -0.4f, 0.0f); // Ir a la cadera
        gl.glRotatef(-anguloExtremidad, 1.0f, 0.0f, 0.0f); // NEGATIVO
        gl.glTranslatef(0.0f, -0.3f, 0.0f); // Bajar para dibujar la pierna desde la cadera

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

// Dibujar la pierna izquierda con textura
        dibujarCara(gl, 4, 20, 8, 32, 0, 0, 0, 1); // Frente
        dibujarCara(gl, 0, 20, 4, 32, 0, 0, 0, 4); // Lado derecho
        dibujarCara(gl, 8, 20, 12, 32, 0, 0, 0, 3); // Lado izquierdo
        dibujarCara(gl, 12, 20, 16, 32, 0, 0, 0, 2); // Espalda
        dibujarCara(gl, 4, 16, 8, 20, 0, 0, 0, 5); // Superior
        dibujarCara(gl, 8, 16, 12, 20, 0, 0, 0, 6); // Inferior

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
        gl.glPopMatrix();
    }
    void dibujarPiernaDerechaSteve(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(0.121f, -0.4f, 0.0f); // Ir a la cadera
        gl.glRotatef(anguloExtremidad, 1.0f, 0.0f, 0.0f); // POSITIVO
        gl.glTranslatef(0.0f, -0.3f, 0.0f); // Bajar para dibujar la pierna desde la cadera

        if (texturaSteve != null) {
            texturaSteve.bind(gl);
            texturaSteve.enable(gl);
        }

// Dibujar la pierna derecha con textura
        dibujarCara(gl, 4, 20, 8, 32, 0, 0, 0, 1); // Frente
        dibujarCara(gl, 0, 20, 4, 32, 0, 0, 0, 4); // Lado derecho
        dibujarCara(gl, 8, 20, 12, 32, 0, 0, 0, 3); // Lado izquierdo
        dibujarCara(gl, 12, 20, 16, 32, 0, 0, 0, 2); // Espalda
        dibujarCara(gl, 4, 16, 8, 20, 0, 0, 0, 5); // Superior
        dibujarCara(gl, 8, 16, 12, 20, 0, 0, 0, 6); // Inferior

        if (texturaSteve != null) {
            texturaSteve.disable(gl);
        }
        gl.glPopMatrix();
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



    public void dibujarPersona(GL2 gl, GLUT glut) {

        // --- TORSO (Inmóvil) ---
        gl.glPushMatrix();
        gl.glScalef(0.5f, 0.7f, 0.3f);
        dibujarTorsoSteve(gl);
        gl.glPopMatrix();

        // --- CABEZA ---
        gl.glPushMatrix();
        dibujarCabezaSteve(gl);
        gl.glPopMatrix();

        // --- BRAZO IZQUIERDO ---
        gl.glPushMatrix();
        dibujarBrazoIzquierdoSteve(gl);
        gl.glPopMatrix();



        // --- BRAZO DERECHO ---
        gl.glPushMatrix();
        dibujarBrazoDerechoSteve(gl);
        gl.glPopMatrix();


        // DIBUJAR EL CUBO DE TIERRA (En la mano)
        gl.glTranslatef(0.05f, -0.4f, -0.1f);
        gl.glColor3f(0.6f, 0.3f, 0.0f);
        gl.glScalef(0.3f, 0.3f, 0.3f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        // --- PIERNA IZQUIERDA ---
        //dibujarPiernaIzquierdaSteve(gl);

        // --- PIERNA DERECHA ---
        //dibujarPiernaDerechaSteve(gl);
    }






    /* public void dibujarPersona(GL2 gl, GLUT glut) {

        // --- TORSO (Inmóvil) ---
        gl.glPushMatrix();
        //gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glScalef(0.5f, 0.7f, 0.3f);
        dibujarTorsoSteve(gl);
        //glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        // --- CABEZA ---
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.5f, 0.0f);
        gl.glScalef(0.5f, 0.5f, 0.5f); // Ajustar tamaño si queda muy grande
        dibujarCabezaSteve(gl);
        gl.glPopMatrix();

        // --- BRAZO IZQUIERDO (Se mueve con anguloExtremidad) ---
          gl.glPushMatrix();
        gl.glTranslatef(-0.35f, 0.2f, 0.0f); // 1. Ir al hombro
        //gl.glRotatef(anguloExtremidad, 1.0f, 0.0f, 0.0f); // 2. ROTAR
        //gl.glTranslatef(0.0f, -0.25f, 0.0f); // 3. Ajustar centro del brazo (opcional, para que rote desde arriba)
//
        //gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glScalef(0.20f, 0.6f, 0.3f);
        dibujarBrazoIzquierdoSteve(gl);

        //glut.glutSolidCube(1.0f);
         gl.glPopMatrix();

//        // --- BRAZO DERECHO ---
          gl.glPushMatrix();
        // 1. Mover al hombro y rotar el brazo completo
        gl.glTranslatef(0.35f, 0.2f, 0.0f);
        gl.glRotatef(-anguloExtremidad, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, -0.25f, 0.0f);
//
//        // 2. DIBUJAR EL BRAZO (Carne)
        gl.glPushMatrix(); // Guardamos para dibujar el brazo independientemente
        gl.glColor3f(1.0f, 0.0f, 0.0f); // Color rojo (manga)
        gl.glScalef(0.20f, 0.6f, 0.3f);
        //glut.glutSolidCube(1.0f);
        dibujarBrazoDerechoSteve(gl);
          gl.glPopMatrix();

        // 3. DIBUJAR EL CUBO DE TIERRA (En la mano)
        // Nos movemos hacia abajo (donde estaría la mano en el brazo)
        gl.glTranslatef(0.05f, -0.4f, -0.1f); // Bajamos más desde el centro del brazo

        gl.glColor3f(0.6f, 0.3f, 0.0f); // <--- COLOR CAFÉ CORREGIDO
        gl.glScalef(0.3f, 0.3f, 0.3f);  // Un cubo de buen tamaño
        glut.glutSolidCube(1.0f);

        gl.glPopMatrix();

        // --- PIERNA IZQUIERDA (Se mueve con -anguloExtremidad, opuesto al brazo izq) ---
          gl.glPushMatrix();
        gl.glTranslatef(-0.121f, -0.4f, 0.0f); // Ir a la cadera
        gl.glRotatef(-anguloExtremidad, 1.0f, 0.0f, 0.0f); // NEGATIVO
        gl.glTranslatef(0.0f, -0.3f, 0.0f); // Bajar para dibujar la pierna desde la cadera
//
//        gl.glColor3f(0.3f, 0.3f, 0.3f);
          gl.glScalef(0.25f, 0.7f, 0.3f);
        dibujarPiernaIzquierdaSteve(gl);
        //glut.glutSolidCube(1.0f);
          gl.glPopMatrix();

        // --- PIERNA DERECHA (Se mueve con anguloExtremidad) ---
          gl.glPushMatrix();
 //       gl.glTranslatef(0.121f, -0.4f, 0.0f);
        gl.glRotatef(anguloExtremidad, 1.0f, 0.0f, 0.0f); // POSITIVO
        gl.glTranslatef(0.0f, -0.3f, 0.0f);

        gl.glColor3f(0.2f, 0.2f, 0.2f);
          gl.glScalef(0.25f, 0.7f, 0.3f);
        dibujarPiernaDerechaSteve(gl);
        //glut.glutSolidCube(1.0f);
          gl.glPopMatrix();
    } */
    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Configuración de proyección y cámara
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(fovy, aspect, 0.1, 20.0);

        // AQUÍ usamos tus variables camX, camY, camZ para mover la cámara
        glu.gluLookAt(this.camX, this.camY, this.camZ,
                0.0, 0.0, 0.0,  // Mirando hacia el centro (0,0,0)
                0.0, 1.0, 0.0); // Arriba es Y positivo

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // --- Lógica de actualización de animación ---
        // Si quieres que siempre camine, cambia 'estaCaminando' a true arriba o quita el if
        if (estaCaminando) {
            if (swingAdelante) {
                anguloExtremidad += velocidadPaso;
                if (anguloExtremidad > 45.0f) { // Límite frontal (45 grados)
                    swingAdelante = false; // Cambiar dirección hacia atrás
                }
            } else {
                anguloExtremidad -= velocidadPaso;
                if (anguloExtremidad < -45.0f) { // Límite trasero (-45 grados)
                    swingAdelante = true; // Cambiar dirección hacia adelante
                }
            }
        }

        // Dibujamos el piso (Grid) para referencia visual
        dibujarPiso(gl);

        // Dibujamos a la persona
        // Aplicamos transformaciones globales si quieres mover a la persona completa
        gl.glPushMatrix();
        // gl.glTranslatef(despX, despY, despZ); // Descomenta si quieres mover al personaje
        // gl.glRotatef(rotacion, 0.0f, 1.0f, 0.0f); // Descomenta si quieres que el personaje gire
        dibujarPersona(gl, glut);
        gl.glPopMatrix();

        gl.glFlush();
    }

    // Método auxiliar rápido para dibujar un piso simple
    public void dibujarPiso(GL2 gl) {
        gl.glColor3f(0.8f, 0.8f, 0.8f);
        gl.glBegin(GL2.GL_LINES);
        for(float i=-10; i<=10; i+=1.0f) {
            gl.glVertex3f(i, -1.0f, -10.0f);
            gl.glVertex3f(i, -1.0f, 10.0f);
            gl.glVertex3f(-10.0f, -1.0f, i);
            gl.glVertex3f(10.0f, -1.0f, i);
        }
        gl.glEnd();
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
            case KeyEvent.VK_LEFT:
                this.despX -= 0.2f;
                break;
            case KeyEvent.VK_RIGHT:
                this.despX += 0.2f;
                break;
            case KeyEvent.VK_A:
                this.despZ += 0.2f;
                break;
            case KeyEvent.VK_Z:
                this.despZ -= 0.2f;
                break;
            case KeyEvent.VK_O:
                this.despY += 0.2f;
                break;
            case KeyEvent.VK_L:
                this.despY -= 0.2f;
                break;
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
