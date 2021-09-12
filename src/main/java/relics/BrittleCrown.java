package relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import lunar.LunarRelic;
import powers.BrittleCoinPower;
import riskTheRain.RiskTheRain;

public class BrittleCrown extends CustomRelic implements LunarRelic {
    private static final String SIGN = "BrittleCrown";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private static final String COIN_ID = RiskTheRain.decorateId("BrittleCoin");
    private static final int perAttackedDamage = 1;
    private static final int perAttackedLoseStack = 2;
    private static final int perAttackDamage = 2;
    private static final int perAttackGainStack = 1;
    private int accumulateAttackAmount = 0;
    private int accumulateAttackedAmount = 0;


    public BrittleCrown() {
        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s%d%s%d%s%d%s",
                DESCRIPTIONS[0],
                perAttackDamage,
                DESCRIPTIONS[1],
                perAttackGainStack,
                DESCRIPTIONS[2],
                perAttackedDamage,
                DESCRIPTIONS[3],
                perAttackedLoseStack,
                DESCRIPTIONS[4]
        );
    }

    private void revokeEffect() {
        if (AbstractDungeon.player.getPower(COIN_ID) != null) {
            addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, COIN_ID));
        }
    }

    @Override
    public void atBattleStart() {
        accumulateAttackAmount = 0;
        accumulateAttackedAmount = 0;
    }

    @Override
    public void onEquip() {
        try {
            this.flash();
            accumulateAttackAmount = 0;
            accumulateAttackedAmount = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnequip() {
        try {
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                revokeEffect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //抛去格挡后的伤害
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            if (damageAmount > 0) {
                this.flash();
                accumulateAttackAmount += damageAmount;
                int gainStacks = accumulateAttackAmount / perAttackDamage * perAttackGainStack;
//            System.out.println("gain: " + accumulateAttackAmount + " " + gainStacks);
                AbstractCreature player = AbstractDungeon.player;
                addToTop(new ApplyPowerAction(player, player, new BrittleCoinPower(player, gainStacks), gainStacks));
                accumulateAttackAmount %= perAttackDamage;
            }
        }
    }

    //抛去格挡后的伤害
    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            if (damageAmount > 0) {
                this.flash();
                accumulateAttackedAmount += damageAmount;
                int lostStacks = accumulateAttackedAmount / perAttackedDamage * perAttackedLoseStack;
//            System.out.println("lose: " + accumulateAttackedAmount + " " + lostStacks);
                AbstractCreature player = AbstractDungeon.player;
                addToBot(new ApplyPowerAction(player, player, new BrittleCoinPower(player, -lostStacks), -lostStacks));
                accumulateAttackedAmount %= perAttackedDamage;
            }
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        BrittleCoinPower coinPower = (BrittleCoinPower) AbstractDungeon.player.getPower(COIN_ID);
        if (coinPower != null) {
            int coins = coinPower.getCoins();
            if (coins > 0) {
                AbstractDungeon.getCurrRoom().addGoldToRewards(coins);
            } else if (coins < 0) {
                AbstractDungeon.player.loseGold(Math.abs(coins));
            }
        }
        accumulateAttackAmount = 0;
        accumulateAttackedAmount = 0;
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
        return new BrittleCrown();
    }

}
