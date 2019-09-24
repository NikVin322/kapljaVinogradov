package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Random;



public class GameWindow extends JFrame {


    public static GameWindow game_window;
    public static long lastFrameTime;
    public static Image water;
    public static Image Glitter_Background_Blue;
    public static Image GameOver;
    public static Image Restart;
    public static float drop_left=200;
    public static float drop_top=-100;//кордината спауна капли до рандома по оси Y
    public static float drop_v=200;
    public static int score=0;
    public static boolean end;
    public static float drop_width=100;
    public static float drop_height=132;
    public static boolean pause = false;
    public static float drop_speed_safe = 0;
    public static double mousecordX=0;
    public static double mousecordY=0;
    public static int direction=-1;

    public static Entry nameEntry;
    public static DataBase db;
    public static Repaint rep;
    public static Direction dir;


    public static boolean isRecorded=false;
    public static boolean drawRecords=false;
    public static ArrayList<String> recordslast=new ArrayList<String>();


    public static void main(String[] args) throws IOException {
        /*try{
            String url = "jdbc:mysql://localhost/gamedrop?useLegacyDatetimeCode=false&serverTimezone=Europe/Helsinki";
            String username="root";
            String password="opilane";

            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)){
                System.out.println("Connection to GameDrop DB succesfull!");
            }
        }
        catch(Exception ex){
            System.out.println("Conn. failed");
            System.out.println(ex);
        }*/
        db=new DataBase("jdbc:mysql://localhost/gamedrop?useLegacyDatetimeCode=false&serverTimezone=Europe/Helsinki","root", "opilane");
        db.init();
        Glitter_Background_Blue= ImageIO.read(GameWindow.class.getResourceAsStream("Glitter_Background_Blue.jpg"));
        water= ImageIO.read(GameWindow.class.getResourceAsStream("water.png")).getScaledInstance((int) drop_width, (int)drop_height, Image.SCALE_DEFAULT);
        GameOver= ImageIO.read(GameWindow.class.getResourceAsStream("GameOver.png")).getScaledInstance(400,400, Image.SCALE_DEFAULT);
        Restart= ImageIO.read(GameWindow.class.getResourceAsStream("img_302284.png")).getScaledInstance(100,100, Image.SCALE_DEFAULT);
        game_window=new GameWindow();
        game_window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game_window.setLocation(200,100);
        game_window.setSize(1500,800);
        game_window.setResizable(false);
        /*pause=false;*/
        lastFrameTime=System.nanoTime();
        GameField game_field= new GameField();
        //onDirection();

        game_field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() ==MouseEvent.BUTTON3){
                    if(pause) {
                        pause=false;
                        drop_v=drop_speed_safe;

                        try{
                            Robot r=new Robot();
                            r.mouseMove((int) mousecordX,(int) mousecordY);
                        }
                        catch (AWTException ee){
                        }
                    }
                    else{
                        drop_speed_safe=drop_v;
                        drop_v=0;
                        mousecordX=MouseInfo.getPointerInfo().getLocation().getX();
                        mousecordY=MouseInfo.getPointerInfo().getLocation().getY();
                        pause=true;
                    }
                }
                if(pause) return;

                int x = e.getX();
                int y = e.getY();

                float drop_right=  drop_left + water.getWidth(null);
                float drop_bottom = drop_top + water.getHeight(null);
                boolean is_drop = x >= drop_left && x <= drop_right && y >=drop_top && y <= drop_bottom;
                if(is_drop){
                    if(!(drop_height <= 50 && drop_width <= 50)){
                        drop_width=drop_width-1;
                        drop_height=drop_height-1;
                        try {
                            dropResize();
                        }
                        catch (IOException ioe){
                        }
                    }
                    drop_top=-100;
                    drop_left = (int)(Math.random()*(game_field.getWidth()-water.getWidth(null)));
                    drop_v=drop_v+20;
                    score++;
                    dir.onDirection();
                    game_window.setTitle("Score: " + score);
                }
                if(end){
                    boolean isRestart = x>=0 && x<=0 + Restart.getHeight(null)
                            && y>=0 && y<=0 + Restart.getHeight(null);
                    if(isRestart){
                        end=false;
                        score=0;
                        game_window.setTitle("Score: " + score);
                        drop_top=-100;
                        drop_left= (int)(Math.random()*(game_field.getWidth()-water.getWidth(null)));
                        drop_v=200;
                        drop_width=100;
                        drop_height=132;
                        isRecorded=false;
                        drawRecords=false;
                    }
                }
            }
        });
        nameEntry= new Entry();
        game_window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println("keyPress");
                nameEntry.keyPress(e);
                if(nameEntry.isActive && !isRecorded){
                    if(e.getKeyCode()==KeyEvent.VK_ENTER){
                        db.addRecord(nameEntry.text, score);
                        isRecorded=true;
                        recordslast=db.getRecords();
                        drawRecords=true;
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });

        game_window.add(game_field);
        game_window.setVisible(true);
    }
    private static void dropResize() throws IOException {
        water= ImageIO.read(GameWindow.class.getResourceAsStream("water.png")).getScaledInstance((int) drop_width, (int)drop_height, Image.SCALE_DEFAULT);
    }

    private static  class GameField extends JPanel{
    @Override
        protected void  paintComponent(Graphics g){
        super.paintComponent(g);
        rep.onRepaint(g);
        repaint();
    }
    }
}
