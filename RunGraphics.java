import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class RunGraphics {

    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 800;

    public RunGraphics() {

    	JFrame frame = new JFrame("Space Invaders");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setResizable(false);// the event that triggers the end of the program
        frame.setPreferredSize(frame.getSize());
        frame.add(new showGraphics(frame.getSize()));
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        new RunGraphics();
    }

    public static class showGraphics extends JPanel implements Runnable, KeyListener {

        private Thread animator;
        int xAxis = 30;
        int yAxis = 30;
        int aliencount = 21;
        int gameoverline = 600;
        Ship s;
        Alien[][] a;
        Shot psh;
        Shot ash;
        boolean running = false;
 
        public showGraphics(Dimension dimension) {
            start();
            addKeyListener ( this ) ; 
            setFocusable(true);
            if (animator == null) {
                animator = new Thread(this);
                animator.start();
            }
            setDoubleBuffered(true);
        }
        @Override
        
        public void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            Dimension d = getSize();
            g2.setColor(new Color(1,1,1));
            g2.fillRect(0, 0, d.width, d.height);
            
            if(running==true) {
            	moveAlien();
            	s.move(0);
            	psh.move(0);           	
            }else {
            	g2.setColor(Color.white);
            	g.setFont(new Font("Ink Free",Font.BOLD,45));
        		FontMetrics metrics = getFontMetrics(g.getFont());
            	g.drawString("Press Enter to Start to the Game", (SCREEN_WIDTH - metrics.stringWidth("Press Enter to Start the Game"))/2, SCREEN_HEIGHT/2);
            }
            
            psh.draw(g2);
            s.draw(g2);
            hitCheck();

            for(int i = 0; i<a.length; i++){
                for (int j = 0; j<a[0].length; j++){
                    if(a[i][j].isVis)
                        a[i][j].draw(g2);
                }
            }
        }
        public void start() {
        	xAxis = 30;
        	yAxis= 30;
        	a = new Alien[3][7];
        	running = false;
        	s = new Ship((SCREEN_WIDTH/2)-50,650,90,90,30,"player.png");
            psh = new Shot((SCREEN_WIDTH/2)-10,650,7,25,20,"pshot.png");
            int x = 10;
            int y = 20;
            for(int i = 0; i<a.length; i++){
                for (int j = 0; j<a[0].length; j++){
                    a[i][j] = new Alien(x,y,50,50,15,"alien.png");
                    x += 60;
                }
                y += 90;
                x = 10;             
            }         
        }

        public void hitCheck(){

            for(int i = 0; i<a.length; i++){
                for (int j = 0; j<a[0].length; j++){
                	
                    if ((a[i][j].isVis) && (psh.getX() + psh.getWidth() >= a[i][j].getX()) && (psh.getX() <= a[i][j].getX() + a[i][j].getWidth()) && (psh.getY() + psh.getHeight() >= (a[i][j].getY())) && (psh.getY() <= a[i][j].getY() + a[i][j].getHeight())) {
                    	
                            a[i][j].isVis=false; 
                            psh.x = -30;
                            aliencount = aliencount - 1 ;
                            if(aliencount == 0) {
                            	running = false;
                            	String YouWon = "You Won.";
                            	JOptionPane.showMessageDialog(this, YouWon);
                            	System.exit(0);
                            	 
                                }                 
                    }
                }   
            }

        }

        public void moveAlien(){
            for(int i = 0; i<a.length; i++){
                for (int j = 0; j<a[0].length; j++){
                    if(a[i][j].moveLeft)
                        a[i][j].setX(a[i][j].getX()-a[i][j].getSpeed());

                    if(a[i][j].moveRight){
                        a[i][j].setX(a[i][j].getX()+a[i][j].getSpeed());
                    }
                }                
            }           
            for(int i = 0; i<a.length; i++){
                for (int j = 0; j<a[0].length; j++){

                    if(a[i][j].getX()>SCREEN_WIDTH){
                        moveLeftRight(1);
                        break;
                    }

                    if(a[i][j].getX()<0){
                        moveLeftRight(2);
                        break;
                    }
                    if((gameoverline <= a[i][j].getY())&&(a[i][j].isVis)) {
                     	running = false;
                     	String GameOver = "Game Over.";
                     	JOptionPane.showMessageDialog(this, GameOver);
                     	System.exit(0);	
                	 }
                }               
            }           
        }

        public void moveLeftRight(int d){ //d is a parameter
            for(int  i= 0; i<a.length; i++){
                for (int j = 0; j<a[0].length; j++){
                    if(d==1){
                        a[i][j].moveLeft=true;
                        a[i][j].moveRight=false;
                    }else{
                        a[i][j].moveLeft=false;
                        a[i][j].moveRight=true;
                    }

                    a[i][j].setY(a[i][j].getY()+15);

                }               
            }
        }
        public void keyTyped ( KeyEvent e ){  

        }  

        public void keyPressed ( KeyEvent e){ 
            int k = e.getKeyCode();
            
            if(k == 10) {
            	start();
            	running = true;
            }
            s.setLeftRight(k);
            
            if(k==32)  {
            	psh.goUp=true;
            	for (int i = 0 ; i<6 ; i++ ){
            		 
                	psh.setX(s.getX() + (s.getWidth()/2));
                    psh.setY(s.getY());
            	}                
            }            
        }  

        public void keyReleased ( KeyEvent e ){  
            int k = e.getKeyCode();
            s.stop();
        } 
        public void run() {
        	    int animationDelay = 100;
                long time = System.currentTimeMillis();
                while (true) {
                    repaint();
                    try {
                        time += animationDelay;
                        Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
                    } catch (InterruptedException e) {
                       System.out.println(e);
                    }    		
        	}
        }
    }
}
