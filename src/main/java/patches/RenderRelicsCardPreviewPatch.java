package patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import relics.Transcendence;
import riskTheRain.RiskTheRain;

public class RenderRelicsCardPreviewPatch {
    @SpirePatch(cls = "com.megacrit.cardcrawl.relics.AbstractRelic", method = "<class>")
    public static class RTRRelicCardPreviewFields {
        public static SpireField<AbstractCard> cardToPreview = new SpireField<>(() -> null);
    }

    private static void patchCode(AbstractRelic __instance, SpriteBatch sb) {
        AbstractCard card = RiskTheRain.getCardToPreview(__instance);
        if (card != null) {
            if ((float) InputHelper.mX < 1400.0F * Settings.scale) {
                if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.RELIC_VIEW) {
//                    TipHelper.queuePowerTips(180.0F * Settings.scale, (float) Settings.HEIGHT * 0.7F, this.tips);
                    card.current_x = 625.0F * Settings.scale;
                    card.current_y = (float) Settings.HEIGHT * 0.58f;
                } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && __instance.tips.size() > 2 && !AbstractDungeon.player.hasRelic(__instance.relicId)) {
//                    TipHelper.queuePowerTips((float) InputHelper.mX + 60.0F * Settings.scale, (float) InputHelper.mY + 180.0F * Settings.scale, this.tips);
                    card.current_x = (float) InputHelper.mX + 500.0F * Settings.scale;
                    card.current_y = (float) InputHelper.mY - 160.0F * Settings.scale;
                } else if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(__instance.relicId)) {
//                    TipHelper.queuePowerTips((float) InputHelper.mX + 60.0F * Settings.scale, (float) InputHelper.mY - 30.0F * Settings.scale, this.tips);
                    card.current_x = (float) InputHelper.mX + 500.0F * Settings.scale;
                    card.current_y = (float) InputHelper.mY - 160.0F * Settings.scale;
                } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
//                    TipHelper.queuePowerTips(360.0F * Settings.scale, (float) InputHelper.mY + 50.0F * Settings.scale, this.tips);
                    card.current_x = 450.0F * Settings.scale;
                    card.current_y = (float) InputHelper.mY - 160.0F * Settings.scale;
                } else {
//                    TipHelper.queuePowerTips((float) InputHelper.mX + 50.0F * Settings.scale, (float) InputHelper.mY + 50.0F * Settings.scale, this.tips);
                    card.current_x = (float) InputHelper.mX + 500.0F * Settings.scale;
                    card.current_y = (float) InputHelper.mY - 160.0F * Settings.scale;
                }
            } else {
//                TipHelper.queuePowerTips((float) InputHelper.mX - 350.0F * Settings.scale, (float) InputHelper.mY - 50.0F * Settings.scale, this.tips);
                card.current_x = (float) InputHelper.mX + 150.0F * Settings.scale;
                card.current_y = (float) InputHelper.mY - 100.0F * Settings.scale;
            }
            card.drawScale = 0.75F;
            card.render(sb);
        }
    }

    private static void patchCodeForPopup(AbstractRelic __instance, SpriteBatch sb) {
        AbstractCard card = RiskTheRain.getCardToPreview(__instance);
        if (card != null) {
            card.current_x = 550F * Settings.scale;
            card.current_y = (float) Settings.HEIGHT * 0.275f;
            card.drawScale = 0.75F;
            card.render(sb);
        }
    }

    //renderGenericTip
    @SpirePatch(cls = "com.megacrit.cardcrawl.relics.AbstractRelic", method = "renderTip")
    public static class RenderCardPreviewPatch {
        public static void Postfix(AbstractRelic __instance, SpriteBatch sb) {
            patchCode(__instance, sb);
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.relics.AbstractRelic", method = "renderBossTip")
    public static class RenderCardPreviewBossPatch {
        public static void Postfix(AbstractRelic __instance, SpriteBatch sb) {
            patchCode(__instance, sb);
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.screens.SingleRelicViewPopup", method = "renderTips")
    public static class RenderCardPreviewRelicPopupPatch {
        public static void Postfix(SingleRelicViewPopup __instance, SpriteBatch sb) {
            patchCodeForPopup(ReflectionHacks.getPrivate(__instance, SingleRelicViewPopup.class, "relic"), sb);
        }
    }
}
