package data;

public class HeroInfo {
    public long frame;
    public long atk, hp, mana;
    public boolean fleeting;

    public HeroInfo(long frame, long atk, long hp, long mana, boolean fleeting) {
        this.frame = frame;
        this.atk = atk;
        this.hp = hp;
        this.mana = mana;
        this.fleeting = fleeting;
    }
}
