package melee_sim.manuvers;
import melee_sim.*;

// attack manuever
public class Rest extends Maneuver {
    public Rest(int myFavor) {
        favor = myFavor;
        name = "Rest";
        type = "Ability";
        priority = 3;
    }
    public boolean check(Creature Character, Creature Opponent){
        attempts++;
        return true;
    }
    public boolean perform(Creature Character, Creature Opponent) {
        succeeded++;
        Character.lose_stress();
        return true;
    }

}