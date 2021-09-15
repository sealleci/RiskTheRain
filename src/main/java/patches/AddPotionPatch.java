package patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.FlashPotionEffect;
import potions.EffigyOfGrief;
import potions.GlowingMeteorite;
import potions.HelfireTincture;
import potions.SpinelTonic;

public class AddPotionPatch {
    @SpireEnum
    public static AbstractPotion.PotionSize HELFIRE_TINCTURE;
    @SpireEnum
    public static AbstractPotion.PotionSize SPINEL_TONIC;
    @SpireEnum
    public static AbstractPotion.PotionSize EFFIGY_OF_GRIEF;
    @SpireEnum
    public static AbstractPotion.PotionSize GLOWING_METEORITE;

    @SpirePatch(cls = "com.megacrit.cardcrawl.potions.AbstractPotion", method = "initializeImage")
    public static class AddCustomPotionShapePatch {
        public static void Postfix(AbstractPotion __instance) {
            if (__instance.size == HELFIRE_TINCTURE) {
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "containerImg", HelfireTincture.CONTAINER);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "liquidImg", HelfireTincture.LIQUID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "hybridImg", HelfireTincture.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "spotsImg", HelfireTincture.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "outlineImg", HelfireTincture.OUTLINE);
            } else if (__instance.size == SPINEL_TONIC) {
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "containerImg", SpinelTonic.CONTAINER);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "liquidImg", SpinelTonic.LIQUID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "hybridImg", SpinelTonic.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "spotsImg", SpinelTonic.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "outlineImg", SpinelTonic.OUTLINE);
            } else if (__instance.size == EFFIGY_OF_GRIEF) {
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "containerImg", EffigyOfGrief.CONTAINER);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "liquidImg", EffigyOfGrief.LIQUID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "hybridImg", EffigyOfGrief.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "spotsImg", EffigyOfGrief.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "outlineImg", EffigyOfGrief.OUTLINE);
            } else if (__instance.size == GLOWING_METEORITE) {
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "containerImg", GlowingMeteorite.CONTAINER);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "liquidImg", GlowingMeteorite.LIQUID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "hybridImg", GlowingMeteorite.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "spotsImg", GlowingMeteorite.HYBRID);
                ReflectionHacks.setPrivate(__instance, AbstractPotion.class, "outlineImg", GlowingMeteorite.OUTLINE);
            }
        }
    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.vfx.FlashPotionEffect", method = SpirePatch.CONSTRUCTOR)
    public static class AddCustomPotionFlashEffectPatch {
        public static void Postfix(FlashPotionEffect __instance, AbstractPotion p) {
            if (p.size == HELFIRE_TINCTURE) {
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "containerImg", HelfireTincture.CONTAINER);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "liquidImg", HelfireTincture.LIQUID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "hybridImg", HelfireTincture.HYBRID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "spotsImg", HelfireTincture.HYBRID);
            } else if (p.size == SPINEL_TONIC) {
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "containerImg", SpinelTonic.CONTAINER);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "liquidImg", SpinelTonic.LIQUID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "hybridImg", SpinelTonic.HYBRID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "spotsImg", SpinelTonic.HYBRID);
            } else if (p.size == EFFIGY_OF_GRIEF) {
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "containerImg", EffigyOfGrief.CONTAINER);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "liquidImg", EffigyOfGrief.LIQUID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "hybridImg", EffigyOfGrief.HYBRID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "spotsImg", EffigyOfGrief.HYBRID);
            } else if (p.size == GLOWING_METEORITE) {
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "containerImg", GlowingMeteorite.CONTAINER);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "liquidImg", GlowingMeteorite.LIQUID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "hybridImg", GlowingMeteorite.HYBRID);
                ReflectionHacks.setPrivate(__instance, FlashPotionEffect.class, "spotsImg", GlowingMeteorite.HYBRID);
            }
        }
    }
}
