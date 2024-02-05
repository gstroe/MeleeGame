package melee_sim.manuvers;
import java.util.Random;
import melee_sim.*;

// attack manuever
public class Dodge extends Maneuver {
    public Dodge(int myFavor) {
        favor = myFavor;
        name = "Dodge";
        type = "Ability";
        counters = "Attack Grapple";
        interruptable = false;
        priority = 1;
    }
    public boolean check(Creature Character, Creature Opponent){
        attempts++;
        // first check if countered
        if (Opponent.maneuver_pass && Opponent.selected_manuever.counters.contains(type)) {
            System.out.println(" > "+Opponent.name+" Countered " + Character.name+"\'s "+name+"!!!");
            countered_count++;
            return false;
        }
        // cant dodged if grappled
        if (Character.status.contains("Grappled")) {
            System.out.println("Cannot dodge when grappled!!!");
            return false;
        }
        // gain stress
        Character.gain_stress();
        // roll
        Random rand = new Random(System.currentTimeMillis());
        int roll = rand.nextInt(20)+1;
        int rollmod = 0;
        rollmod += Character.get_attr("Dexterity") + Character.get_skill(name);
        System.out.println(Character.name+" rolled a "+(int)(roll+rollmod)+" ("+roll+"+"+rollmod+") versus "+Opponent.name+" Reflex of "+Opponent.get_defi("Reflex"));
        roll += rollmod;
        // check to see if roll is greater then Reflex
        if (roll >= Opponent.get_defi("Reflex")) {
            System.out.println(" > That's a Success!");
            return true;
        } else {
            System.out.println(" > That's a Failure!");
            return false;
        }
    }
    public boolean perform(Creature Character, Creature Opponent) {
        succeeded++;
        return true;
    }

}