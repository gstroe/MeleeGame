package melee_sim.manuvers;
import java.util.Random;
import melee_sim.*;

// Strike manuever
public class Strike extends Maneuver {
    public Strike(int myFavor) {
        favor = myFavor;
        name = "Strike";
        type = "Attack";
        interruptable = true;
        priority = 2;
        flour_favor = 6;
    }
    public boolean check(Creature Character, Creature Opponent){
        attempts++;
        // first check if countered
        if (Opponent.maneuver_pass && Opponent.selected_manuever.counters.contains(type)) {
            System.out.println(" > "+Opponent.name+" Countered " + Character.name+"\'s "+name+"!!!");
            countered_count++;
            // done, stop flourishing
            if(Character.flourished) {
                System.out.println(" > "+Character.name+" done Flourishing.");
                Character.flourished = false;
            }
            return false;
        }
        // roll
        Random rand = new Random(System.currentTimeMillis());
        int roll = rand.nextInt(20)+1;
        int rollmod = 0;
        if (Character.flourished) { rollmod += 2; }// add two if this is a heavy attack
        rollmod += Character.get_attr("Dexterity") + Character.get_skill(name);
        System.out.println(Character.name+" rolled a "+(int)(roll+rollmod)+" ("+roll+"+"+rollmod+") versus "+Opponent.name+" Defense of "+Opponent.get_defense());
        roll += rollmod;
        // check to see if roll is greater then defence
        if (roll >= Opponent.get_defense()) {
            System.out.println(" > That's a Success!");
            if (Character.flourished) { interruptable = false; } // if flourished its no longer interuptable
            return true;
        } else {
            System.out.println(" > That's a Failure!");
            // done, stop flourishing
            if(Character.flourished) {
                System.out.println(" > "+Character.name+" done Flourishing.");
                Character.flourished = false;
            }
            return false;
        }
    }
    public boolean perform(Creature Character, Creature Opponent) {
        succeeded++;
        // roll damage
        Random rand = new Random(System.currentTimeMillis());
        int roll = 0;
        int rollmod = 0;
        int[] dice;
        // check if its a heavy or light attack
        if(Character.flourished) { dice = Character.dam_heavy; }
        else { dice = Character.dam_light; }
        for (int i=0; i<dice[0]; i++) {
            roll += rand.nextInt(dice[1])+1;
        }
        if(Character.flourished) {
            rollmod += 2*Character.get_damage_bonus();
        }
        else {
            rollmod += Character.get_damage_bonus();
        }


        System.out.println(Character.name+" dealt "+(int)(roll+rollmod)+" ("+roll+"+"+rollmod+") damage to "+Opponent.name);
        Opponent.damage(roll+rollmod, dice[2]);

        // done, stop flourishing
        if(Character.flourished) {
            System.out.println(" > "+Character.name+" done Flourishing.");
            Character.flourished = false;
        }
        return true;
    }

}