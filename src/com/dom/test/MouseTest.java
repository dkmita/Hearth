package com.dom.test;

import java.awt.*;
import java.awt.event.InputEvent;


public class MouseTest {

    public static void main(String[] args) throws Exception {

        Robot robot = new Robot();

        while( true ) {

            PointerInfo info = MouseInfo.getPointerInfo();

            int x = info.getLocation().x;

            int y = info.getLocation().y;

            System.out.println( x + ", " + y );

            synchronized (Thread.currentThread()) {
                Thread.currentThread().wait(250);
            }

            if( x > 2000 ) {

                break;
            }
        }

        // SET THE MOUSE X Y POSITION
        robot.mouseMove(300, 550);

        robot.mousePress(InputEvent.BUTTON3_MASK);

        robot.mouseMove(400, 550);

        robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }
}
