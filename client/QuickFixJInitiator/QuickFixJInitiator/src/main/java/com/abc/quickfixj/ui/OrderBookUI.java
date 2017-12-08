package com.abc.quickfixj.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

/**
 * Created by darshanas on 11/27/2017.
 */
public class OrderBookUI {

    public static void main(String[] args) {
        DefaultTerminalFactory dtf = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = dtf.createScreen();
            screen.startScreen();
            TextGraphics textGraphics = screen.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);

            //textGraphics.enableModifiers(SGR.BOLD);
            int tiPos = 3;
            textGraphics.putString(2, 2, "Bid Qty");
            TerminalPosition startPosition = screen.getCursorPosition();
            textGraphics.putString(startPosition.withRelative(20, 2),"Bid Value");
            textGraphics.putString(screen.getCursorPosition().withRelative(40, 2),"Ask Value");
            textGraphics.putString(screen.getCursorPosition().withRelative(60, 2), "Ask Qty");
            screen.refresh();
            fillRaws(textGraphics,screen,tiPos);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){

        }
    }

    public static void fillRaws(TextGraphics textGraphics,Screen screen,int tiPos) throws IOException, InterruptedException {
        for(int i = 1; i < 5; i++){
            textGraphics.setForegroundColor(TextColor.ANSI.BLUE);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
            textGraphics.putString(2, (tiPos + i), "100");
            textGraphics.putString(screen.getCursorPosition().withRelative(20,(tiPos + i)), "101");
            textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);
            textGraphics.putString(screen.getCursorPosition().withRelative(40, (tiPos + i)),"120");
            textGraphics.putString(screen.getCursorPosition().withRelative(60, (tiPos + i)), "500");
            screen.refresh();
            Thread.sleep(1000);
        }
    }

}
