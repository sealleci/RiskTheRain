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

public class SpinelTonic extends AbstractPotion {
    public static final String SIGN = "SpinelTonic";
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
        liquidColor = new Color(4 / 255.0f, 44 / 255.0f, 229 / 255.0f, 1.0f);
        hybridColor = new Color(182 / 255.0f, 201 / 255.0f, 240 / 255.0f, 1.0f);
        CONTAINER = new Texture(RiskTheRain.getPotionImagePath(SIGN, "body.png"));
        LIQUID = new Texture(RiskTheRain.getPotionImagePath(SIGN, "liquid.png"));
        HYBRID = new Texture(RiskTheRain.getPotionImagePath(SIGN, "hybrid.png"));
        OUTLINE = new Texture(RiskTheRain.getPotionImagePath(SIGN, "outline.png"));
    }

    public SpinelTonic() {
        super(NAME, ID, PotionRarity.RARE, AddPotionPatch.SPINEL_TONIC, PotionEffect.OSCILLATE,
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
        return new SpinelTonic();
    }
}
