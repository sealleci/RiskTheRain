package potions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import lunar.TheLunar;
import patches.AddPotionPatch;
import riskTheRain.RiskTheRain;

public class HelfireTincture extends AbstractPotion {
    public static final String SIGN = "HelfireTincture";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(ID);
    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;
    public static final Color liquidColor;
    public static final Color hybridColor;
    public static final Texture CONTAINER;
    public static final Texture LIQUID;
    public static final Texture HYBRID;
    public static final Texture OUTLINE;

    static {
        liquidColor = new Color(25 / 255.0f, 67 / 255.0f, 80 / 255.0f, 1.0f);
        hybridColor = new Color(255 / 255.0f, 136 / 255.0f, 130 / 255.0f, 1.0f);
        CONTAINER = new Texture(RiskTheRain.getPotionImagePath(SIGN, "body.png"));
        LIQUID = new Texture(RiskTheRain.getPotionImagePath(SIGN, "liquid.png"));
        HYBRID = new Texture(RiskTheRain.getPotionImagePath(SIGN, "hybrid.png"));
        OUTLINE = new Texture(RiskTheRain.getPotionImagePath(SIGN, "outline.png"));
    }

    public HelfireTincture() {
        super(NAME, ID, PotionRarity.RARE, AddPotionPatch.HELFIRE_TINCTURE, PotionEffect.OSCILLATE,
                liquidColor, hybridColor, hybridColor);
        this.description = DESCRIPTIONS[0];
        this.tips.add(new PowerTip(this.name, this.description));
        this.labOutlineColor = TheLunar.lunarBlue;
    }

    @Override
    public void use(AbstractCreature target) {

    }

    @Override
    public int getPotency(int level) {
        return 2;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new HelfireTincture();
    }
}
