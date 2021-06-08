package melee_sim;
import java.util.ArrayList;

public class MeleeSim {
    private static final int wait_count = 600;
    public static void main(final String[] args) {
        // lets get our cast of characters
        ArrayList<Creature> cast = new ArrayList<Creature>();
        cast.add(new Creature("Gabe", 3, 2, 3, 2, 3, 2, 3, 2));
        cast.add(new Creature("Orc", 1, 4, 1, 4, 1, 4, 1, 4));

        boolean death = false;
        int round = 1;
        // lets loop through and do the things
        while (!death) {
            // check exhausted
            for (Creature actor : cast) {
                if (actor.get_stress() > actor.get_max_stress()) {
                    actor.lose_stress();
                    actor.status.add("Exhausted");
                }
            }

            System.out.println("Round "+round);
            for (Creature actor : cast) {actor.maneuver_pass = false;}
            for (Creature actor : cast) {
                System.out.print(actor.name+"   Health: "+actor.get_health()+"   Stress: "+actor.get_stress()+"   Effects:");
                for (String status : actor.status) {
                    System.out.print(" "+status);
                }
                System.out.println();
                wait(wait_count);
            }
            for (Creature actor : cast) {
                if (!actor.equals(cast.get(0))) { // skip the player for now
                    boolean p1 = false; // check if  the player is exhausted
                    if (cast.get(0).status.contains("Exhausted") && !actor.status.contains("Exhausted")) { p1 = true; }
                    actor.selected_manuever = actor.npc_select_maneuver(true, p1); // add a select manuever function later
                }
            }
            //now do the player selection
            cast.get(0).selected_manuever = cast.get(0).player_select_maneuver();

            // check exhausted status
            for (Creature actor : cast) {
                if (actor.status.contains("Exhausted")) {
                    actor.status.remove("Exhausted");
                    actor.prior_pen = 3;
                }
            }

            // anounce manuevers
            for (Creature actor : cast) {
                System.out.println(actor.name+" Selected: "+actor.selected_manuever.name+" ("+(int)(actor.selected_manuever.priority+actor.prior_pen)+")");
                wait(wait_count);
                // Check if flourished and not attack
                if(actor.flourished && !actor.selected_manuever.name.equals("Strike")) {
                    System.out.println(" > "+actor.name+" done Flourishing.");
                    actor.gain_stress();
                    actor.flourished = false;
                }
            }



            // add to the list only if the ability prioity is off that number so that things happen at the correct priority
            for (int i=0; i<10; i++) {
                ArrayList<Creature> players = new ArrayList<Creature>();
                for (Creature actor : cast) {
                    if((actor.selected_manuever.priority+actor.prior_pen) == i) {
                        players.add(actor);
                    }
                }

                // do check
                for (Creature actor : players) {
                    int other;
                    if (actor.equals(cast.get(0))) { other = 1; } // if this is 0 the other is 1
                    else { other = 0; }
                    actor.maneuver_pass = actor.selected_manuever.check(actor, cast.get(other));
                    wait(wait_count);
                }

                // check interrupt then do action
                for (Creature actor : players) {
                    if (actor.maneuver_pass) {
                        int other;
                        if (actor.equals(cast.get(0))) { other = 1; } // if this is 0 the other is 1
                        else { other = 0; }
                        if (!actor.selected_manuever.interupted(cast.get(other))) {
                            actor.selected_manuever.perform(actor, cast.get(other));
                        }
                        else {
                            System.out.println(" > "+cast.get(other).name+" Interrupted "+actor.name+"!");
                            actor.flourished = false;
                        }
                        wait(wait_count);
                    }
                }
            }

            // check death
            for (Creature actor : cast) {
                if (actor.get_health() < 1) {
                    System.out.println(" > "+actor.name+" has died!");
                    death = true;
                }
            }

            // Reset prior_pen per character
            for (Creature actor : cast) { actor.prior_pen = 0; }

            round++;
            System.out.println("");
        }
        // print final stats
        for (Creature actor : cast) {
            System.out.println("For "+actor.name+" the following was used:");
            for (int i=0; i<actor.manuver_list.size(); i++) {
                if (actor.manuver_list.get(i).attempts > 0) {
                    System.out.printf(" > %s: Completed %d out of %d attempts (%.0f%%)  Interupted: %d (%.0f%%)   Countered: %d (%.0f%%) \n",
                    actor.manuver_list.get(i).name,
                    actor.manuver_list.get(i).succeeded,
                    actor.manuver_list.get(i).attempts,
                    (double)actor.manuver_list.get(i).succeeded/actor.manuver_list.get(i).attempts*100,
                    actor.manuver_list.get(i).interupt_count,
                    (double)actor.manuver_list.get(i).interupt_count/actor.manuver_list.get(i).attempts*100,
                    actor.manuver_list.get(i).countered_count,
                    (double)actor.manuver_list.get(i).countered_count/actor.manuver_list.get(i).attempts*100);
                } else if (actor.manuver_list.get(i).priority != 0) {
                    System.out.println(" > "+actor.manuver_list.get(i).name+": Not Attempted");
                }
            }
        }
    }

    //sleep
    public static void wait(int ms){
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

}