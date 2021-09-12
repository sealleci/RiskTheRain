package powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import riskTheRain.RiskTheRain;

public class BrittleCoinPower extends AbstractPower {
    private static final String SIGN = "BrittleCoin";
    public static final String POWER_ID = RiskTheRain.decorateId(SIGN);
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final int perCoinNeedStack = 4;
    private static final int perCoin = 1;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public BrittleCoinPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.region128 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "32.png")), 0, 0, 32, 32);
        this.canGoNegative = true;
        updateDescription();
    }

    public int getCoins() {
        return this.amount / perCoinNeedStack * perCoin;
    }

    @Override
    public void updateDescription() {
        try {
            int coins = getCoins();
            this.description = String.format(
                    "%s%d%s",
                    coins >= 0 ? DESCRIPTIONS[0] : DESCRIPTIONS[1],
                    coins,
                    DESCRIPTIONS[2]
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stackPower(int amount) {
        this.fontScale = 8.0f;
        this.amount += amount;
        if (this.amount >= 999) {
            this.amount = 999;
        }
        if (this.amount <= -999) {
            this.amount = -999;
        }
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        this.type = PowerType.BUFF;
    }

    @Override
    public void reducePower(int amount) {
        this.fontScale = 8.0f;
        this.amount -= amount;
        if (this.amount >= 999) {
            this.amount = 999;
        }
        if (this.amount <= -999) {
            this.amount = -999;
        }
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        this.type = PowerType.BUFF;
    }
}
