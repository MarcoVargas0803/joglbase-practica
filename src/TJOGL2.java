/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;        
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

// Clases necesarias para el uso de texturas
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.jogamp.opengl.GLProfile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * JOGL 2.0 Program Template (GLCanvas)
 * This is a "Component" which can be added into a top-level "Container".
 * It also handles the OpenGL events to render graphics.
 */
@SuppressWarnings("serial")
public class TJOGL2 extends GLJPanel implements GLEventListener, KeyListener {
   // Define constants for the top-level container
   private static String TITLE = "JOGL 2.0 Setup (GLCanvas)";  // window's title
   private static final int CANVAS_WIDTH = 640;  // width of the drawable
   private static final int CANVAS_HEIGHT = 480; // height of the drawable
   private static final int FPS = 60; // animator's target frames per second
   private static final float factInc = 5.0f; // animator's target frames per second
   float fovy=45.0f;
   int   eje=0;
   float rotX=0.0f;
   float rotY=0.0f;
   float rotZ=0.0f;
   
   float posCamX = 0.0f;
   float posCamY = 0.0f;
   float posCamZ = 0.0f;
   
   Texture textura1;
 
   /** The entry main() method to setup the top-level container and animator */
   public static void main(String[] args) {
      // Run the GUI codes in the event-dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            // Create the OpenGL rendering canvas
            GLJPanel canvas = new TJOGL2();
            canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
 
            // Create a animator that drives canvas' display() at the specified FPS.
            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
 
            // Create the top-level container
            final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
            
            
            BorderLayout fl = new BorderLayout();
            frame.setLayout(fl);
                        
            frame.getContentPane().add(canvas, BorderLayout.CENTER);
            
            frame.addKeyListener((KeyListener)canvas);
            frame.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent e) {
                  // Use a dedicate thread to run the stop() to ensure that the
                  // animator stops before program exits.
                  new Thread() {
                     @Override
                     public void run() {
                        if (animator.isStarted()) animator.stop();
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
   
   Texture cargarTextura(String imageFile){
       Texture text1 = null;
       try {
            BufferedImage buffImage = ImageIO.read(new File(imageFile));           
            text1 = AWTTextureIO.newTexture(GLProfile.getDefault(),buffImage,false);
       } catch (IOException ioe){
           System.out.println("Problema al cargar el archivo "+imageFile);
       }  
       return text1;
   }
   // Setup OpenGL Graphics Renderer
 
   private GLU glu;  // for the GL Utility
   private GLUT glut;
 
   /** Constructor to setup the GUI for this Component */
   public TJOGL2() {
      this.addGLEventListener(this);
      this.addKeyListener(this);
   }
 
   // ------ Implement methods declared in GLEventListener ------
 
   /**
    * Called back immediately after the OpenGL context is initialized. Can be used
    * to perform one-time initialization. Run only once.
    */
   @Override
   public void init(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
      glu = new GLU();                        // get GL Utilities
      glut = new GLUT();
      gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set background (clear) color
      gl.glClearDepth(1.0f);      // set clear depth value to farthest
      gl.glEnable(GL_DEPTH_TEST); // enables depth testing
      gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
      
      float[] whiteMaterial={1.0f, 1.0f, 1.0f};
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, whiteMaterial,0);
      gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
 
      // ----- Your OpenGL initialization code here -----
      float[] ambientLight = { 0.5f, .5f, .5f,0.5f };  // weak RED ambient 
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0); 
      float[] diffuseLight = { .8f,.8f,.8f,0f };  // multicolor diffuse 
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0); 
      float[] specularLight = { 1f,1f,1f,0f };  // specular 
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight, 0);       
              
      gl.glEnable(GL2.GL_LIGHTING);      
      gl.glEnable(GL2.GL_LIGHT0);
      
      this.textura1 = this.cargarTextura("imagenes/manodigital.jpg");
      
      // Habilitar el uso de texturas
      gl.glEnable(GL2.GL_TEXTURE_2D);
      gl.glEnable(GL2.GL_BLEND);

      // Especificar la funcion de mezcla "adherencia" de textura
      gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);       
   }
 
   /**
    * Call-back handler for window re-size event. Also called when the drawable is
    * first set to visible.
    */
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
 
      if (height == 0) height = 1;   // prevent divide by zero
      float aspect = (float)width / height;
 
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
 
   /**
    * Called back by the animator to perform rendering.
    */
   @Override
   public void display(GLAutoDrawable drawable) {
       
        float aspect = (float)this.getWidth() / this.getHeight();
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

        float[] lightPos = { 0.0f,5.0f,10.0f,1 };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos, 0);

        gl.glLoadIdentity();  // reset the model-view matrix
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);


        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);

        glu.gluLookAt(0.0, 0.0, 5.0, this.posCamX, this.posCamY, this.posCamZ, 0.0, 1.0, 0.0);
        
        if (rotX<0) rotX=360-factInc;
        if (rotY<0) rotY=360-factInc;
        if (rotZ<0) rotZ=360-factInc;

        if (rotX>=360) rotX=0;
        if (rotY>=360) rotY=0;
        if (rotZ>=360) rotZ=0;

        // ----- Your OpenGL rendering code here (Render a white triangle for testing) -----

        gl.glRotatef(rotX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
      
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        /*
         * draw sphere in first row, first column diffuse reflection only; no
         * ambient or specular
         */
    //        gl.glPushMatrix();
        float no_mat[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float mat_ambient[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_ambient_color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float no_shininess[] = { 0.0f };
        float low_shininess[] = { 5.0f };
        float high_shininess[] = { 100.0f };
        float mat_emission[] = { 0.5f, 0.5f, 0.5f, 0.0f };

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);

        // Asociar la textura con el canvas
        this.textura1.bind(gl);
        this.textura1.enable(gl); 
                
        //glut.glutSolidTeapot(1);
        
        this.drawCube(gl);
        //this.drawCubeUVWmapped(gl);
        
        this.textura1.disable(gl);
        
        //glut.glutSolidSphere(1, 50, 50);
        //gl.glTranslatef(2.0f, 0.0f, 0.0f);
        
        
      
        gl.glFlush();
        
        this.rotY += 0.5f;
      
   }

    void drawCube(GL2 gl){     

        gl.glBegin(GL2.GL_QUADS);
        // Front Face
        
        gl.glTexCoord2f(0.5f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(0.5f, 1.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Top Left
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Top Right
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right
        
       
        // Back Face
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Left
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Left
        // Top Face
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right
        // Bottom Face
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Top Left
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right
        // Right face
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Right
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Left
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left
        // Left Face
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Right
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left
        
       
        gl.glEnd();
    }

    /**
     * Render 2 unit cube with texture coordinates, mapped to 8 separate faces
     * in texture image.
     *              ___________
     *             |   | T |   |
     *             |___|___|___|
     *             | L | F | R |
     *             |___|___|___|
     *             |   | B | BB|
     *             |___|___|___|
     */
    public static void drawCubeUVWmapped(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        // Left Face
        gl.glTexCoord2f(0.0f, .33f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left
        gl.glTexCoord2f(.33f, .33f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right
        gl.glTexCoord2f(.33f, .66f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Right
        gl.glTexCoord2f(0.0f, .66f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left
        // Front Face
        gl.glTexCoord2f(.33f, .33f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(.66f, .33f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Right
        gl.glTexCoord2f(.66f, .66f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Right
        gl.glTexCoord2f(.33f, .66f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Left
        // Right face
        gl.glTexCoord2f(.66f, .33f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(1.0f, .33f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Right
        gl.glTexCoord2f(1.0f, .66f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(.66f, .66f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Left
        // Top Face
        gl.glTexCoord2f(.33f, .66f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(.66f, .66f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right
        gl.glTexCoord2f(.66f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(.33f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left
        // Bottom Face
        gl.glTexCoord2f(.66f, .33f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left
        gl.glTexCoord2f(.33f, .33f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right
        gl.glTexCoord2f(.33f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(.66f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Top Left
        // Back Face
        gl.glTexCoord2f(.66f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Left
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right
        gl.glTexCoord2f(1.0f, .33f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Right
        gl.glTexCoord2f(.66f, .33f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Left
        gl.glEnd();
    }
   
    void drawPyramid(float x,float y,float z,float w,float h,GL2 gl){
      float mw=w/2;
      float mh=h/2;
      float xi,xd,ys,yi,zf,zp;
      
      xi=x-mw;
      xd=x+mw;
              
      yi=y-mh;
      ys=y+mh;
              
      zf=z+mw;
      zp=z-mw;              
                   
      gl.glBegin(GL_TRIANGLES); // draw using triangles
         gl.glColor3f(1.0f,0,0);
         gl.glVertex3f(xi, yi, zf);
         gl.glColor3f(0,1.0f,0);
         gl.glVertex3f(x, ys, z);                 
         gl.glColor3f(0,0,1.0f);
         gl.glVertex3f(xd, yi, zf);
      gl.glEnd();
      
      gl.glBegin(GL_TRIANGLES); // draw using triangles
         gl.glColor3f(0.0f,0,1.0f);
         gl.glVertex3f(xd,yi,zf);
         gl.glColor3f(0,1.0f,0);
         gl.glVertex3f(x, ys, z);
         gl.glColor3f(1.0f,0,0);
         gl.glVertex3f(xd, yi,zp);
      gl.glEnd();
      gl.glBegin(GL_TRIANGLES); // draw using triangles
         gl.glColor3f(1.0f,0.0f,0.0f);
         gl.glVertex3f(xd, yi, zp);
         gl.glColor3f(0,1.0f,0);
         gl.glVertex3f(x, ys,z);
         gl.glColor3f(0.0f,0,1.0f);
         gl.glVertex3f(xi, yi, zp);
      gl.glEnd();
      gl.glBegin(GL_TRIANGLES); // draw using triangles
         gl.glColor3f(0.0f,0,1.0f);
         gl.glVertex3f(xi,yi, zp);
         gl.glColor3f(0,1.0f,0);
         gl.glVertex3f(x, ys, z);
         gl.glColor3f(1.0f,0,0.0f);
         gl.glVertex3f(xi, yi, zf);
      gl.glEnd();
      gl.glBegin(GL_QUADS ); // draw using triangles
         gl.glColor3f(1.0f,0,0);
         gl.glVertex3f(xi, yi, zf);
         gl.glColor3f(0,0.0f,1.0f);
         gl.glVertex3f(xd, yi, zf);
         gl.glColor3f(1.0f,0,0.0f);
         gl.glVertex3f(xd, yi, zp);
         gl.glColor3f(0.0f,0.0f,1.0f);
         gl.glVertex3f(xi, yi, zp);
      gl.glEnd();          
   }

   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int cod = e.getKeyCode();
        switch(cod){
            case KeyEvent.VK_UP:
                posCamY +=0.5f;
                break;
            case KeyEvent.VK_DOWN:    
                posCamY -=0.5f;
                break;                
            case KeyEvent.VK_LEFT:
                posCamX -=0.5f;
                break;                
            case KeyEvent.VK_RIGHT:                
                posCamX +=0.5f;                
                break;                
            case KeyEvent.VK_F1:
                    fovy+=factInc; break;
            case KeyEvent.VK_F2:
                    fovy-=factInc; break;   
            case KeyEvent.VK_3:
                switch(eje){
                    case 1:
                        rotX+=factInc; break;
                    case 2:
                        rotY+=factInc; break;
                    case 3:
                        rotZ+=factInc; break;                        
                }
                break;
            case KeyEvent.VK_4:
                switch(eje){
                    case 1:
                        rotX-=factInc; break;
                    case 2:
                        rotY-=factInc; break;
                    case 3:
                        rotZ-=factInc; break;                        
                } 
                break;

            case KeyEvent.VK_X:
                    eje=1;
                    break;                               
            case KeyEvent.VK_Y:
                    eje=2;
                    break;                   
            case KeyEvent.VK_Z:
                    eje=3;
                    break;                                   
        }
                        
        System.out.println("Typed="+e.getKeyCode()+", fovy="+fovy+", eje="+eje+" ,rotX="+rotX+" ,rotY="+rotY+" ,rotZ="+rotZ);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}