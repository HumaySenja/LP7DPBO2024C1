import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;
import java.util.ArrayList;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int frameWidth = 360;
    int frameHeight = 640;

    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    //Player
    int playerStartPosX = frameWidth/8;
    int playerStartPosY = frameHeight/2;
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;

    //Pipe
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight=512;
    ArrayList<Pipe> pipes;

    Timer gameloop;
    Timer pipesCooldown;

    boolean GameOver = false;
    double score = 0;

    int gravity = 1;
    public FlappyBird(){
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);
//        setBackground(Color.blue);

        backgroundImage = new ImageIcon(getClass().getResource("Assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("Assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("Assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("Assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        pipesCooldown = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCooldown.start();
        gameloop = new Timer(1000/60, this);
        gameloop.start();

    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g)
    {
        g.drawImage(backgroundImage,0,0,frameWidth,frameHeight,null);

        g.drawImage(player.getImage(), player.getPosX(),player.getPosY(), player.getWidth(), player.getHeight(), null);

        for(int i = 0; i<pipes.size();i++)
        {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(),pipe.getPosX(), pipe.getPosY(),pipe.getWidth(), pipe.getHeight(), null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (GameOver){
            g.drawString("Game Over: "+ String.valueOf((int)score), 10,35);
        }
        else{
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void placePipes()
    {
        int randompipeStartPosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingspace = frameHeight/4;
        Pipe upperPipe = new Pipe(pipeStartPosX, randompipeStartPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randompipeStartPosY +pipeHeight + openingspace,pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }
    public void move(){
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        for (int i = 0; i < pipes.size();i++)
        {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            if(!pipe.passed && player.getPosX() > pipe.getPosX() + pipe.getWidth()){
                pipe.passed = true;
                score += 0.5;
            }

            if (nabrak(player, pipe)){
                GameOver = true;
            }
        }

        if (player.getPosY() > frameHeight){
            GameOver = true;
        }
    }

    public boolean nabrak(Player a, Pipe b){
        return  a.getPosX() < b.getPosX() + b.getWidth() &&
                a.getPosX() + a.getWidth() > b.getPosX() &&
                a.getPosY() < b.getPosY() + b.getHeight() &&
                a.getPosY() + a.getHeight() > b.getPosY();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
        if (GameOver){
            pipesCooldown.stop();
            gameloop.stop();
        }
    }
    @Override
    public void keyTyped(KeyEvent e)
    {

    }
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            player.setVelocityY(-10);
        }
        if (e.getKeyCode() == KeyEvent.VK_R)
        {
            if(GameOver)
            {
                player.setPosY(frameHeight/2);
                player.setVelocityY(-0);
                pipes.clear();
                score = 0;
                GameOver = false;
                gameloop.start();
                pipesCooldown.start();
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
