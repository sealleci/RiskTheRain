package riskTheRain;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardSave;
import lunar.TheLunar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patches.CreatureHealthPatch;
import patches.RenderRelicsCardPreviewPatch;
import potions.EffigyOfGrief;
import potions.GlowingMeteorite;
import potions.HelfireTincture;
import potions.SpinelTonic;
import saves.LunarCoinSaveState;
import rewards.LunarCoinReward;
import patches.LunarCoinPatch;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

@SpireInitializer
public class RiskTheRain implements EditRelicsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber, EditCardsSubscriber, PostInitializeSubscriber {
    private static final Logger logger = LogManager.getLogger(RiskTheRain.class.getName());
    private static final String[] lunarRelicIds = {"Transcendence", "ShapedGlass", "BrittleCrown", "FocusedConvergence", "BeadsOfFealty", "GestureOfTheDrowned"};
    private static final String[] colorlessCardIds = {"PotionOfWhorl"};
    private static final String ROOT_L10N_PATH = "localizations";
    private static final String[] jsonFileNames = {"relicStrings.json", "powerStrings.json", "potionStrings.json", "cardStrings.json"};
    private static final Class<?>[] stringClasses = {RelicStrings.class, PowerStrings.class, PotionStrings.class, CardStrings.class};
    private static final String keywordFileName = "keywordStrings.json";
    private static final String[] lunarPotionIds = {
            "HelfireTincture",
            "EffigyOfGrief",
            "GlowingMeteorite",
            "SpinelTonic"
    };
    private static final Color[] liquidColors = {
            new Color(25 / 255.0f, 67 / 255.0f, 80 / 255.0f, 1.0f),
            new Color(113 / 255.0f, 133 / 255.0f, 103 / 255.0f, 1.0f),
            new Color(162 / 255.0f, 219 / 255.0f, 250 / 255.0f, 1.0f),
            new Color(4 / 255.0f, 44 / 255.0f, 229 / 255.0f, 1.0f)
    };
    private static final Color[] hybridColors = {
            new Color(255 / 255.0f, 136 / 255.0f, 130 / 255.0f, 1.0f),
            new Color(86 / 255.0f, 74 / 255.0f, 74 / 255.0f, 1.0f),
            new Color(4 / 255.0f, 0 / 255.0f, 154 / 255.0f, 1.0f),
            new Color(182 / 255.0f, 201 / 255.0f, 240 / 255.0f, 1.0f)
    };


    public static int playerShielding = 0;
    public static ArrayList<Integer> monsterShielding = new ArrayList<>();
    public static boolean monstersUsingShielding = false;

    public RiskTheRain() {
        BaseMod.subscribe(this);
        BaseMod.addColor(
                TheLunar.Enums.LUNAR_BLUE,
                TheLunar.lunarBlue,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
        );
        BaseMod.addSaveField(
                "curTurnGetLunarCoins",
                new LunarCoinSaveState()
        );
        addPotions();
    }

    public Class<? extends AbstractPotion> getPotionClass(int i) {
        switch (i) {
            case 0:
                return HelfireTincture.class;
            case 1:
                return EffigyOfGrief.class;
            case 2:
                return GlowingMeteorite.class;
            case 3:
                return SpinelTonic.class;
            default:
                return AbstractPotion.class;
        }
    }

    public void addPotions() {
        for (int i = 0; i < lunarPotionIds.length; ++i) {
            BaseMod.addPotion(getPotionClass(i),
                    liquidColors[i], hybridColors[i], hybridColors[i], decorateId(lunarPotionIds[i]));
        }
    }

    private static String decorateInfo(String info) {
        return "RiskTheRain:" + info;
    }

    private static AbstractRelic getRelic(String id) {
        try {
            Class<?> className = Class.forName("relics." + id);
            return (AbstractRelic) className.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.catching(e);
            return null;
        }
    }

    private static AbstractCard getCard(String id) {
        try {
            Class<?> className = Class.forName("cards." + id);
            return (AbstractCard) className.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.catching(e);
            return null;
        }
    }

    public static String getFileReadString(String dir, String file) {
        return Gdx.files.internal(String.format("%s/%s", dir, file)).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    public static String decorateId(String id) {
        return "RiskTheRain:" + id;
    }

    public static FileHandle getRelicImagePath(String resource) {
        return Gdx.files.internal("images/relics/" + resource);
    }

    public static FileHandle getOutlineImagePath(String resource) {
        return Gdx.files.internal("images/relics/outline/" + resource);
    }

    public static FileHandle getLargeRelicImagePath(String resource) {
        return Gdx.files.internal("images/largeRelics/" + resource);
    }

    public static FileHandle getPowerImagePath(String resource) {
        return Gdx.files.internal("images/powers/" + resource);
    }

    public static FileHandle getPotionImagePath(String name, String resource) {
        return Gdx.files.internal(String.format("images/potions/%s/%s", name, resource));
    }

    public static String getCardImagePath(String color, String resource) {
        return String.format("images/cards/%s/%s", color, resource);
    }

    public static AbstractCard getCardToPreview(AbstractRelic relic) {
        if (relic == null) {
            return null;
        }
        return RenderRelicsCardPreviewPatch.RTRRelicCardPreviewFields.cardToPreview.get(relic);
    }

    public static void setCardToPreview(AbstractRelic relic, AbstractCard card) {
        if (relic == null) {
            return;
        }
        RenderRelicsCardPreviewPatch.RTRRelicCardPreviewFields.cardToPreview.set(relic, card);
    }

    public static int getMaxBlueShield(AbstractCreature creature) {
        if (creature == null) {
            return 0;
        }
        return CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature);
    }

    public static int getBlueShield(AbstractCreature creature) {
        if (creature == null) {
            return 0;
        }
        return CreatureHealthPatch.RTRCreatureFields.blueShield.get(creature);
    }

    public static void setBlueShield(AbstractCreature creature, int amt) {
        if (creature == null) {
            return;
        }
        CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature, amt);
        if (CreatureHealthPatch.RTRCreatureFields.blueShield.get(creature) < 0) {
            CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature, 0);
        }
        if (CreatureHealthPatch.RTRCreatureFields.blueShield.get(creature) >
                CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature)) {
            CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature,
                    CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature));
        }
    }

    public static void addBlueShield(AbstractCreature creature, int amt) {
        if (creature == null) {
            return;
        }
        CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature,
                CreatureHealthPatch.RTRCreatureFields.blueShield.get(creature) + amt);
        if (CreatureHealthPatch.RTRCreatureFields.blueShield.get(creature) < 0) {
            CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature, 0);
        }
        if (CreatureHealthPatch.RTRCreatureFields.blueShield.get(creature) >
                CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature)) {
            CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature,
                    CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature));
        }
    }

    public static void setMaxBlueShield(AbstractCreature creature, int amt) {
        if (creature == null) {
            return;
        }
        CreatureHealthPatch.RTRCreatureFields.maxBlueShield.set(creature, amt);
        if (CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature) < 0) {
            CreatureHealthPatch.RTRCreatureFields.maxBlueShield.set(creature, 0);
        }
    }

    public static void addMaxBlueShield(AbstractCreature creature, int amt) {
        if (creature == null) {
            return;
        }
        CreatureHealthPatch.RTRCreatureFields.maxBlueShield.set(creature,
                CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature) + amt);
        if (CreatureHealthPatch.RTRCreatureFields.maxBlueShield.get(creature) < 0) {
            CreatureHealthPatch.RTRCreatureFields.maxBlueShield.set(creature, 0);
        }
    }

    public static void clearBlueShield() {
        if (AbstractDungeon.player != null) {
            CreatureHealthPatch.RTRCreatureFields.blueShield.set(AbstractDungeon.player, 0);
        }
        playerShielding = 0;
        monsterShielding = new ArrayList<>();
        monstersUsingShielding = false;
    }

    public static void clearBlueShield(AbstractCreature creature) {
        if (creature == null)
            return;
        CreatureHealthPatch.RTRCreatureFields.blueShield.set(creature, 0);
    }

    public static int getCurTurnGetLunarCoins(AbstractCreature creature) {
        return LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.get(creature);
    }

    public static void setCurTurnGetLunarCoins(AbstractCreature creature, int amt) {
        LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.set(creature, amt);
        if (LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.get(creature) < 0) {
            LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.set(creature, 0);
        }
    }

    public static void addCurTurnGetLunarCoins(AbstractCreature creature, int amt) {
        LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.set(creature,
                LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.get(creature) + amt);
        if (LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.get(creature) < 0) {
            LunarCoinPatch.RTRCreatureFields.curTurnGetLunarCoins.set(creature, 0);
        }
    }

    public static void initialize() {
        try {
            logger.info(decorateInfo("init RiskTheRain mod"));
            RiskTheRain riskTheRain = new RiskTheRain();

        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public void receiveEditRelics() {
        logger.info(decorateInfo("add relics"));
        try {
            for (String id : lunarRelicIds) {
                BaseMod.addRelicToCustomPool(getRelic(id), TheLunar.Enums.LUNAR_BLUE);
            }
        } catch (Exception e) {
            logger.catching(e);
        }

    }

    public static String getL10NPath() {
        switch (Settings.language) {
            case ZHS:
                return String.format("%s/%s", ROOT_L10N_PATH, "zhs");
            case ENG:
            default:
                return String.format("%s/%s", ROOT_L10N_PATH, "eng");
        }
    }

    @Override
    public void receiveEditStrings() {
        String L10N_DIR = getL10NPath();

        for (int i = 0; i < jsonFileNames.length; ++i) {
            BaseMod.loadCustomStrings(stringClasses[i], getFileReadString(L10N_DIR, jsonFileNames[i]));
        }
    }

    @Override
    public void receiveEditKeywords() {
        String L10N_DIR = getL10NPath();

        String keywordJsonString = getFileReadString(L10N_DIR, keywordFileName);
        Gson gson = new Gson();
        Keyword[] keywords = gson.fromJson(keywordJsonString, Keyword[].class);
        for (Keyword k : keywords) {
            BaseMod.addKeyword(k.PROPER_NAME, k.NAMES, k.DESCRIPTION);
        }
    }

    @Override
    public void receiveEditCards() {
        for (String id : colorlessCardIds) {
            BaseMod.addCard(Objects.requireNonNull(getCard(id)));
        }
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.registerCustomReward(
                LunarCoinPatch.LUNAR_COIN,
                rewardSave -> {
                    return new LunarCoinReward(rewardSave.amount);
                },
                customReward -> {
                    return new RewardSave(
                            customReward.type.toString(),
                            LunarCoinReward.ID,
                            ((LunarCoinReward) customReward).amount,
                            0);
                }
        );
    }
}
