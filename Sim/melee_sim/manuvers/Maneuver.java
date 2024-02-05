package melee_sim.manuvers;
import melee_sim.*;

abstract public class Maneuver {
    public int adv = 0; // -1 for disadv, 1 for adv, 0 for none
    public String type = "";
    public String name;
    public String counters = "";
    public boolean interruptable = false;
    public int favor;
    public int flour_favor = 0;
    public int priority;
    public int attempts = 0;
    public int succeeded = 0;
    public int interupt_count = 0;
    public int countered_count = 0;

    public boolean interupted(Creature Opponent){
        // check if oopenent interupted you.
        if (Opponent.selected_manuever.type.equals("Attack") && Opponent.maneuver_pass && this.interruptable) { interupt_count++; return true; }
        else { return false; }
    }
    abstract public boolean check(Creature Character, Creature Opponent);
    abstract public boolean perform(Creature Character, Creature Opponent);

}