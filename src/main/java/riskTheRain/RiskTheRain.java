package riskTheRain;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

@SpireInitializer
public class RiskTheRain implements EditRelicsSubscriber, EditStringsSubscriber {
    private static final Logger logger = LogManager.getLogger(RiskTheRain.class.getName());
    private static final String[] shopRelicIds = {"ShapedGlass"};
    private static final String[][] totalSharedRelicIds = {shopRelicIds};
    private static final String ROOT_L10N_PATH = "localizations/";
    private static final String[] jsonFileNames = {"relicStrings.json", "powerStrings.json"};
    private static final Class<?>[] stringClasses = {RelicStrings.class, PowerStrings.class};

    public RiskTheRain() {
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

    public static void initialize() {
        logger.info(decorateInfo("init rat mod"));
        try {
            BaseMod.subscribe(new RiskTheRain());
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public void receiveEditRelics() {
        logger.info(decorateInfo("add relics"));
        try {
            for (String[] relics : totalSharedRelicIds) {
                for (String id : relics) {
                    BaseMod.addRelic(getRelic(id), RelicType.SHARED);
                }
            }
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public void receiveEditStrings() {
        String L10N_DIR;

        if (Settings.language == Settings.GameLanguage.ZHS) {
            L10N_DIR = ROOT_L10N_PATH + "zhs";
        } else {
            L10N_DIR = ROOT_L10N_PATH + "eng";
        }

        for (int i = 0; i < jsonFileNames.length; ++i) {
            BaseMod.loadCustomStrings(stringClasses[i], getFileReadString(L10N_DIR, jsonFileNames[i]));
        }
    }
}
