package vfx.combat;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;

public class BlueShieldNumberEffect extends DamageNumberEffect {
    private Color originalColor;

    public BlueShieldNumberEffect(AbstractCreature target, float x, float y, int amt) {
        super(target, x, y, amt);
        this.color = Settings.BLUE_TEXT_COLOR.cpy();
        this.originalColor = this.color.cpy();
    }

    public void update() {
        super.update();
        this.color = this.originalColor.cpy();
    }
}
