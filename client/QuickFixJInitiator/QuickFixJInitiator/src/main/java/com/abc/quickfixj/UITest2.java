package com.abc.quickfixj;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;


/**
 * Created by darshanas on 11/27/2017.
 */
public class UITest2 {
    public static void main(String[] args) {
        DefaultTerminalFactory dtf = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = dtf.createScreen();
            screen.startScreen();
            screen.setCharacter(10, 5, new TextCharacter('!', TextColor.ANSI.RED, TextColor.ANSI.GREEN));
            screen.refresh();
            Thread.sleep(1500);
            TextGraphics textGraphics = screen.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(12, 5, "Hello Lanterna!");
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
