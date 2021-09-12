package riskTheRain;

import basemod.BaseMod;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import lunar.TheLunar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patches.CreatureHealthPatch;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SpireInitializer
public class RiskTheRain implements EditRelicsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber {
    private static final Logger logger = LogManager.getLogger(RiskTheRain.class.getName());
    private static final String[] lunarRelicIds = {"Transcendence", "ShapedGlass", "BrittleCrown"};
    private static final String ROOT_L10N_PATH = "localizations";
    private static final String[] jsonFileNames = {"relicStrings.json", "powerStrings.json"};
    private static final String keywordFileName = "keywordStrings.json";
    private static final Class<?>[] stringClasses = {RelicStrings.class, PowerStrings.class};
    public static int playerShielding = 0;
    public static ArrayList<Integer> monsterShielding = new ArrayList<>();
    public static boolean monstersUsingShielding = false;

    public RiskTheRain() {
        BaseMod.subscribe(this);
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

    private String getFileReadString(String dir, String file) {
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

    public static FileHandle getPowerImagePath(String resource) {
        return Gdx.files.internal("images/powers/" + resource);
    }

    public static int getMaxBlueShield(AbstractCreature creature) {
        if (creature == null) {
            return 1;
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

    public static void initialize() {
        try {
            logger.info(decorateInfo("init RiskTheRain mod"));
            RiskTheRain riskTheRain = new RiskTheRain();
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

    private static String getL10NPath() {
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
}
