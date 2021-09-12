package powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import riskTheRain.RiskTheRain;

public class GlassStrengthPower extends AbstractPower {
    private static final String SIGN = "GlassStrength";
    public static final String POWER_ID = RiskTheRain.decorateId(SIGN);
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private int MULTIPLYING;
    private static final int BASE = 25;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public GlassStrengthPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.MULTIPLYING = amount * BASE;
        this.amount = amount;
        this.region128 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "32.png")), 0, 0, 32, 32);
        this.type = AbstractPower.PowerType.BUFF;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        try {
            this.description = String.format(
                    "%s%d%s",
                    DESCRIPTIONS[0],
                    MULTIPLYING,
                    DESCRIPTIONS[1]
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0f;
        MULTIPLYING += stackAmount * BASE;
        this.amount += stackAmount;
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        if (this.amount >= 999) {
            this.amount = 999;
            MULTIPLYING = 999 * BASE;
        }
        if (this.amount <= 0) {
            this.amount = 0;
            MULTIPLYING = 0;
        }
    }

    @Override
    public void reducePower(int reduceAmount) {
        this.fontScale = 8.0f;
        MULTIPLYING -= reduceAmount * BASE;
        this.amount -= reduceAmount;
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        if (this.amount >= 999) {
            this.amount = 999;
            MULTIPLYING = 999 * BASE;
        }
        if (this.amount <= 0) {
            this.amount = 0;
            MULTIPLYING = 0;
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return (MULTIPLYING / 100.0f + 1.0f) * damage;
        } else {
            return damage;
        }
    }
}
