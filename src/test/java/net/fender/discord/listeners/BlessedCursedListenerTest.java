package net.fender.discord.listeners;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

public class BlessedCursedListenerTest {

    @Test
    public void test() {
        String text = "am i blessed?";
        Matcher matcher = BlessedCursedListener.BLESSSED.matcher(text);
        matcher.matches();
        String cursed = matcher.group(1) + "CURSED" + matcher.group(3);
        System.out.println(cursed);
    }
}
