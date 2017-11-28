package com.abc.quickfixj;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Created by darshanas on 11/27/2017.
 */
public class UITest {
    public static void main(String[] args) {
        DefaultTerminalFactory dtf = new DefaultTerminalFactory();
        Terminal terminal = null;
        try {
            terminal = dtf.createTerminal();
            terminal.putCharacter('H');
            terminal.putCharacter('e');
            terminal.putCharacter('l');
            terminal.putCharacter('l');
            terminal.putCharacter('o');
            terminal.putCharacter('\n');
            terminal.flush();
            Thread.sleep(2000);

            TerminalPosition startPosition = terminal.getCursorPosition();
            terminal.setCursorPosition(startPosition.withRelativeColumn(3).withRelativeRow(1));
            terminal.flush();
            Thread.sleep(2000);

            terminal.setBackgroundColor(TextColor.ANSI.BLUE);
            terminal.setForegroundColor(TextColor.ANSI.YELLOW);

            terminal.putCharacter('Y');
            terminal.putCharacter('e');
            terminal.putCharacter('l');
            terminal.putCharacter('l');
            terminal.putCharacter('o');
            terminal.putCharacter('w');
            terminal.putCharacter(' ');
            terminal.putCharacter('o');
            terminal.putCharacter('n');
            terminal.putCharacter(' ');
            terminal.putCharacter('b');
            terminal.putCharacter('l');
            terminal.putCharacter('u');
            terminal.putCharacter('e');
            terminal.flush();
            Thread.sleep(2000);

            terminal.setCursorPosition(startPosition.withRelativeColumn(3).withRelativeRow(3));
            terminal.flush();
            Thread.sleep(2000);

            terminal.enableSGR(SGR.BOLD);
            terminal.putCharacter('Y');
            terminal.putCharacter('e');
            terminal.putCharacter('l');
            terminal.putCharacter('l');
            terminal.putCharacter('o');
            terminal.putCharacter('w');
            terminal.putCharacter(' ');
            terminal.putCharacter('o');
            terminal.putCharacter('n');
            terminal.putCharacter(' ');
            terminal.putCharacter('b');
            terminal.putCharacter('l');
            terminal.putCharacter('u');
            terminal.putCharacter('e');
            terminal.flush();
            Thread.sleep(2000);

            terminal.resetColorAndSGR();
            terminal.setCursorPosition(terminal.getCursorPosition().withColumn(0).withRow(2));
            terminal.putCharacter('D');
            terminal.putCharacter('o');
            terminal.putCharacter('n');
            terminal.putCharacter('e');
            terminal.putCharacter('\n');
            terminal.flush();

            Thread.sleep(2000);

            terminal.bell();
            terminal.flush();
            Thread.sleep(200);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            if(terminal != null){
                try {
                    terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
