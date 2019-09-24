package com.company;

import java.awt.*;
import static com.company.GameWindow.*;

public class Repaint {
    public static void onRepaint(Graphics g){
        long currentTime=System.nanoTime();
        float delta_time = (currentTime - lastFrameTime)*0.000000001f;//штука которая вычисляет переменную без смс и регистрации (скорость падения)
        lastFrameTime=currentTime;

        drop_top=drop_top+drop_v*delta_time;//реверс
        drop_left=drop_left+(direction*drop_v)*delta_time;//реверс

        g.drawImage(Glitter_Background_Blue,0,0,null);
        g.drawImage(water,(int) drop_left/*drop_top*/,(int) drop_top,null);//перерисовка картинки по кардинатам (как только мы ставим в обе кардинаты динамическую переменную (delta_time) то она летит по диагонали)
        if(drop_top>game_window.getHeight()) {
            g.drawImage(GameOver, 520, 150, null);
            g.drawImage(Restart, 0, 0, null);
            end = true;
        }
        if(drop_left<=0.0 || drop_left+drop_width>game_window.getWidth()){
            if (direction==-1) direction=1;
            else direction=-1;
        }
        if(drawRecords){
            for(int i=0; i<recordslast.size();i++){
                g.drawString(recordslast.get(i),200, 25+25*i);
            }
        }
        nameEntry.isActive=end;
        nameEntry.update(g);
    }
}
