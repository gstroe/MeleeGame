package melee_sim;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import melee_sim.manuvers.*;

public class Creature {
    // declare abunch of starting values
    public String name = "";
    protected Map<String,Integer> attributes;
    protected Map<String,Integer> skills;
    protected Map<String,Integer> defiances;
    protected Map<String,Integer> stats;
    protected List<Maneuver> manuver_list;
    public int[] dam_light = {1,4,0};
    public int[] dam_heavy = {3,4,1};
    public String stance = "";
    public List<String> status;
    public boolean flourished = false;
    public Maneuver selected_manuever;
    public boolean maneuver_pass;
    public int prior_pen = 0; // priority peneltly. Should always be reset to zero each turn

    // private method to check if attributes and defiances exist correctly.
    private boolean check_stats() {
        boolean check = true; // set to false if something failed
        if (!attributes.containsKey("Constitution")) { attributes.put("Constitution", 0); check = false; }
        if (!attributes.containsKey("Strength")) { attributes.put("Strength", 0); check = false; }
        if (!attributes.containsKey("Dexterity")) { attributes.put("Dexterity", 0); check = false; }
        if (!attributes.containsKey("Focus")) { attributes.put("Focus", 0); check = false; }
        if (!attributes.containsKey("Wisdom")) { attributes.put("Wisdom", 0); check = false; }
        if (!attributes.containsKey("Charisma")) { attributes.put("Charisma", 0); check = false; }
        if (!attributes.containsKey("Cunning")) { attributes.put("Cunning", 0); check = false; }
        if (!attributes.containsKey("Intelligence")) { attributes.put("Intelligence", 0); check = false; }
        if (!defiances.containsKey("Fortitude")) { defiances.put("Fortitude", 10+attributes.get("Constitution")+attributes.get("Strength")); check = false; }
        if (!defiances.containsKey("Reflex")) { defiances.put("Reflex", 10+attributes.get("Dexterity")+attributes.get("Focus")); check = false; }
        if (!defiances.containsKey("Willpower")) { defiances.put("Willpower", 10+attributes.get("Wisdom")+attributes.get("Charisma")); check = false; }
        if (!defiances.containsKey("Reason")) { defiances.put("Reason", 10+attributes.get("Focus")+attributes.get("Wisdom")); check = false; }
        if (!defiances.containsKey("Perception")) { defiances.put("Perception", 10+attributes.get("Constitution")+attributes.get("Strength")); check = false; }
        if (!defiances.containsKey("Composure")) { defiances.put("Composure", 10+attributes.get("Charisma")+attributes.get("Cunning")); check = false; }
        if (!stats.containsKey("Max_Health")) { stats.put("Max_Health", 0); }
        if (!stats.containsKey("Max_Stress")) { stats.put("Max_Stress", 3); }
        if (!stats.containsKey("Defense")) { stats.put("Defense", 10); }
        if (!stats.containsKey("Guard_Defense")) { stats.put("Guard_Defense", defiances.get("Reflex")); }
        if (!stats.containsKey("Damage_Bonus")) { stats.put("Damage_Bonus", attributes.get("Strength")); }

        return check; // if something was missing fail
    }

    // private method to recalculated from attributes stats (should be called anytime an atribute changes)
    private void recalculate() {
        // error check first
        check_stats();
        defiances.replace("Fortitude", 10+attributes.get("Constitution")+attributes.get("Strength"));
        defiances.replace("Reflex", 10+attributes.get("Dexterity")+attributes.get("Focus"));
        defiances.replace("Willpower", 10+attributes.get("Wisdom")+attributes.get("Charisma"));
        defiances.replace("Reason", 10+attributes.get("Focus")+attributes.get("Wisdom"));
        defiances.replace("Perception", 10+attributes.get("Constitution")+attributes.get("Strength"));
        defiances.replace("Composure", 10+attributes.get("Charisma")+attributes.get("Cunning"));

        // max health
        if (attributes.get("Constitution") > 0) {
            stats.replace("Max_Health", attributes.get("Constitution")*5 + 25);
        } else {
            stats.replace("Max_Health",25);
        }

        // defense
        stats.replace("Defense", 10);
        stats.replace("Guard_Defense", defiances.get("Reflex"));

        // stress
        stats.replace("Max_Stress", 3);

        // Damage Bonus
        stats.replace("Damage_Bonus", attributes.get("Strength"));
    }

    // constructors
    // this one lets you add atributes right away
    public Creature (String my_name,int con, int str, int dex, int foc, int wis, int cha, int cun, int inte) {
        name = my_name;
        // create lists
        attributes = new HashMap<String,Integer>();
        defiances = new HashMap<String,Integer>();
        skills = new HashMap<String,Integer>();
        stats = new HashMap<String,Integer>();
        manuver_list = new ArrayList<Maneuver>();
        status = new ArrayList<String>();
        // set attributes
        attributes.put("Constitution", con);
        attributes.put("Strength", str);
        attributes.put("Dexterity", dex);
        attributes.put("Focus", foc);
        attributes.put("Wisdom", wis);
        attributes.put("Charisma", cha);
        attributes.put("Cunning", cun);
        attributes.put("Intelligence", inte);
        stats.put("Max_Health", 0);
        stats.put("Max_Stress", 3);
        stats.put("Defense", 10);
        stats.put("Guard_Defense", 10);
        recalculate(); // calculate

        stats.put("Current_Health", stats.get("Max_Health"));
        stats.put("Current_Stress", 0);

        // add manuvers
        manuver_list.add(new Strike(2));
        manuver_list.add(new Flourish(1));
        manuver_list.add(new Counter_Strike(1));
        manuver_list.add(new Dodge(1));
        manuver_list.add(new Rest(1));
    }
    // this one creates a creature with no atribute changes
    public Creature (String my_name) {
        this(my_name,0,0,0,0,0,0,0,0);
    }

    // all gets
    public int get_attr(String name) { return attributes.get(name); }
    public int get_defi(String name) { return defiances.get(name); }
    public int get_skill(String name) {
        int result = 0;
        if (skills.containsKey("Bonus_"+name)) {
            result += skills.get("Bonus_"+name);
        }
        if (skills.containsKey(name)) {
            result += skills.get(name);
        }
        return result;
    }
    public int get_health() { return stats.get("Current_Health"); }
    public int get_stress() { return stats.get("Current_Stress"); }
    public int get_max_stress() { return stats.get("Max_Stress"); }
    public int get_defense() {
        if (stance.equals("Guard")) {
            return stats.get("Guard_Defense");
        } else {
            return stats.get("Defense");
        }
    }
    //all sets
    public boolean set_attr(String name, int value) {
        if (!attributes.containsKey(name)) { return false; } // fail if doesnt exist
        attributes.replace(name, value);
        return true;
    }
    public boolean set_defi(String name, int value) {
        if (!defiances.containsKey(name)) { return false; } // fail if doesnt exist
        defiances.replace(name, value);
        return true;
    }
    public boolean set_skill(String name, int value) {
        if (!skills.containsKey(name)) { return false; } // fail if doesnt exist
        skills.replace(name, value);
        return true;
    }
    // special increments
    public boolean damage(int dam, int structure) { // returns false if dead
        stats.replace("Current_Health", stats.get("Current_Health")-dam);
        if (stats.get("Current_Health") < 1) { return false; }
        else if (stats.get("Current_Health") > stats.get("Max_Health")) { stats.replace("Current_Health", stats.get("Max_health")); }
        return true;
    }
    public int gain_stress() {
        stats.replace("Current_Stress", stats.get("Current_Stress")+1);
        if (stats.get("Current_Stress") < 0) { stats.replace("Current_Stress", 0); }
        return stats.get("Current_Stress");
    }
    public int lose_stress() {
        stats.replace("Current_Stress", stats.get("Current_Stress")-1);
        if (stats.get("Current_Stress") < 0) { stats.replace("Current_Stress", 0); }
        return stats.get("Current_Stress");
    }
    public int get_damage_bonus() {
        return stats.get("Damage_Bonus");
    }

    // NPC select maneuver and show chances or not
    public Maneuver npc_select_maneuver(boolean show_chance, boolean oppo_exhaust) {
        ArrayList<String> select_list = new ArrayList<String>();
        for (Maneuver maneuver : manuver_list) { // for each maneuver
            // skip some manuevers in certain cases
            if (maneuver.name.equals("Rest") && get_stress() == 0) {}
            else {
                if (flourished) { // if flourish add only flourish priority
                    if (oppo_exhaust) {
                        if ( maneuver.name.equals("Strike")) {
                            System.out.println("IT SHOULD HAPPEN");
                            select_list.add("Strike");
                        }
                    } else {
                        for (int i=1; i<=maneuver.flour_favor; i++) { // add a instance for each favor
                            select_list.add(maneuver.name);
                        }
                    }
                } else {
                    if (oppo_exhaust) {
                        for (int i=1; i<=maneuver.flour_favor; i++) { // add a instance for each favor
                            select_list.add(maneuver.name);
                        }
                    } else {
                        for (int i=1; i<=maneuver.favor; i++) { // add a instance for each favor
                            select_list.add(maneuver.name);
                        }
                    }
                }
            }
        }
        // now generate random number based on the list
        Random rand = new Random(System.currentTimeMillis());
        int pick = rand.nextInt(select_list.size());
        Maneuver select = null;
        if (show_chance) { System.out.println(name+" has a chance to: "); }
        for (Maneuver maneuver : manuver_list) { // if your name selected is the same as the maneuver, use that one
            if (maneuver.name.equals("Rest") && get_stress() == 0) {}
            else {
                int favor_type = 0;
                if (flourished && oppo_exhaust) {
                    if (maneuver.name.equals("Strike")) { favor_type = 1; }
                    else { favor_type = 0; }
                }
                else if (flourished || oppo_exhaust) { favor_type = maneuver.flour_favor; }
                else { favor_type = maneuver.favor; }
                if (favor_type != 0) {
                    if (show_chance) {
                        System.out.printf("%s-%.0f%%   ",maneuver.name, (double)favor_type/select_list.size()*100);
                    }
                    if (select_list.get(pick).equals(maneuver.name)) { select = maneuver; }
                }
            }
        }
        if (show_chance) { System.out.println(); }
        return select;
    }
    public Maneuver player_select_maneuver(){
        // while legal option not yet selected
        Maneuver select = null;
        boolean legal = false;
        while (!legal) {
            // show selection opption
            System.out.println("~~~ Select the number of the maneuver to use ~~~");
            for (int i=0; i<manuver_list.size(); i++) {
                System.out.print((int)(i+1)+":"+manuver_list.get(i).name+"   ");
            }
            System.out.println();

            Scanner sc = new Scanner(System.in);
            char c = sc.nextLine().charAt(0);
            if (c >= 49 && c <= 57 && c-49 < manuver_list.size()) {
                select = manuver_list.get(c-49);
                // check if in stance already
                legal = true;
            }
            else {
                System.out.println(c+" not a valid option");
            }
        }
        return select;
    }
}