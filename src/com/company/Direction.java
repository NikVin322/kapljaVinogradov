package com.company;

import static com.company.GameWindow.*;

public class Direction {
    public static int onDirection(){
        int rand = (int)(Math.random()*2+1);
        if(rand==2) direction = 1;
        else direction = -1;

        return direction;
    }
}
