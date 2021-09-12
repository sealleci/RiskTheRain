package powers;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import riskTheRain.RiskTheRain;

public class ScarabRecoveryPower extends AbstractPower {
    private static final String SIGN = "ScarabRecovery";
    public static final String POWER_ID = RiskTheRain.decorateId(SIGN);
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private final int MAX_TURN;

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
    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
    }

    @Override
    public void stackPower(int amount) {
        this.fontScale = 8.0f;
        this.amount += amount;
        if (this.amount >= MAX_TURN) {
            this.amount = MAX_TURN;
        }
        if (this.amount <= 0) {
            this.amount = 0;
        }
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    @Override
    public void reducePower(int amount) {
        this.fontScale = 8.0f;
        this.amount -= amount;
        if (this.amount >= MAX_TURN) {
            this.amount = MAX_TURN;
        }
        if (this.amount <= 0) {
            this.amount = 0;
        }
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    @Override
    public void atStartOfTurn() {
        try {
            flashWithoutSound();
            if (this.amount - 1 <= 0) {
                int diff = RiskTheRain.getMaxBlueShield(this.owner) - RiskTheRain.getBlueShield(this.owner);
                RiskTheRain.setBlueShield(this.owner, RiskTheRain.getMaxBlueShield(this.owner));
                if (diff > 0) {
                    ReflectionHacks.privateMethod(AbstractCreature.class, "healthBarRevivedEvent").invoke(this.owner);
                    AbstractDungeon.topPanel.panelHealEffect();
                    AbstractDungeon.effectsQueue.add(new HealEffect(this.owner.hb.cX - this.owner.animX, this.owner.hb.cY, diff));
                }
            }
            addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetTurn() {
        addToBot(new ApplyPowerAction(this.owner, this.owner, this, MAX_TURN - this.amount));
    }
}
