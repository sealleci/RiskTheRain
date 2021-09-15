package lunar;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;

public class TheLunar {
    public static Color lunarBlue = new Color(83 / 255.0f, 225 / 255.0f, 245 / 255.0f, 1.0f);

    public static class Enums {
        @SpireEnum
        public static AbstractPlayer.PlayerClass THE_LUNAR;

        @SpireEnum(name = "LUNAR_BLUE")
        public static AbstractCard.CardColor LUNAR_BLUE;

        @SpireEnum(name = "LUNAR_BLUE")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public static boolean isHeresySuit(){
        return true;
    }
}
