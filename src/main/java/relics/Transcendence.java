package relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import lunar.LunarRelic;
import powers.ScarabRecoveryPower;
import riskTheRain.RiskTheRain;

import java.lang.reflect.Type;

public class Transcendence extends CustomRelic implements LunarRelic, CustomSavable<Transcendence.BlueShieldSave> {
    private static final String SIGN = "Transcendence";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private int hpLoss = 0;
    private int maxShield = 0;
    private boolean isApplied = false;
    private boolean isEquipped = false;
    private static final int HP_LOCK = 5;
    private static final float CONVERSION = 0.5f;
    private static final int TURNS = 3;
    private static final String RECOVERY_ID = RiskTheRain.decorateId("ScarabRecovery");
    public boolean isRemoving = false;

    public Transcendence() {
        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
    }

    protected void finalize() {
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s%.1f%s",
                DESCRIPTIONS[0],
                HP_LOCK,
                DESCRIPTIONS[1],
                CONVERSION,
                DESCRIPTIONS[2]
        );
    }

    private void applyEffect() {
        if (!isEquipped) {
            RiskTheRain.addMaxBlueShield(AbstractDungeon.player, maxShield);
            isEquipped = true;
        }
        if (!isApplied) {
            this.flash();
            RiskTheRain.setBlueShield(AbstractDungeon.player,
                    RiskTheRain.getMaxBlueShield(AbstractDungeon.player));
            isApplied = true;
        }
    }

    private void revokeEffect() {
        if (isApplied) {
            if (AbstractDungeon.player.getPower(RECOVERY_ID) != null) {
                addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, RECOVERY_ID));
            }
            RiskTheRain.setBlueShield(AbstractDungeon.player,
                    RiskTheRain.getMaxBlueShield(AbstractDungeon.player));
            isApplied = false;
        }
    }

    @Override
    public void onEquip() {
        try {
            this.flash();
            if (!isEquipped) {
                int health = AbstractDungeon.player.maxHealth;
                if (health > 5) {
                    hpLoss = health - 5;
                }
                maxShield = MathUtils.floor(health * CONVERSION);
                AbstractDungeon.player.decreaseMaxHealth(hpLoss);
                RiskTheRain.addMaxBlueShield(AbstractDungeon.player, maxShield);
                isEquipped = true;
            }
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                applyEffect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnequip() {
        try {
            if (isEquipped) {
                isRemoving = true;
                RiskTheRain.addMaxBlueShield(AbstractDungeon.player, -maxShield);
                AbstractDungeon.player.increaseMaxHp(hpLoss, false);
                hpLoss = 0;
                maxShield = 0;
                isEquipped = false;
            }
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                revokeEffect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atBattleStart() {
        try {
            isApplied = false;
            applyEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atTurnStart() {
        try {
            applyEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        ScarabRecoveryPower recovery = (ScarabRecoveryPower) AbstractDungeon.player.getPower(RECOVERY_ID);
        if (recovery == null) {
            if (RiskTheRain.getBlueShield(AbstractDungeon.player)
                    < RiskTheRain.getMaxBlueShield(AbstractDungeon.player)) {
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ScarabRecoveryPower(AbstractDungeon.player, TURNS), TURNS));
            }
        }
/*
        else {
            if (info.type == DamageInfo.DamageType.HP_LOSS ||
                    AbstractDungeon.player.currentBlock <= damageAmount) {
                recovery.resetTurn();
            }
        }
*/
        return damageAmount;
    }

    public void increaseHealth(int amount) {
        this.hpLoss += amount;
        int diff = Math.max(MathUtils.floor(
                (AbstractDungeon.player.maxHealth + hpLoss) * CONVERSION
        ) - maxShield, 0);
        RiskTheRain.addMaxBlueShield(AbstractDungeon.player, diff);
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            RiskTheRain.addBlueShield(AbstractDungeon.player, diff);
        }
        this.maxShield += diff;
    }

    @Override
    public int getPrice() {
        return 250;
    }

    @Override
    public int getLunarPrice() {
        return 5;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Transcendence();
    }

    public static class BlueShieldSave {
        public int maxBlueShield;
        public int hpLoss;

        public BlueShieldSave(int maxBlueShield, int hpLoss) {
            this.maxBlueShield = maxBlueShield;
            this.hpLoss = hpLoss;
        }
    }

    @Override
    public BlueShieldSave onSave() {
        return new BlueShieldSave(maxShield, hpLoss);
    }

    @Override
    public void onLoad(BlueShieldSave blueShieldSave) {
        if (blueShieldSave != null) {
            maxShield = blueShieldSave.maxBlueShield;
            hpLoss = blueShieldSave.hpLoss;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<BlueShieldSave>() {
        }.getType();
    }
}
