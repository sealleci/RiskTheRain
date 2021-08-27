package relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import riskTheRain.RiskTheRain;
import powers.GlassStrength;

public class ShapedGlass extends CustomRelic {
    private static final String SIGN = "ShapedGlass";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private static final int DMG_INC = 25;
    private static final int HP_DEC = 25;
    private int hpLoss = 0;
    private int price = 250;
    private int lunaPrice = 2;
    private boolean isApplied = false;

    public ShapedGlass() {
        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s%d%s",
                DESCRIPTIONS[0],
                DMG_INC,
                DESCRIPTIONS[1],
                HP_DEC,
                DESCRIPTIONS[2]
        );
    }

    private void applyMultiplying() {
        if (!isApplied) {
            AbstractPlayer player = AbstractDungeon.player;
            this.flash();
            addToBot(new ApplyPowerAction(player, player, new GlassStrength(player, 1), 1));
            addToBot(new RelicAboveCreatureAction(player, this));
            isApplied = true;
        }
    }

    @Override
    public void onEquip() {
        this.flash();
        hpLoss = Math.max(MathUtils.floor(HP_DEC / 100.0f * AbstractDungeon.player.maxHealth), 1);
        if (hpLoss >= AbstractDungeon.player.maxHealth) {
            hpLoss = AbstractDungeon.player.maxHealth - 1;
        }
        AbstractDungeon.player.decreaseMaxHealth(hpLoss);
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            applyMultiplying();
        }
    }

    @Override
    public void atBattleStart() {
        try {
            isApplied = false;
            applyMultiplying();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atTurnStart() {
        try {
            applyMultiplying();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPrice() {
        return price;
    }
}
