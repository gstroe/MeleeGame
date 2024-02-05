package melee_sim.manuvers;
import melee_sim.*;

// attack manuever
public class Flourish extends Maneuver {
    public Flourish(int myFavor) {
        favor = myFavor;
        name = "Flourish";
        type = "Ability";
        interruptable = true;
        priority = 3;
        flour_favor = 3;
    }
    public boolean check(Creature Character, Creature Opponent){
        attempts++;
        // first check if countered
        if (Opponent.maneuver_pass && Opponent.selected_manuever.counters.contains(type)) {
            System.out.println(" > "+Opponent.name+" Countered " + Character.name+"\'s "+name+"!!!");
            countered_count++;
            return false;
        }
        return true;
    }
    public boolean perform(Creature Character, Creature Opponent) {
        succeeded++;
        // change flourished status
        System.out.println(" > "+Character.name+" started Flourishing.");
        Character.flourished = true;
        return true;
    }

}