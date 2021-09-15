package saves;

import basemod.abstracts.CustomSavable;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import riskTheRain.RiskTheRain;

import java.lang.reflect.Type;

public class LunarCoinSaveState implements CustomSavable<Integer> {
    @Override
    public Integer onSave() {
        return RiskTheRain.getCurTurnGetLunarCoins(AbstractDungeon.player);
    }

    @Override
    public void onLoad(Integer lunarCoin) {
        if (lunarCoin != null) {
            RiskTheRain.setCurTurnGetLunarCoins(AbstractDungeon.player, lunarCoin);
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<Integer>() {
        }.getType();
    }
}
