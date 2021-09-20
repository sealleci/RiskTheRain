package relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import lunar.LunarRelic;
import riskTheRain.RiskTheRain;

import java.util.HashMap;
import java.util.Map;

public class FocusedConvergence extends CustomRelic implements LunarRelic {
    private static final String SIGN = "FocusedConvergence";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture L_IMG = new Texture(RiskTheRain.getLargeRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private static final int CONVERSION = 20;
    private static final Map<String, Integer> plates;
    private boolean isApplied = false;

    static {
        plates = new HashMap<>();
        plates.put("Exordium", 4);
        plates.put("TheCity", 6);
        plates.put("TheBeyond", 8);
        plates.put("TheEnding", 10);
    }

    public FocusedConvergence() {
        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
        this.largeImg=L_IMG;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s",
                DESCRIPTIONS[0],
                CONVERSION,
                DESCRIPTIONS[1]
        );
    }

    @Override
    public int getPrice() {
        return 250;
    }

    @Override
    public int getLunarPrice() {
        return 5;
    }

    private void applyEffect() {
        if (!isApplied) {
            if (isAtBoss()) {
                flash();
                Integer count = plates.get(AbstractDungeon.id);
                if (count == null) {
                    count = 6;
                }
                for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                    if (m.type == AbstractMonster.EnemyType.BOSS) {
                        m.decreaseMaxHealth(MathUtils.floor(CONVERSION / 100.0F * m.maxHealth));
                        addToBot(new ApplyPowerAction(m, m, new PlatedArmorPower(m, count), count));
                        addToBot(new GainBlockAction(m, m, count * 3));
                    }
                }
                addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            }
            isApplied = true;
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
            revokeEffect();
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
    public AbstractRelic makeCopy() {
        return new FocusedConvergence();
    }
}
