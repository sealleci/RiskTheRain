package patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relics.Transcendence;

public class IncreaseHealthPatch {
    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "increaseMaxHp")
    public static class PlayerIncreaseHealthPatch {
        public static void Prefix(AbstractCreature __instance, @ByRef int[] amount, boolean showEffect) {
            if (!Settings.isEndless || !AbstractDungeon.player.hasBlight("FullBelly")) {
                if (amount[0] > 0) {
                    if (__instance instanceof AbstractPlayer) {
                        Transcendence bug = (Transcendence) ((AbstractPlayer) __instance).getRelic(Transcendence.ID);
                        if (bug != null && !bug.isRemoving) {
                            bug.increaseHealth(amount[0]);
                            amount[0] = 0;
                        }
                    }
                }
            }
        }
    }
}
