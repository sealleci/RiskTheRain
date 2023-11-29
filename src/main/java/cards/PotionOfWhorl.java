package cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import riskTheRain.RiskTheRain;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.returnRandomPotion;

public class PotionOfWhorl extends CustomCard {
    public static final String SIGN = "PotionOfWhorl";
    public static final String ID = RiskTheRain.decorateId(SIGN);
    public static final String COLOR = "colorless";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final int COST = 0;

    public PotionOfWhorl() {
        super(
                ID,
                cardStrings.NAME,
                RiskTheRain.getCardImagePath(COLOR, SIGN + ".png"),
                COST,
                cardStrings.DESCRIPTION,
                AbstractCard.CardType.SKILL,
                CardColor.COLORLESS,
                AbstractCard.CardRarity.RARE,
                AbstractCard.CardTarget.SELF
        );
        this.exhaust = true;
        this.isEthereal = true;
        this.tags.add(AbstractCard.CardTags.HEALING);
    }

    private AbstractPotion getCertainRarityRandomPotion(AbstractPotion.PotionRarity rarity) {
        ArrayList<AbstractPotion> potions = new ArrayList<>();
        for (String name : PotionHelper.potions) {
            AbstractPotion p = PotionHelper.getPotion(name);
            if (p.rarity == rarity) {
                potions.add(p);
            }
        }
        if (!potions.isEmpty()) {
            return potions.get(AbstractDungeon.potionRng.random(potions.size() - 1));
        } else {
            return null;
        }
    }

    private AbstractPotion getRandomPotion() {
        int roll = AbstractDungeon.potionRng.random(0, 99);
        if (roll < PotionHelper.POTION_COMMON_CHANCE) {
            return getCertainRarityRandomPotion(AbstractPotion.PotionRarity.COMMON);
        } else {
            return roll < PotionHelper.POTION_UNCOMMON_CHANCE + PotionHelper.POTION_COMMON_CHANCE ?
                    getCertainRarityRandomPotion(AbstractPotion.PotionRarity.UNCOMMON) :
                    getCertainRarityRandomPotion(AbstractPotion.PotionRarity.RARE);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ObtainPotionAction(getRandomPotion()));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            this.isEthereal = false;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PotionOfWhorl();
    }
}
