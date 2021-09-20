package relics;

import basemod.abstracts.CustomRelic;
import cards.PotionOfWhorl;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import lunar.LunarRelic;
import riskTheRain.RiskTheRain;

import java.util.Objects;

public class GestureOfTheDrowned extends CustomRelic implements LunarRelic {
    private static final String SIGN = "GestureOfTheDrowned";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final Texture IMG = new Texture(RiskTheRain.getRelicImagePath(SIGN + ".png"));
    public static final Texture L_IMG = new Texture(RiskTheRain.getLargeRelicImagePath(SIGN + ".png"));
    public static final Texture OUTLINE = new Texture(RiskTheRain.getOutlineImagePath(SIGN + ".png"));
    private static final int HP_LOSS = 1;
    private static final int ADD_CARDS = 2;
    private boolean isApplied = false;

    public GestureOfTheDrowned() {
        super(ID, IMG, OUTLINE, AbstractRelic.RelicTier.SHOP, AbstractRelic.LandingSound.MAGICAL);
        RiskTheRain.setCardToPreview(this, new PotionOfWhorl());
        this.largeImg=L_IMG;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(
                "%s%d%s%d%s",
                DESCRIPTIONS[0],
                ADD_CARDS,
                DESCRIPTIONS[1],
                HP_LOSS,
                DESCRIPTIONS[2]
        );
    }

    private void applyEffect() {
        if (!isApplied) {
            this.flash();
            addToTop(new MakeTempCardInHandAction(new PotionOfWhorl(), ADD_CARDS));
            isApplied = true;
        }
    }

    private void revokeEffect() {
        if (isApplied) {
            int count = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (Objects.equals(c.cardID, PotionOfWhorl.ID)) {
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    count++;
                }
                if (count >= ADD_CARDS) {
                    break;
                }
            }
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
    public void onUsePotion() {
        AbstractDungeon.player.decreaseMaxHealth(HP_LOSS);
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
        return new GestureOfTheDrowned();
    }
}
