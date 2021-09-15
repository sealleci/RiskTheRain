package relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import lunar.LunarRelic;
import riskTheRain.RiskTheRain;
import powers.GlassStrengthPower;

import java.lang.reflect.Type;

public class ShapedGlass extends CustomRelic implements LunarRelic, CustomSavable<Integer> {
    private static final String SIGN = "ShapedGlass";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private static final int HP_DEC = 25;
    private static final int POWER_AMOUNT = 1;
    private int hpLoss = 0;
    private boolean isApplied = false;
    private static final String GLASSSTRENGTH_ID=  RiskTheRain.decorateId("GlassStrength");

    public ShapedGlass() {

        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s%d%s",
                DESCRIPTIONS[0],
                HP_DEC,
                DESCRIPTIONS[1],
                POWER_AMOUNT,
                DESCRIPTIONS[2]
        );
    }

    private void applyEffect() {
        if (!isApplied) {
            this.flash();
            AbstractPlayer player = AbstractDungeon.player;
            addToBot(new ApplyPowerAction(player, player, new GlassStrengthPower(player, POWER_AMOUNT), POWER_AMOUNT));
            addToBot(new RelicAboveCreatureAction(player, this));
            isApplied = true;
        }
    }

    private void revokeEffect() {
        if (isApplied) {
            AbstractPlayer player = AbstractDungeon.player;
            addToBot(new ReducePowerAction(player, player, GLASSSTRENGTH_ID, POWER_AMOUNT));
            isApplied = false;
        }
    }

    @Override
    public void onEquip() {
        try {
            this.flash();
            hpLoss = Math.max(MathUtils.ceil(HP_DEC / 100.0f * AbstractDungeon.player.maxHealth), 1);
            if (hpLoss >= AbstractDungeon.player.maxHealth) {
                hpLoss = AbstractDungeon.player.maxHealth - 1;
            }
            AbstractDungeon.player.decreaseMaxHealth(hpLoss);
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
            AbstractDungeon.player.increaseMaxHp(hpLoss, false);
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
    public int getPrice() {
        return 250;
    }

    @Override
    public int getLunarPrice() {
        return 5;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ShapedGlass();
    }

    @Override
    public Integer onSave() {
        return this.hpLoss;
    }

    @Override
    public void onLoad(Integer hpLoss) {
        if (hpLoss != null) {
            this.hpLoss = hpLoss;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<Integer>() {
        }.getType();
    }
}
