package powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import riskTheRain.RiskTheRain;

public class ScarabShieldPower extends AbstractPower {
    private static final String SIGN = "ScarabShield";
    public static final String POWER_ID = RiskTheRain.decorateId(SIGN);
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private int MAX_SHIELD;
    private static final int MAX_TURN = 2;
    public static final String RECOVERY_ID = RiskTheRain.decorateId("ScarabRecovery");
    private static final Logger logger = LogManager.getLogger(RiskTheRain.class.getName());

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public ScarabShieldPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        MAX_SHIELD = amount;
        this.amount = MAX_SHIELD;
        this.region128 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(new Texture(RiskTheRain.getPowerImagePath(SIGN + "32.png")), 0, 0, 32, 32);
        updateDescription();
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            flash();
            addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
        }
    }

    private void gainBlock() {
        try {
            flash();
            int calipersBlock = 15;
            if (!AbstractDungeon.player.hasPower("Barricade") && !AbstractDungeon.player.hasPower("Blur")) {
                //没有壁垒和残影
                if (!AbstractDungeon.player.hasRelic("Calipers")) {
                    //没有外卡钳
                    //正常获得甲虫防御
                    addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
                } else {
                    //有外卡钳
                    if (AbstractDungeon.player.currentBlock + calipersBlock <= MAX_SHIELD) {
                        //加15后当前防御小于等于最大护盾
                        //只增加15
                        addToBot(new GainBlockAction(this.owner, this.owner, calipersBlock));
                    } else {
                        //加15后当前防御大于最大护盾
                        int normalBlock = AbstractDungeon.player.currentBlock + calipersBlock - MAX_SHIELD;
                        addToBot(new GainBlockAction(this.owner, this.owner, this.amount + Math.max(normalBlock - calipersBlock, 0)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDescription() {
        try {
            this.description = String.format(
                    "%s%d%s",
                    DESCRIPTIONS[0],
                    this.amount,
                    DESCRIPTIONS[1]
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stackPower(int amount) {
        this.fontScale = 8.0f;
        this.amount += amount;
        if (this.amount >= MAX_SHIELD) {
            this.amount = MAX_SHIELD;
        }
        if (this.amount <= 0) {
            this.amount = 0;
        }
    }

    @Override
    public void reducePower(int amount) {
        this.fontScale = 8.0f;
        this.amount -= amount;
        if (this.amount >= MAX_SHIELD) {
            this.amount = MAX_SHIELD;
        }
        if (this.amount <= 0) {
            this.amount = 0;
        }
    }

    @Override
    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_PLATED", 0.05F);
    }

    @Override
    public void atStartOfTurn() {
        gainBlock();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        int block = AbstractDungeon.player.currentBlock;
        int remain = block - damageAmount;
//        logger.info(String.format("%d %d,%d", block, damageAmount, remain));
        if (remain < MAX_SHIELD) {
            this.amount = Math.max(remain < 0 ? MathUtils.ceilPositive(remain) : MathUtils.ceil(remain), 0);
            if (AbstractDungeon.player.getPower(RECOVERY_ID) == null) {
                addToBot(new ApplyPowerAction(this.owner, this.owner, new ScarabRecoveryPower(this.owner, MAX_TURN), MAX_TURN));
            }
            updateDescription();
        }
        return damageAmount;
    }

    public int getRecoverCount() {
        return MAX_SHIELD - this.amount;
    }

    public void recover() {
        this.amount = MAX_SHIELD;
        updateDescription();
    }
}
