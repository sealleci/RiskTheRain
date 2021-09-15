package rewards;

import basemod.abstracts.CustomReward;
import basemod.abstracts.CustomSavable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import patches.LunarCoinPatch;
import riskTheRain.RiskTheRain;

import java.lang.reflect.Type;
import java.util.Objects;

public class LunarCoinReward extends CustomReward{
    public static final String SIGN = "LunarCoin";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    private static final String fileName = "miscStrings.json";
    public String NAME;
    public int amount;

    public static class MiscString {
        public String ID;
        public String NAME;

        public MiscString(String id, String name) {
            this.ID = id;
            this.NAME = name;
        }
    }

    public LunarCoinReward(int amount) {
        super(ImageMaster.UI_GOLD, "", LunarCoinPatch.LUNAR_COIN);
        this.amount = amount;
        this.NAME = getName();
        this.text = this.amount + NAME;
    }

    public String getName() {
        Gson gson = new Gson();
        MiscString[] miscs = gson.fromJson(RiskTheRain.getFileReadString(RiskTheRain.getL10NPath(), fileName), MiscString[].class);
        for (MiscString m : miscs) {
            if (Objects.equals(m.ID, ID)) {
                return m.NAME;
            }
        }
        return "";
    }

    @Override
    public boolean claimReward() {
        RiskTheRain.addCurTurnGetLunarCoins(AbstractDungeon.player, amount);
        return true;
    }
}
