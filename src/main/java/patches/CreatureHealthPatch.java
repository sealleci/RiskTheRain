package patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;
import powers.ScarabRecoveryPower;
import riskTheRain.RiskTheRain;
import vfx.combat.BlueShieldNumberEffect;

public class CreatureHealthPatch {

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "<class>")
    public static class RTRCreatureFields {
        public static SpireField<Integer> blueShield = new SpireField<>(() -> 0);
        public static SpireField<Integer> maxBlueShield = new SpireField<>(() -> 0);
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "decrementBlock")
    public static class UpdateBlueShieldNumberPatch {
        private static final String RECOVERY_ID = RiskTheRain.decorateId("ScarabRecovery");

        public static int Postfix(AbstractCreature __instance, DamageInfo info, int damageAmount) {
            try {
                int maxShield = RTRCreatureFields.maxBlueShield.get(__instance);
                if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                    int curShield = RTRCreatureFields.blueShield.get(__instance);
                    boolean isBlueShieldReduce = false;
                    if (maxShield > 0) {
                        if (curShield > 0 && info.owner != __instance) {
                            if (damageAmount > curShield) {
                                damageAmount -= curShield;
                                if (Settings.SHOW_DMG_BLOCK) {
                                    AbstractDungeon.effectList.add(new BlueShieldNumberEffect(
                                            __instance,
                                            __instance.hb.cX,
                                            __instance.hb.cY + __instance.hb.height / 2.0F,
                                            curShield)
                                    );
                                }
                                RiskTheRain.clearBlueShield(__instance);
                                isBlueShieldReduce = true;
                            } else if (damageAmount == curShield) {
                                RiskTheRain.clearBlueShield(__instance);
                                if (Settings.SHOW_DMG_BLOCK) {
                                    AbstractDungeon.effectList.add(new BlueShieldNumberEffect(
                                            __instance,
                                            __instance.hb.cX,
                                            __instance.hb.cY + __instance.hb.height / 2.0F,
                                            damageAmount)
                                    );
                                }
                                damageAmount = 0;
                                isBlueShieldReduce = true;
                            } else {
                                if (damageAmount > 0) {
                                    isBlueShieldReduce = true;
                                }
                                RiskTheRain.addBlueShield(__instance, damageAmount * -1);
                                if (Settings.SHOW_DMG_BLOCK) {
                                    AbstractDungeon.effectList.add(new BlueShieldNumberEffect(
                                            __instance,
                                            __instance.hb.cX,
                                            __instance.hb.cY + __instance.hb.height / 2.0F,
                                            damageAmount)
                                    );
                                }
                                damageAmount = 0;
                            }
                        }
                        ReflectionHacks.privateMethod(AbstractCreature.class, "healthBarUpdatedEvent").invoke(__instance);
                        if (isBlueShieldReduce) {
                            ScarabRecoveryPower recovery = (ScarabRecoveryPower) __instance.getPower(RECOVERY_ID);
                            if (recovery != null) {
                                recovery.resetTurn();
                            }
                        }
                    }
                } else {
                    if (maxShield > 0 && damageAmount >= __instance.currentHealth) {
                        damageAmount = __instance.currentHealth - 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return damageAmount;
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "healthBarRevivedEvent")
    public static class HealthBarRevivedEventPatch {
        public static void Postfix(AbstractCreature __instance) {
            int curShield = RTRCreatureFields.blueShield.get(__instance);
            int maxShield = RTRCreatureFields.maxBlueShield.get(__instance);
            if (maxShield > 0) {
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth",
                        __instance.hb.width * (__instance.currentHealth + curShield) / (__instance.maxHealth + maxShield));
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "healthBarWidth",
                        ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth"));
//                System.out.println((Float) ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "healthBarWidth"));
            }
        }
    }

    //采用后缀的话，healthBarWidth的原来数值会因5/5满体力而被覆盖
    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "healthBarUpdatedEvent")
    public static class HealthBarUpdatedEventPatch {
        public static void Replace(AbstractCreature __instance) {
            int curShield = RTRCreatureFields.blueShield.get(__instance);
            int maxShield = RTRCreatureFields.maxBlueShield.get(__instance);
            ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "healthBarAnimTimer", 1.2f);

            if (maxShield > 0) {
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth",
                        __instance.hb.width * (__instance.currentHealth + curShield) / (__instance.maxHealth + maxShield));
            } else {
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth",
                        __instance.hb.width * __instance.currentHealth / __instance.maxHealth);
            }

            if (__instance.currentHealth + curShield == __instance.maxHealth + maxShield) {
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "healthBarWidth",
                        ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth"));
            } else if (__instance.currentHealth + curShield <= 0) {
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "healthBarWidth", 0.0f);
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth", 0.0f);
            }
            if ((Float) ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth") >
                    (Float) ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "healthBarWidth")) {
                ReflectionHacks.setPrivate(__instance, AbstractCreature.class, "healthBarWidth",
                        ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth"));
            }
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "renderHealthBg")
    public static class RenderHealthBgPatch {
        public static void Postfix(AbstractCreature __instance, SpriteBatch sb, float x, float y) {
            int curShield = RTRCreatureFields.blueShield.get(__instance);
            int maxShield = RTRCreatureFields.maxBlueShield.get(__instance);
            float HEALTH_BAR_HEIGHT = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "HEALTH_BAR_HEIGHT");
            float HEALTH_BAR_OFFSET_Y = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "HEALTH_BAR_OFFSET_Y");
            if (maxShield > 0 && curShield < maxShield) {
                sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, __instance.hb.width, HEALTH_BAR_HEIGHT);
                sb.draw(ImageMaster.HEALTH_BAR_R, x + __instance.hb.width, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
            }
        }
    }


    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "renderHealthText")
    public static class RenderHealthTextPatch {
        public static void Replace(AbstractCreature __instance, SpriteBatch sb, float y) {
            int maxShield = RTRCreatureFields.maxBlueShield.get(__instance);
            if (maxShield <= 0) {
                float HEALTH_BAR_OFFSET_Y = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "HEALTH_BAR_OFFSET_Y");
                float HEALTH_TEXT_OFFSET_Y = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "HEALTH_TEXT_OFFSET_Y");
                Color hbTextColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "hbTextColor");
                if ((Float) ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth") != 0.0F) {
                    float tmp = hbTextColor.a;
                    hbTextColor.a *= (float) ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "healthHideTimer");
                    FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, __instance.currentHealth + "/" + __instance.maxHealth, __instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y + 5.0F * Settings.scale, hbTextColor);
                    hbTextColor.a = tmp;
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, AbstractCreature.TEXT[0], __instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y - 1.0F * Settings.scale, hbTextColor);
                }
            }
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.core.AbstractCreature", method = "renderHealth")
    public static class RenderBlueShieldBarPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"x", "y"})
        public static void Insert(AbstractCreature __instance, SpriteBatch sb, float x, float y) {
            try {
                if (Settings.hideCombatElements) {
                    return;
                }

                int curShield = RTRCreatureFields.blueShield.get(__instance);
                int maxShield = RTRCreatureFields.maxBlueShield.get(__instance);
                Color grayColor = new Color(208 / 255.0f, 229 / 255.0f, 229 / 255.0f, 1.0f);
                Color blueColor = new Color(72 / 255.0f, 94 / 255.0f, 181 / 255.0f, 1.0f);
                Color greenColor = new Color(72 / 255.0f, 181 / 255.0f, 87 / 255.0f, 1.0f);
                Color orangeHbBarColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "orangeHbBarColor");
                Color greenHbBarColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "greenHbBarColor");
                Color blueHbBarColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "blueHbBarColor");
                Color redHbBarColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "redHbBarColor");
                Color hbTextColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "hbTextColor");
                float healthBarWidth = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "healthBarWidth");
                float targetHealthBarWidth = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "targetHealthBarWidth");
                float hbYOffset = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "hbYOffset");
                float healthHideTimer = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "healthHideTimer");
                float HEALTH_BAR_HEIGHT = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "HEALTH_BAR_HEIGHT");
                float HEALTH_BAR_OFFSET_Y = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "HEALTH_BAR_OFFSET_Y");
                float HEALTH_TEXT_OFFSET_Y = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "HEALTH_TEXT_OFFSET_Y");

//                float x = __instance.hb.cX - __instance.hb.width / 2.0F;
//                float y = __instance.hb.cY - __instance.hb.height / 2.0F + hbYOffset;

                float originBlueWidth = healthBarWidth;
                float blueWidth = __instance.hb.width * (__instance.currentHealth + curShield) / (__instance.maxHealth + maxShield);
                float originRedWidth = healthBarWidth * __instance.currentHealth / (__instance.currentHealth + curShield);
                float redWidth = __instance.hb.width * __instance.currentHealth / (__instance.maxHealth + maxShield);
                if (__instance.currentHealth + curShield == __instance.maxHealth + maxShield) {
                    originBlueWidth = blueWidth;
                } else if (__instance.currentHealth + curShield <= 0) {
                    originBlueWidth = 0.0f;
                    blueWidth = 0.0f;
                }
                if (blueWidth > originBlueWidth) {
                    originBlueWidth = blueWidth;
                }

                if (__instance.currentHealth == __instance.maxHealth) {
                    originRedWidth = redWidth;
                } else if (__instance.currentHealth <= 0) {
                    originRedWidth = 0.0f;
                    redWidth = 0.0f;
                }
                if (redWidth > originRedWidth) {
                    originRedWidth = redWidth;
                }

                if (blueWidth != 0.0f) {
                    if (maxShield > 0) {//有虫盾
                        //渲染背景
                        ReflectionHacks.privateMethod(AbstractCreature.class, "renderHealthBg", new Class[]{SpriteBatch.class, float.class, float.class})
                                .invoke(__instance, new Object[]{sb, x, y});

                        if (curShield > 0) {//虫盾不为0
                            //灰白条
                            sb.setColor(grayColor);
                            sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, originBlueWidth, HEALTH_BAR_HEIGHT);
                            sb.draw(ImageMaster.HEALTH_BAR_R, x + originBlueWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            //蓝条
                            if (__instance.currentBlock > 0) {
                                sb.setColor(greenColor);
                            } else {
                                sb.setColor(blueColor);
                            }
                            if (!__instance.hasPower("Poison")) {
                                if (__instance.currentHealth > 0)
                                    sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, blueWidth, HEALTH_BAR_HEIGHT);
                                sb.draw(ImageMaster.HEALTH_BAR_R, x + blueWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            } else {
                                int poisonAmt = (__instance.getPower("Poison")).amount;
                                if (poisonAmt > 0 && __instance.hasPower("Intangible"))
                                    poisonAmt = 1;
                                if (__instance.currentHealth > poisonAmt) {
                                    float w = 1.0F - (__instance.currentHealth - poisonAmt) / (__instance.currentHealth * 1.0f);
                                    w *= originBlueWidth;
                                    if (__instance.currentHealth > 0)
                                        sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                    sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, blueWidth - w, HEALTH_BAR_HEIGHT);
                                    sb.draw(ImageMaster.HEALTH_BAR_R, x + blueWidth - w, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                }
                            }
                        } else {
                            //橙条
                            sb.setColor(orangeHbBarColor);
                            sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, originRedWidth, HEALTH_BAR_HEIGHT);
                            sb.draw(ImageMaster.HEALTH_BAR_R, x + originRedWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);

                            //绿条
                            if (__instance.hasPower("Poison")) {
                                sb.setColor(greenHbBarColor);
                                if (__instance.currentHealth > 0) {
                                    sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                }
                                sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, redWidth, HEALTH_BAR_HEIGHT);
                                sb.draw(ImageMaster.HEALTH_BAR_R, x + redWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            }
                        }
                        //红条
                        if (__instance.currentBlock > 0) {
                            sb.setColor(blueHbBarColor);
                        } else {
                            sb.setColor(redHbBarColor);
                        }
                        if (curShield > 0) {//虫盾
                            if (__instance.currentHealth > 0)
                                sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, redWidth, HEALTH_BAR_HEIGHT);
                            sb.draw(ImageMaster.HEALTH_BAR_R, x + redWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                        } else {
                            if (!__instance.hasPower("Poison")) {
                                if (__instance.currentHealth > 0)
                                    sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, redWidth, HEALTH_BAR_HEIGHT);
                                sb.draw(ImageMaster.HEALTH_BAR_R, x + redWidth, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                            } else {
                                int poisonAmt = (__instance.getPower("Poison")).amount;
                                if (poisonAmt > 0 && __instance.hasPower("Intangible")) {
                                    poisonAmt = 1;
                                }
                                if (__instance.currentHealth > poisonAmt) {
                                    float w = 1.0F - (__instance.currentHealth - poisonAmt) / (__instance.currentHealth * 1.0f);
                                    w *= originRedWidth;
                                    if (__instance.currentHealth > 0) {
                                        sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                    }
                                    sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, redWidth - w, HEALTH_BAR_HEIGHT);
                                    sb.draw(ImageMaster.HEALTH_BAR_R, x + redWidth - w, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                                }
                            }
                        }
                    }
//                    else {//没虫盾
//                        if (targetHealthBarWidth != 0.0F) {
//                            ReflectionHacks.privateMethod(AbstractCreature.class, "renderOrangeHealthBar", new Class[]{SpriteBatch.class, float.class, float.class})
//                                    .invoke(__instance, new Object[]{sb, x, y});
//                            if (__instance.hasPower("Poison")) {
//                                ReflectionHacks.privateMethod(AbstractCreature.class, "renderGreenHealthBar", new Class[]{SpriteBatch.class, float.class, float.class})
//                                        .invoke(__instance, new Object[]{sb, x, y});
//                            }
//                            ReflectionHacks.privateMethod(AbstractCreature.class, "renderRedHealthBar", new Class[]{SpriteBatch.class, float.class, float.class})
//                                    .invoke(__instance, new Object[]{sb, x, y});
//                        }
//                    }
                }

//                if (__instance.currentBlock != 0 && __instance.hbAlpha != 0.0F) {
//                    ReflectionHacks.privateMethod(AbstractCreature.class, "renderBlockOutline", new Class[]{SpriteBatch.class, float.class, float.class})
//                            .invoke(__instance, new Object[]{sb, x, y});
//                }

                //字体
                if (maxShield > 0) {
                    if (blueWidth != 0.0F) {
                        float tmp = hbTextColor.a;
                        hbTextColor.a *= healthHideTimer;
                        FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, (__instance.currentHealth + curShield) + "/" + __instance.maxHealth, __instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y + 5.0F * Settings.scale, hbTextColor);
                        hbTextColor.a = tmp;
                    } else {
                        FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, AbstractCreature.TEXT[0], __instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y - Settings.scale, hbTextColor);
                    }
                }
//                else {
//                    ReflectionHacks.privateMethod(AbstractCreature.class, "renderHealthText", new Class[]{SpriteBatch.class, float.class})
//                            .invoke(__instance, new Object[]{sb, y});
//                }

//                if (__instance.currentBlock != 0 && __instance.hbAlpha != 0.0F) {
//                    ReflectionHacks.privateMethod(AbstractCreature.class, "renderBlockIconAndValue", new Class[]{SpriteBatch.class, float.class, float.class})
//                            .invoke(__instance, new Object[]{sb, x, y});
//                }
//                ReflectionHacks.privateMethod(AbstractCreature.class, "renderPowerIcons", new Class[]{SpriteBatch.class, float.class, float.class})
//                        .invoke(__instance, new Object[]{sb, x, y});

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "renderHealthText");
                return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
            }
        }
    }
}
