
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

    float camX = 2.0f;
    float camY = 2.0f;
    float camZ = 8.0f;

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

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();  // reset the model-view matrix
                
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
        
        //glu.gluPerspective(fovy, aspect, 0.1, 20.0); // fovy, aspect, zNear, zFar
        glu.gluLookAt(this.camX, this.camY, this.camZ, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
       
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();
        
        glu.gluPerspective(fovy, aspect, 0.1, 20.0);
        glu.gluLookAt(this.camX, this.camY, this.camZ, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);        

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();  // reset the model-view matrix                    

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);               

        gl.glColor3f(0.0f, 0.0f, 1.0f);

        gl.glBegin(GL2.GL_LINES);
            gl.glVertex3f(-100.0f, 0.0f, 0.0f);
            gl.glVertex3f(100.0f, 0.0f, 0.0f);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(0.0f, -100.0f, 0.0f);
        gl.glVertex3f(0.0f, 100.0f, 0.0f);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(0.0f, 0.0f, -100.0f);
        gl.glVertex3f(0.0f, 0.0f, 100.0f);
        gl.glEnd();
                

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        //gl.glRotatef(this.rotacion, 0.0f, 1.0f, 0.0f);                
       
        float puntosControl[] = {0.0f,0.0f,0.0f,
                                 2.0f,2.0f,0.0f,
                                 3.0f,2.0f,0.0f,
                                 4.0f,0.0f,0.0f,
                                 5.0f,2.0f,0.0f
                                };
        
        FloatBuffer fb = toFloatBuffer(puntosControl);
        
        gl.glMap1f(GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 5, fb);
        gl.glEnable(GL_MAP1_VERTEX_3);
                
        gl.glBegin(GL_LINE_STRIP);
            gl.glColor3f(1.0f,0.0f,0.0f);
            for (int i = 0; i < puntosControl.length; i++) {
                gl.glEvalCoord1f((float) i /(puntosControl.length));
            }
        gl.glEnd();
        
        
        gl.glTranslatef(2.0f,2.0f,-1.0f);
        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            
            gl.glVertex3f(0.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, 0.0f);
            gl.glVertex3f(0.0f, 1.0f, 0.0f);  
        gl.glEnd();
        
        // Cuadro inferior - base
        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            
            gl.glVertex3f(0.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 0.0f, -1.0f);
            gl.glVertex3f(0.0f, 0.0f, -1.0f);  
        gl.glEnd();        
        
        // Cuadro superior - tapa       
        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            
            gl.glVertex3f(0.0f, 1.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glVertex3f(0.0f, 1.0f, -1.0f);  
        gl.glEnd();  

        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glVertex3f(0.0f, 0.0f, -1.0f);            
            gl.glVertex3f(1.0f, 0.0f, -1.0f);            
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glVertex3f(0.0f, 1.0f, -1.0f);  
        gl.glEnd();  
                
        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glVertex3f(0.0f, 0.0f, 0.0f);            
            gl.glVertex3f(0.0f, 1.0f, 0.0f);            
            gl.glVertex3f(0.0f, 1.0f, -1.0f);
            gl.glVertex3f(0.0f, 0.0f, -1.0f);  
        gl.glEnd();

        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glVertex3f(1.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glVertex3f(1.0f, 0.0f, -1.0f);  
        gl.glEnd();     
        
        
        
        /*
            Dibujo de dos cuadros, uno rojo y otro verde      
        */
        
        /*
        gl.glLoadIdentity();
        gl.glTranslatef(1.0f,1.0f,0.0f);

        gl.glTranslatef(this.despX, this.despY, this.despZ);        
        
        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            
            gl.glVertex3f(0.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, 0.0f);
            gl.glVertex3f(0.0f, 1.0f, 0.0f);  
        gl.glEnd();
        
        gl.glLoadIdentity();
        gl.glTranslatef(-2.0f,1.0f,0.0f);
        gl.glTranslatef(this.despX, this.despY, this.despZ);
        
        gl.glBegin(GL2.GL_QUADS);
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            
            gl.glVertex3f(0.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 0.0f, 0.0f);            
            gl.glVertex3f(1.0f, 1.0f, 0.0f);
            gl.glVertex3f(0.0f, 1.0f, 0.0f);  
        gl.glEnd();
        */
        
 
        this.rotacion += 5.0f;
        if (this.rotacion > 360) {
            this.rotacion = 0;
        }

        // System.out.printf("Rotacion %f \n", this.rotacion);
        gl.glFlush();

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
            case KeyEvent.VK_DOWN:
                this.despZ += 0.2f;
                break;
            case KeyEvent.VK_UP:
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
            case KeyEvent.VK_6:
                this.camX += 0.2f;
                break;
            case KeyEvent.VK_4:
                this.camX -= 0.2f;
                break;
            case KeyEvent.VK_Z:
                this.camZ += 0.2f;
                break;
            case KeyEvent.VK_A:
                this.camZ -= 0.2f;
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
