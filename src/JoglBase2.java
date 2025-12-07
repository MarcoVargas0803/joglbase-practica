
/**
 *
 * @author gmendez
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

/**
 * JoglBase Programa Plantilla (GLCanvas)
 */
@SuppressWarnings("serial")
public class JoglBase2 {
    // Define constants for the top-level container

    private static final String TITLE = "Plantilla Base java open gl";  // window's title
    private final int CANVAS_WIDTH = 640;  // width of the drawable
    private final int CANVAS_HEIGHT = 480; // height of the drawable
    private final int FPS = 30; // animator's target frames per second

    float rotacion = 0.0f;
    float despl = 0.0f;
    float despX = 1.0f;
    float despY = 1.0f;
    float despZ = 8.0f;

    GLJPanel canvas;
    Pintor pintor;
    FPSAnimator animator;

    MiKeyListener keylistener;

    JFrame frame;
    JPanel panel1;
    BorderLayout bl;

    /**
     * The entry main() method to setup the top-level container and animator
     */
    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the OpenGL rendering canvas
                JoglBase2 jb = new JoglBase2();
                jb.start();
            }
        });
    }

    /**
     * Constructor to setup the GUI for this Component
     */
    public JoglBase2() {
        this.canvas = new GLJPanel();
        this.pintor = new Pintor(this);

        this.keylistener = new MiKeyListener(this);

        this.canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        this.canvas.addGLEventListener(pintor);

        this.animator = new FPSAnimator(canvas, this.FPS, true);

        // Create the top-level container
        frame = new JFrame(); // Swing's JFrame or AWT's Frame        
        frame.setTitle(TITLE);

        this.frame.addKeyListener(keylistener);

        bl = new BorderLayout();

        frame.setLayout(bl);

        JPanel  panel1 = new JPanel();
        JButton btnIni = new JButton("Iniciar");
        JButton btnDet = new JButton("Detener");

        btnIni.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animator.start();
            }
        });

        btnDet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animator.stop();
            }
        });

        panel1.add(btnIni);
        panel1.add(btnDet);

        frame.getContentPane().add(panel1, BorderLayout.NORTH);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev) {
                Component c = (Component) ev.getSource();
                // Get new size
                Dimension newSize = c.getSize();
                panel1.setSize(newSize);
                canvas.setSize(newSize);
            }
        });

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
    }

    void start() {

        frame.pack();
        frame.setVisible(true);
        animator.start(); // start the animation loop

    }




}

class Pintor implements GLEventListener {

    private final float factInc = 5.0f; // animator's target frames per second
    private float fovy = 45.0f;

    JoglBase2 padre;

    private final GLU glu;  // for the GL Utility
    private final GLUT glut;

    public Pintor(JoglBase2 p) {
        this.padre = p;
        this.glu = new GLU();                        // get GL Utilities
        this.glut = new GLUT();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();      // get the OpenGL graphics context

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set background (clear) color

        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // reset the model-view matrix
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        glu.gluLookAt(this.padre.despX, // Posicion de la camara
                this.padre.despY,
                this.padre.despZ,
                0.0, // Direccion X hacia donde observa la cámara
                0.0, // Direccion Y hacia donde observa la cámara
                0.0, // Direccion Z hacia donde observa la cámara
                0.0, // Orientación de la cámara en X 
                1.0, // Orientación de la cámara en Y
                0.0);// Orientación de la cámara en Z

        gl.glColor3f(0.0f, 0.0f, 1.0f);
//                    R     G     B
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(-10.0f, 0.0f, 0.0f);
        gl.glVertex3f(10.0f, 0.0f, 0.0f);
        gl.glEnd();

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(0.0f, -10.0f, 0.0f);
        gl.glVertex3f(0.0f, 10.0f, 0.0f);
        gl.glEnd();

        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(0.0f, 0.0f, -10.0f);
        gl.glVertex3f(0.0f, 0.0f, 10.0f);
        gl.glEnd();

        gl.glLoadIdentity();

        //glu.gluLookAt(2.0f, 2.0f, 6.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        glu.gluLookAt(this.padre.despX, // Posicion de la camara
                this.padre.despY,
                this.padre.despZ,
                0.0, // Direccion X hacia donde observa la cámara
                0.0, // Direccion Y hacia donde observa la cámara
                0.0, // Direccion Z hacia donde observa la cámara
                0.0, // Orientación de la cámara en X 
                1.0, // Orientación de la cámara en Y
                0.0);// Orientación de la cámara en Z

        gl.glTranslatef(1, 1, 1);
        gl.glRotatef(this.padre.rotacion, 0.0f, 1.0f, 0.0f);
        //gl.glTranslatef(1, 1, 1);
        //gl.glTranslatef(this.padre.despX, this.padre.despY, this.padre.despZ);

        //gl.glColor3f(1.0f, 0.0f, 0.0f);
        //glut.glutSolidTorus(0.5, 1, 20, 20);
        //glut.glutSolidTeapot(1);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(1.0f, 0.0f, -1.0f);
        gl.glVertex3f(0.0f, 0.0f, -1.0f);
        gl.glEnd();

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex3f(0.0f, 0.0f, -1.0f);
        gl.glVertex3f(1.0f, 0.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(0.0f, 1.0f, -1.0f);
        gl.glEnd();

        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, 1.0f, -1.0f);
        gl.glVertex3f(0.0f, 0.0f, -1.0f);
        gl.glEnd();

        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 0.0f, -1.0f);
        gl.glEnd();

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glEnd();

        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(0.0f, 1.0f, -1.0f);
        gl.glEnd();

        this.padre.rotacion += 5.0f;
        if (this.padre.rotacion > 360) {
            this.padre.rotacion = 0;
        }

        System.out.printf("Rotacion %f \n", this.padre.rotacion);

        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) {
            height = 1;   // prevent divide by zero
        }
        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(fovy, aspect, 1.0, 20.0); // fovy, aspect, zNear, zFar

    }

}

class MiKeyListener implements KeyListener {

    JoglBase2 padre;

    MiKeyListener(JoglBase2 p) {
        this.padre = p;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();

        switch (codigo) {
            case KeyEvent.VK_LEFT:
                this.padre.despX -= 0.2f;
                break;
            case KeyEvent.VK_RIGHT:
                this.padre.despX += 0.2f;
                break;
            case KeyEvent.VK_DOWN:
                this.padre.despZ += 0.2f;
                break;
            case KeyEvent.VK_UP:
                this.padre.despZ -= 0.2f;
                break;

            case KeyEvent.VK_A:
                this.padre.despY += 0.2f;
                break;
            case KeyEvent.VK_Z:
                this.padre.despY -= 0.2f;
                break;

            case KeyEvent.VK_T:
                this.padre.rotacion -= 5.0f;
                break;
                
            case KeyEvent.VK_R:
                this.padre.rotacion += 5.0f;
                break;
        }
        System.out.println("despX =" + this.padre.despX);
        System.out.println("despY =" + this.padre.despY);
        System.out.println("despZ =" + this.padre.despZ);
    }

}
