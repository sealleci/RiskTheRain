package lunar;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface LunarRelic {
    int getLunarPrice();

    default boolean isAtBoss() {
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (m.type == AbstractMonster.EnemyType.BOSS) {
                return true;
            }
        }
        return false;
    }

    default String getLevelId(int n) {
        switch (n) {
            case 2:
                return "TheCity";
            case 3:
                return "TheBeyond";
            case 4:
                return "TheEnding";
            case 1:
            default:
                return "Exordium";
        }
    }
}
