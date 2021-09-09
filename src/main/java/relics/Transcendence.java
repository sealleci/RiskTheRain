package relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import lunar.LunarRelic;
import riskTheRain.RiskTheRain;

//TODO: save max shield
//TODO: Blue HP bar
public class Transcendence extends CustomRelic implements LunarRelic {
    private static final String SIGN = "Transcendence";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private int price = 250;
    private int lunarPrice = 5;
    private static int stacks = 0;
    private static int baseShield = 0;
    private int hpLoss = 0;
    private int maxShield = 0;
    private boolean isApplied = false;
    private static final int HP_LOCK = 5;
    private static final float CONVERSION = 0.5f;
    private static final int TURNS = 2;
    private static final String SHIELD_ID = RiskTheRain.decorateId("ScarabShield");
    public static final String RECOVERY_ID = RiskTheRain.decorateId("ScarabRecovery");

    public Transcendence() {
        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
        Transcendence.stacks++;
    }

    protected void finalize() {
        Transcendence.stacks--;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s%.1f%s%d%s",
                DESCRIPTIONS[0],
                HP_LOCK,
                DESCRIPTIONS[1],
                CONVERSION,
                DESCRIPTIONS[2],
                TURNS,
                DESCRIPTIONS[3]
        );
    }

    private void applyMultiplying() {
        if (!isApplied) {
            this.flash();
            RiskTheRain.setMaxBlueShield(AbstractDungeon.player, 40);
            RiskTheRain.setBlueShield(AbstractDungeon.player, 40);
            isApplied = true;
//            AbstractPlayer player = AbstractDungeon.player;
//            this.flash();
//            addToBot(new ApplyPowerAction(player, player, new ScarabShieldPower(player, maxShield), maxShield));
//            addToBot(new RelicAboveCreatureAction(player, this));
//            isApplied = true;
        }
    }

    private void revokeMultiplying() {
        if (isApplied) {
            if (AbstractDungeon.player.getPower(RECOVERY_ID) != null) {
                addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, RECOVERY_ID));
            }
            addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, SHIELD_ID));
            isApplied = false;
        }
    }

    @Override
    public void onEquip() {
        try {
            this.flash();
            int health = AbstractDungeon.player.maxHealth;
            if (health > 5) {
                hpLoss = health - 5;
            }
            maxShield = MathUtils.floor(health * CONVERSION);
            AbstractDungeon.player.decreaseMaxHealth(hpLoss);
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                applyMultiplying();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnequip() {
        try {
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                revokeMultiplying();
            }
            AbstractDungeon.player.increaseMaxHp(hpLoss, false);
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public int getLunarPrice() {
        return lunarPrice;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Transcendence();
    }
}
