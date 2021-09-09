package powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import riskTheRain.RiskTheRain;

public class ScarabRecoveryPower extends AbstractPower {
    private static final String SIGN = "ScarabRecovery";
    private static final String SHIELD_ID = RiskTheRain.decorateId("ScarabShield");
    public static final String POWER_ID = RiskTheRain.decorateId(SIGN);
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private int MAX_TURN;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public ScarabRecoveryPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        MAX_TURN = amount;
        this.amount = MAX_TURN;
        this.region128 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "32.png")), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        try {
            this.description = String.format(
                    "%s%d%s",
                    DESCRIPTIONS[0],
                    this.amount,
                    DESCRIPTIONS[1]
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stackPower(int amount) {
        this.fontScale = 8.0f;
        this.amount += amount;
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        if (this.amount >= MAX_TURN) {
            this.amount = MAX_TURN;
        }
        if (this.amount <= 0) {
            this.amount = 0;
        }
    }

    @Override
    public void reducePower(int amount) {
        this.fontScale = 8.0f;
        this.amount -= amount;
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        if (this.amount >= MAX_TURN) {
            this.amount = MAX_TURN;
        }
        if (this.amount <= 0) {
            this.amount = 0;
        }
    }

    @Override
    public void atEndOfRound() {
        try {
            flashWithoutSound();
            if (this.amount - 1 <= 0) {
                ScarabShieldPower power = (ScarabShieldPower) this.owner.getPower(SHIELD_ID);
                if (power != null) {
//                    addToTop(new ApplyPowerAction(this.owner, this.owner, power, power.getRecoverCount()));
                    power.recover();
                }
            }
            addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
