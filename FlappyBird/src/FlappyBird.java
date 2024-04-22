import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int frameWidth =   360;
    int frameHeigth = 640;

    //image attribut
    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;
    //buat player
    int playerStartPostX = frameWidth/8;
    int playerStartPosY = frameHeigth/2;
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;

    //set waktu
    Timer gameLoop;
    Timer pipesCoolddown;
    int gravity = 1;

    //pipe atribut
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;
    double score = 0;
    boolean gameOver = false;
    public FlappyBird(){
        setPreferredSize(new Dimension(frameWidth, frameHeigth));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load image
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        //save player
        player = new Player(playerStartPostX, playerStartPosY, playerWidth, playerHeight, birdImage);

        //save pipe
        pipes= new ArrayList<Pipe>();

        //pipes cooldown
        pipesCoolddown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCoolddown.start();

        //waktu
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    //temPt pip
    public void placePipes(){
        int randomPipePosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeigth/4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPipePosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, (randomPipePosY + openingSpace + pipeHeight), pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);


    }
    public  void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeigth, null);

        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        //nambah pipe
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
        //getscore
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);

        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){

        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));


        //pipe

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            if (!pipe.passed && player.getPosX() > pipe.getPosX() + pipe.getWidth()){
                score += 0.5;
                pipe.passed = true;
            }
            if(collision(player, pipe)){
                gameOver = true;
            }
        }
        if (player.getPosY() > frameHeigth){
            gameOver = true;
        }
    }

    boolean collision(Player a, Pipe b){
        return a.getPosX() < b.getPosX() + b.getWidth() &&   //a's top left corner doesn't reach b's top right corner
                a.getPosX() + a.getWidth() > b.getPosX() &&   //a's top right corner passes b's top left corner
                a.getPosY() < b.getPosY() + b.getHeight() &&  //a's top left corner doesn't reach b's bottom left corner
                a.getPosY() + a.getHeight() > b.getPosY();    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
        if (gameOver ){
            pipesCoolddown.stop();
            gameLoop.stop();
        }
    }



    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // System.out.println("JUMP!");
            player.setVelocityY(-9);
        }
        else if (e.getKeyCode() == KeyEvent.VK_R ||  gameOver ) {
            //restart game by resetting conditions
            player.setPosY(player.getPosY());
            player.setVelocityY(0);
            pipes.clear();
            gameOver = false;
            score = 0;
            gameLoop.start();
            pipesCoolddown.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
