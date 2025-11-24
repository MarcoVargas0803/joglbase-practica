
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.awt.GLJPanel;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_FLAT;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Light extends GLJPanel implements GLEventListener, KeyListener {

    private static String TITLE = "Aplicacion de iluminacion";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second
    private static final float factInc = 5.0f; // animator's target frames per second
    private float fovy = 45.0f;

    //////////////// Variables /////////////////////////
    // Referencias de rotacion
    float rotX = 90.0f;
    float rotY = 0.0f;
    float rotZ = 0.0f;

    // Posicion de la luz.
    float lightX = 1f;
    float lightY = 1f;
    float lightZ = 2f;
    float dLight = 0.05f;

    // Posicion de la camara
    float camX = 1.0f;
    float camY = 0.0f;
    float camZ = 4.0f;

    // Material y luces.       R    G     B      A
    final float ambient[] = {0.3f, 0.3f, 0.3f, 0.3f};
    
    final float position[] = {lightX, lightY, lightZ, 1.0f};

    //                           R    G      B    A
    final float[] colorBlack = {0.0f, 0.0f, 0.0f, 1.0f};
    final float[] colorWhite = {1.0f, 1.0f, 1.0f, 1.0f};
    final float[] colorGray = {0.4f, 0.4f, 0.4f, 1.0f};
    final float[] colorDarkGray = {0.2f, 0.2f, 0.2f, 1.0f};
    final float[] colorRed = {1.0f, 0.0f, 0.0f, 1.0f};
    final float[] colorGreen = {0.5f, 1.0f, 0.5f, 1.0f};
    final float[] colorBlue = {0.0f, 0.0f, 0.6f, 1.0f};
    final float[] colorYellow = {1.0f, 1.0f, 0.0f, 1.0f};
    final float[] colorLightYellow = {.5f, .5f, 0.0f, 1.0f};

    //       R     G     B     A          
    final float mat_diffuse[] = {0.6f, 0.6f, 0.6f, 1.0f};
    final float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    final float mat_shininess[] = {50.0f};
    private float aspect;

    ///////////////// Funciones /////////////////////////
    public Light() {
        this.addGLEventListener(this);
        this.addKeyListener(this);
    }

    /////////////// Define Luz y Material /////////
    private GLU glu;  // para las herramientas GL (GL Utilities)
    private GLUT glut;

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Establece un material por default.
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f); // set background (clear) color

        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do

        // 1. First Switch the lights on.
        gl.glEnable(GL2.GL_LIGHTING); 
       
        // 2. Alguna luz de ambiente global
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, this.ambient, 0); 
        
        // 3. Establecer el modelo de sombreado
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting  

        // 4. Establecer caracteristicas de la luz y encenderlas - Light 0.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0 );
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, colorBlue, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, colorBlue, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
        
        gl.glEnable(GL2.GL_LIGHT0);

        this.initPosition(gl);

        glu = new GLU();                        // get GL Utilities
        glut = new GLUT();
    }

    public void initPosition(GL2 gl) {
        float posLight1[] = {lightX, lightY, lightZ, 1.0f};
        float spotDirection1[] = {0.0f, 0.0f, 0.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLight1, 0);
    }

    /////////////// Move light ////////////////////////////
    // Move light 0.
    public void moveLightX(boolean positivDirection) {
        lightX += positivDirection ? dLight : -dLight;
    }

    public void moveLightY(boolean positivDirection) {
        lightY += positivDirection ? dLight : -dLight;
    }

    public void moveLightZ(boolean positivDirection) {
        lightZ += positivDirection ? dLight : -dLight;
    }

    public void animate(GL2 gl, GLU glu, GLUT glut) {
        float posLight0[] = {lightX, lightY, lightZ, 1.f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLight0, 0);
        drawLight(gl, glu, glut);
        //lightX += 0.003f;
        //lightY += 0.003f;
    }

    /////////////// Define Material /////////////////////
    public void setLightSphereMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorBlue, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorBlue, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorBlue, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 100);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlue, 0);
        //gl.glMaterialfv( face, GL.GL_EMISSION, colorLightYellow , 0 );
        //gl.glMaterialfv( face, GL.GL_EMISSION, colorBlack , 0 );
    }

    public void setSomeMaterial(GL2 gl, int face, float rgba[], int offset) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, rgba, offset);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, rgba, offset);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, rgba, offset);
        gl.glMaterialfv(face, GL2.GL_SHININESS, rgba, offset);
        gl.glMateriali(face, GL2.GL_SHININESS, 4);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(face, GL2.GL_SHININESS, mat_shininess, 0);
    }

    public void setSomeWhiteMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorWhite, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorWhite, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorWhite, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 4);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeGrayMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorGray, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorGray, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorGray, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 4);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeDarkGrayMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorDarkGray, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorDarkGray, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorDarkGray, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 4);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeYellowMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorBlack, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorLightYellow, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorYellow, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 5);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeBlueMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorBlue, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorBlue, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorBlue, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 4);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeRedMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorDarkGray, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorRed, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorWhite, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 50);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorDarkGray, 0);
    }

    public void setSomeGreenMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorGray, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorGreen, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorGreen, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 120);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorDarkGray, 0);
    }

    /////////////////// dibujos /////////////////////////
    ///////////////// Dibuja una Esfera con Luz ///////////////
    public void drawLight(GL2 gl, GLU glu, GLUT glut) {
        setLightSphereMaterial(gl, GL.GL_FRONT);
        gl.glPushMatrix();
        {
            gl.glTranslatef(lightX, lightY, lightZ);
            glut.glutSolidSphere(0.1f, 20, 20);
        }
        gl.glPopMatrix();
    }

    public void drawFigure(GL2 gl, GLUT glut) {
        //gl.glRotatef(rotX,1.0f, 0.0f, 0.0f);
        this.setSomeGreenMaterial(gl, GL.GL_FRONT);
        glut.glutSolidTeapot(1.0f, true);

        this.setSomeRedMaterial(gl, GL.GL_FRONT);
        gl.glTranslatef(2.0f, 1.0f, -1.0f);
        glut.glutSolidTorus(0.5, 1, 20, 20);
        
        //glut.glutSolidOctahedron();

        /*
        String string = "Hola Mundo";              
        float width = 0.0f;
        
        if (string!=null && string.length() > 0) {
            width = glut.glutStrokeLength(GLUT.STROKE_ROMAN, string);
            //gl.glTranslatef(-width / 2f, 0, 0);
            // Render The Text
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, c);
            }
        } 
        */
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {

        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context

        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();
        glu.gluPerspective(fovy, aspect, 0.1, 20.0);
        glu.gluLookAt(this.camX, this.camY, this.camZ, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();  // reset the model-view matrix

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        //this.setSomeGreenMaterial( gl, GL.GL_FRONT );      
        //this.drawFigure(gl, glut);
        gl.glTranslatef(-2.0f, 0.0f, -2.0f);
        this.drawFigure(gl, glut);

        this.animate(gl, this.glu, this.glut);

        gl.glFlush();

    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) {
            height = 1;   // prevent divide by zero
        }
        aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(fovy, aspect, 0.1, 50.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset

    }

    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the OpenGL rendering canvas
                GLJPanel canvas = new Light();
                canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                // Create the top-level container
                final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame

                FlowLayout fl = new FlowLayout();
                frame.setLayout(fl);
                
                frame.getContentPane().add(canvas);

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

                frame.setTitle(TITLE);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();
        //  lightX, lightY, lightZ
        System.out.println("codigo presionado = " + codigo);

        switch (codigo) {
            case KeyEvent.VK_DOWN:
                this.moveLightY(false);
                break;
            case KeyEvent.VK_UP:
                this.moveLightY(true);
                break;
            case KeyEvent.VK_RIGHT:
                this.moveLightX(true);
                break;
            case KeyEvent.VK_LEFT:
                this.moveLightX(false);
                break;
            case KeyEvent.VK_W:
                this.moveLightZ(false);
                break;
            case KeyEvent.VK_S:
                this.moveLightZ(true);
                break;

            case KeyEvent.VK_I:
                this.camY += 0.2f;
                break;
            case KeyEvent.VK_K:
                this.camY -= 0.2f;
                break;
            case KeyEvent.VK_L:
                this.camX += 0.2f;
                break;
            case KeyEvent.VK_J:
                this.camX -= 0.2f;
                break;
            case KeyEvent.VK_Z:
                this.camZ += 0.2f;
                break;
            case KeyEvent.VK_A:
                this.camZ -= 0.2f;
                break;
        }
        System.out.println("rotX = " + rotX);
        System.out.println("Posicion de luz "+lightX+","+lightY+","+lightZ);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
