package melee_sim.manuvers;
import java.util.Random;
import melee_sim.*;

// attack manuever
public class Counter_Strike extends Maneuver {
    public Counter_Strike(int myFavor) {
        favor = myFavor;
        name = "Counter_Strike";
        type = "Attack";
        counters = "Attack";
        interruptable = false;
        priority = 1;
        flour_favor = 1;
    }
    public boolean check(Creature Character, Creature Opponent){
        attempts++;
        if (Opponent.selected_manuever.type == "Attack" && !Opponent.selected_manuever.name.contains("Counter") && !Opponent.maneuver_pass) { // if oppenet attacking with an none counter ability
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
        } else {
            System.out.println("No attacks, "+name+" failed!");
            Character.status.add("Exhausted");
            System.out.println(" > "+Character.name+" Exhausted.");
            return false;
        }
    }
    public boolean perform(Creature Character, Creature Opponent) {
        succeeded++;
        // roll damage
        Random rand = new Random(System.currentTimeMillis());
        int roll = 0;
        int rollmod = 0;
        int[] dice = Character.dam_light;
        for (int i=0; i<dice[0]+1; i++) {
            roll += rand.nextInt(dice[1])+1;
        }
        rollmod += Character.get_damage_bonus();

        System.out.println(Character.name+" dealt "+(int)(roll+rollmod)+" ("+roll+"+"+rollmod+") damage to "+Opponent.name);
        Opponent.damage(roll+rollmod, dice[2]);

        return true;
    }

}