package relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import lunar.LunarRelic;
import riskTheRain.RiskTheRain;

import java.lang.reflect.Type;
import java.util.Objects;


public class BeadsOfFealty extends CustomRelic implements LunarRelic, CustomSavable<Boolean> {
    private static final String SIGN = "BeadsOfFealty";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture L_IMG = new Texture(RiskTheRain.getLargeRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private static final int PER_KEY = 1;
    private boolean isApplied = false;

    public BeadsOfFealty() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SHOP, AbstractRelic.LandingSound.MAGICAL);
        this.largeImg = L_IMG;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s",
                DESCRIPTIONS[0],
                PER_KEY,
                DESCRIPTIONS[1]
        );
    }

    private void applyEffect() {
        if (!isApplied) {
            if (Objects.equals(AbstractDungeon.id, getLevelId(3)) &&
                    isAtBoss() &&
                    (AbstractDungeon.ascensionLevel < 20 || AbstractDungeon.bossList.size() < 2)) {
                this.flash();
                if (!Settings.hasRubyKey) {
                    Settings.hasRubyKey = true;
                } else if (!Settings.hasEmeraldKey) {
                    Settings.hasEmeraldKey = true;
                } else if (!Settings.hasSapphireKey) {
                    Settings.hasSapphireKey = true;
                }
                usedUp();
                isApplied = true;
            }
        }
    }

    private void revokeEffect() {
        if (isApplied) {
            isApplied = false;
        }
    }


    @Override
    public void onEquip() {
        try {
            applyEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnequip() {
        try {
            revokeEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVictory() {
        try {
            applyEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPrice() {
        return 100;
    }

    @Override
    public int getLunarPrice() {
        return 2;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BeadsOfFealty();
    }

    @Override
    public Boolean onSave() {
        return this.isApplied;
    }

    @Override
    public void onLoad(Boolean isApplied) {
        if (isApplied != null) {
            this.isApplied = isApplied;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<Boolean>() {
        }.getType();
    }
}
