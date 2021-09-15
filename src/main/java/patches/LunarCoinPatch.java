package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class LunarCoinPatch {
    @SpireEnum
    public static RewardItem.RewardType LUNAR_COIN;

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "<class>")
    public static class RTRCreatureFields {
        public static SpireField<Integer> curTurnGetLunarCoins = new SpireField<>(() -> 0);
    }
}
