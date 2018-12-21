/*
 * CS 106A Critters
 * The model of all critters in the simulation.
 * The main work is done by the update method, which moves all critters
 * and initiates the various fights and interactions between them.
 * 
 * Performance profiled with Java HProf.
 * To profile heap memory/object usage:
 *   java -Xrunhprof CritterMain
 * To profile CPU cycles:
 *   java -Xrunhprof:cpu=old,thread=y,depth=10,cutoff=0,format=a CritterMain
 * View HProf data with HPjmeter software (Google for it).
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp, Stuart Reges, Steve Gribble
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.model;

import java.awt.Color;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import stanford.cs106.reflect.*;
import stanford.cs106.util.*;

public final class CritterModel extends Observable {  // implements Iterable<Critter> {
    // class constants
    public static final String EMPTY = " "; // how to display empty squares
    public static final String WOLF_CLASS_NAME = "Wolf";
    public static final int DEFAULT_CRITTER_COUNT = 25;
    public static final int DEFAULT_WIDTH = 60;
    public static final int DEFAULT_HEIGHT = 50;

    // how many pieces of food an animal type must eat before being blocked (0 to disable)
    public static final int CRITTER_GLUTTON_COUNT = 1;
    public static /* final */ int CRITTER_CLASS_GLUTTON_COUNT = 0;  // 20;
    public static /* final */ int CRITTER_MOVE_FATIGUE_COUNT = 0;
    public static final int INT_PARAM_MAX = 9;
    public static final int GLUTTON_PENALTY = 20;
    public static final boolean RANDOMIZE_GLUTTON_PENALTY = true;  // not always exactly 20
    public static final int MATING_PENALTY = 20;
    public static final int FOOD_RESPAWN_INTERVAL = 50;
    public static final String FOOD = ".";  // how will food be drawn?
    public static final int FOOD_PERCENTAGE = 5;  // what % squares have food?
    public static final Color FOOD_COLOR = Color.BLACK;
    public static final boolean DISPLAY_BABIES = true;    // if true, temporarily show babies in lowercase

    // largest value that will be passed to the constructor of a critter that 
    // takes an int as a parameter

    // CritterModel's global random number generator
    // (used to use Math dot random, but that can be hacked)
    private static final Random RAND = new Random();
    private static final String RANDOM_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Returns the true cause of an exception that has occurred.
    public static Throwable getUnderlyingCause(Throwable t) {
        while (t != null && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    // Returns true if the given class represents a Wolf type of animal.
    public static boolean isWolfClass(Class<? extends Critter> clazz) {
        return clazz != null && clazz.getName().startsWith(WOLF_CLASS_NAME);
    }
    
	public static String toString(Critter critter) {
	    return "{" + critter.getClass().getName() + ", \"" + critter + "\", id " + critter.hashCode() + "}";
	}
	
    // Returns a random color.
    private static Color randomColor() {
        int r = RAND.nextInt(256);
        int g = RAND.nextInt(256);
        int b = RAND.nextInt(256);
        return new Color(r, g, b);
    }

    // fields
    private final int height;
    private final int width;
    private final Critter[][] grid;
    private final String[][] display;
    private final Color[][] colorDisplay;
    private final boolean[][] food;
    private final Random rand;
    private final List<Critter> critterList;
    private final Map<Critter, Point> locationMap;
    private final SortedMap<String, CritterState> classStateMap;
    private final Map<Critter, CritterState> critterStateMap;
    private int moveCount;
    private Point infoPoint = new Point();
    private boolean debug = false;
    // private boolean partialMode = false;
    private int partialIndex = 0;
    private SecurityManager security = null;   // if set, model is "locked"

    public static CritterModel MODEL = null;
    
    // Constructs a new model of the given size.
    public CritterModel(int width, int height) {
        this(width, height, false);
    }
    
    // Constructs a new model of the given size.
    public CritterModel(int width, int height, boolean debug) {
        // check for invalid model size
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        
        MODEL = this;

        this.width = width;
        this.height = height;
        this.debug = debug;
        
        rand = new Random();
        moveCount = 0;
        grid = new Critter[width][height];
        food = new boolean[width][height];
        display = new String[width][height];
        colorDisplay = new Color[width][height];

        // initialize various data structures
        critterList = new ArrayList<Critter>();
        locationMap = new HashMap<Critter, Point>();
        classStateMap = new TreeMap<String, CritterState>();

        // important to use IdentityHashMap so they can't trick me by overriding hashCode
        critterStateMap = new IdentityHashMap<Critter, CritterState>();

        createRandomFood();
        updateDisplay(Event.NEW);
    }

    // Adds the given number of critters of the given type to the simulation.
    public synchronized void add(int number, Class<? extends Critter> critterClass) {
        add(number, critterClass, null);
    }
    
    public synchronized void add(int number, Class<? extends Critter> critterClass, SecurityManager mgr) {
        mutateCheck(mgr);
        
        // count # of critters of each class
        String className = critterClass.getName();
        if (!classStateMap.containsKey(className)) {
            classStateMap.put(className, new CritterState(critterClass));
        }

        try {
            // call private helper add method many times
            for (int i = 0; i < number; i++) {
                add(critterClass);
            }
        } catch (IllegalAccessException e) {
            throw new InvalidCritterClassException(e);
        } catch (InvocationTargetException e) {
            throw new InvalidCritterClassException(e);
        } catch (InstantiationException e) {
            throw new InvalidCritterClassException(e);
        }

        updateDisplay(Event.ADD);
    }

    // Returns the color that should be displayed on the given (x, y) location,
    // or black if nothing is there.
    public Color getColor(int x, int y) {
        if (!isOnBoard(x, y)) {
            return null;
        }
        
        return colorDisplay[x][y];
    }

    // Returns a set of all names of Critter classes that exist in this model.
    public Set<String> getClassNames() {
        return Collections.unmodifiableSet(classStateMap.keySet());
    }

    // Returns a set of [class name, count] entry pairs in this model.
    public Set<Map.Entry<String, CritterState>> getClassStates() {
        return Collections.unmodifiableSet(classStateMap.entrySet());
    }

    // Returns how many critters of the given type exist in the world.
    public int getCount(String className) {
        if (classStateMap.containsKey(className)) {
            return classStateMap.get(className).count;
        } else {
            return 0;
        }
    }

    // Returns the class of animal that is sitting at the given (x, y) location (null if empty).
    public Class<? extends Critter> getCritterClass(int x, int y) {
        if (!isOnBoard(x, y)) {
            return null;
        }
        if (grid[x][y] == null) {
            return null;
        } else {
            return grid[x][y].getClass();
        }
    }

    // Returns how many critters of the given type have died.
    public int getDeaths(String className) {
        if (classStateMap.containsKey(className)) {
            return classStateMap.get(className).deaths;
        } else {
            return 0;
        }
    }

    // Returns how many critters of the given type exist in the world.
    public int getFoodEaten(String className) {
        if (classStateMap.containsKey(className)) {
            return classStateMap.get(className).foodEaten;
        } else {
            return 0;
        }
    }

    // Returns how many critters of the given type exist in the world.
    public int getFoodPenalty(String className) {
        if (classStateMap.containsKey(className)) {
            return classStateMap.get(className).foodPenalty;
        } else {
            return 0;
        }
    }

    // Returns the height of this model.
    public int getHeight() {
        return height;
    }

    // Returns how many critters of the given type exist in the world.
    public int getKills(String className) {
        if (classStateMap.containsKey(className)) {
            return classStateMap.get(className).kills;
        } else {
            return 0;
        }
    }

    // Returns number of updates made to this model.
    public int getMoveCount() {
        return moveCount;
    }
    
    public int getPartialIndex() {
        return partialIndex;
    }

    // Returns the String of text to display at the given (x, y) location.
    public String getString(int x, int y) {
        if (!isOnBoard(x, y)) {
            return null;
        }
        return display[x][y];
    }

    // Returns the total number of critters in this model.
    public int getTotalCritterCount() {
        return locationMap.keySet().size();
    }

    // Returns the width of this model.
    public int getWidth() {
        return width;
    }

    // Returns the name of the critter class with the highest score.
    public String getWinningClassName() {
        int max = 0;
        String maxClassName = "";
        for (Map.Entry<String, CritterState> entry : classStateMap.entrySet()) {
            CritterState state = entry.getValue();
            int total = state.count + state.kills + state.foodEaten;
            if (total > max) {
                max = total;
                maxClassName = entry.getKey();
            } else if (total == max) {
                // combine the names just to make it not match
                maxClassName += " " + entry.getKey();
            }
        }
        return maxClassName;
    }

    // Returns whether a critter at the given square is asleep.
    public boolean isAsleep(int x, int y) {
        if (!isOnBoard(x, y)) {
            return false;
        }
        if (grid[x][y] == null) {
            return false;
        }
        CritterState state = critterStateMap.get(grid[x][y]);
        return state != null && state.isAsleep();
    }

    // Returns whether a critter at the given square is asleep.
    public boolean isBaby(int x, int y) {
        if (!isOnBoard(x, y)) {
            return false;
        }
        if (!DISPLAY_BABIES || grid[x][y] == null) {
            return false;
        }
        CritterState state = critterStateMap.get(grid[x][y]);
        return state != null && state.isBaby();
    }
    
    // Returns whether a critter at the given square is asleep.
    public boolean isCurrentCritter(int x, int y) {
        if (!isOnBoard(x, y)) {
            return false;
        }
        if (!debug || grid[x][y] == null) {
            return false;
        }
        
        Critter currentCritter = critterList.get(partialIndex);
        return currentCritter == grid[x][y];
    }

    // Returns whether the model is printing debug messages
    public boolean isDebug() {
        return debug;
    }

    // Returns whether a critter at the given square is asleep.
    public boolean isMating(int x, int y) {
        if (!isOnBoard(x, y)) {
            return false;
        }
        if (grid[x][y] == null) {
            return false;
        }
        CritterState state = critterStateMap.get(grid[x][y]);
        return state != null && state.isMating();
    }
    
    public boolean isOnBoard(int x, int y) {
        return 0 <= x && x < getWidth()
                && 0 <= y && y < getHeight();
    }
    
    public boolean isLocked() {
        return security != null;
    }

    // Returns an iterator of the critters in this model.
    // public Iterator<Critter> iterator() {
    //     return Collections.unmodifiableList(critterList).iterator();
    // }
    
    public void lock(SecurityManager mgr) {
        if (isLocked()) {
            throw new CritterSecurityException("Cannot re-lock an already locked model");
        }
        security = mgr;
    }
    
    // helper so that the panel can move critters around from x1,y1 to x2,y2
    public synchronized boolean move(int x1, int y1, int x2, int y2) {
        return move(x1, y1, x2, y2, null);
    }
    
    public synchronized boolean move(int x1, int y1, int x2, int y2, SecurityManager mgr) {
        mutateCheck(mgr);
        
        if (!isDebug()) {
            return false;
        }
        
        if (!isOnBoard(x1, y1) || !isOnBoard(x2, y2)) {
            return false;
        }
        
        // there must be a critter in the start square
        if (grid[x1][y1] == null) {
            return false;
        }
        
        // there must not be a critter in the end square
        if (grid[x2][y2] != null) {
            return false;
        }
        
        // there must not be food in the end square
        if (food[x2][y2]) {
            return false;
        }
        
        grid[x2][y2] = grid[x1][y1];
        grid[x1][y1] = null;
        
        colorDisplay[x2][y2] = colorDisplay[x1][y1];
        colorDisplay[x1][y1] = null;
        
        display[x2][y2] = display[x1][y1];
        display[x1][y1] = null;
        
        Point location = locationMap.get(grid[x2][y2]);
        location.x = x2;
        location.y = y2;
        
        setChanged();
        notifyObservers(Event.MOVE);
        return true;
    }
    
    public void printDebugInfo(int x, int y) {
        if (!isDebug()) {
            return;
        }
        if (!isOnBoard(x, y)) {
            return ;
        }
        if (grid[x][y] != null) {
            System.out.println("x=" + x + ", y=" + y + ": " + toString(grid[x][y]));
        }
    }

    // Restarts the model and reloads the critters.
    public synchronized void reset() {
        reset(null);
    }
    
    public synchronized void reset(SecurityManager mgr) {
        mutateCheck(mgr);
        
        createRandomFood();
        
        // remove/reset all existing animals from the game
        if (debug) System.out.println("Calling reset() on each critter:");
        for (Critter critter : critterList) {
            critterStateMap.remove(critter);
            Point location = locationMap.remove(critter);
            if (location != null && grid[location.x][location.y] == critter) {
                grid[location.x][location.y] = null;
            }
            
            try {
                critter.reset();
            } catch (Throwable t) {
                // student messed up their code
                throw new BuggyCritterException("error in reset method of class " + critter.getClass().getName(), t, critter.getClass().getName());
            }
        }
        critterList.clear();
        
        // reset state for all classes
        if (debug) System.out.println("Calling static method resetAll() on each critter class, and re-adding to board:");
        for (Map.Entry<String, CritterState> entry : classStateMap.entrySet()) {
            String className = entry.getKey();

            // wipe the class entry for this animal type
            CritterState classState = entry.getValue();

            // remove all animals of this type
            int count = entry.getValue().initialCount;
            removeAll(className, false);
            classState.reset();

            // notify the class that it was reset, if method exists
            Class<? extends Critter> critterClass = entry.getValue().critterClass;
            try {
                Method resetAllMethod = critterClass.getDeclaredMethod("resetAll");
                if (resetAllMethod != null 
                        && (Modifier.isPublic(resetAllMethod.getModifiers()))
                        && (Modifier.isStatic(resetAllMethod.getModifiers()))) {
                    if (debug) System.out.println("    calling resetAll() on class " + critterClass.getName());
                    resetAllMethod.invoke(null);
                }
            } catch (NoSuchMethodException e) {
                // no resetAll method; this is okay and expected for non-Huskies
            } catch (InvocationTargetException e) {
                // student messed up their code
                throw new BuggyCritterException("error in resetAll method of class " + critterClass.getName(), getUnderlyingCause(e), critterClass.getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Your resetAll method must be public.", e);
            }

            // add them back
            add(count, critterClass, mgr);
        }
        
        // reset class-based state (hmm, is this redundant with the above?)
        for (CritterState state : critterStateMap.values()) {
            state.reset();
        }
        moveCount = 0;

        setChanged();
        notifyObservers(Event.RESET);
    }

    // Removes all critters of the given type from the simulation.
    public synchronized void removeAll(String className) {
        removeAll(className, null);
    }
    
    public synchronized void removeAll(String className, SecurityManager mgr) {
        mutateCheck(mgr);
        removeAll(className, true);
    }

    // Removes all critters of the given type from the simulation;
    // if permanent is true, they won't revive even after a reset.
    private synchronized void removeAll(String className, boolean permanent) {
        for (Iterator<Critter> i = critterList.iterator(); i.hasNext();) {
            Critter critter = i.next();
            if (critter.getClass().getName().equals(className)) {
                // delete this critter
                i.remove();
                Point location = locationMap.remove(critter);
                if (grid[location.x][location.y] == critter) {
                    grid[location.x][location.y] = null;
                    // display[location.x][location.y] = EMPTY;
                }
            }
        }
        if (permanent) {
            // TODO: this might cause a ConcurrentModificationException
            // if done while game is running...
            try {
                classStateMap.remove(className);
            } catch (Exception e) {
            	// empty
            }
        }

        updateDisplay(Event.REMOVE_ALL);
    }
    
    public void setDebug(boolean debug) {
        setDebug(debug, null);
    }
    
    public void setDebug(boolean debug, SecurityManager mgr) {
        mutateCheck(mgr);
        this.debug = debug;
    }
    
    // ends this game; used by tournament main program
    public synchronized void shutdown() {
        shutdown(null);
    }
    
    public synchronized void shutdown(SecurityManager mgr) {
        mutateCheck(mgr);
        
        // remove/reset all existing animals from the game
        for (Critter critter : critterList) {
            try {
                critter.reset();
            } catch (Throwable t) {
                // student messed up their code
                // throw new BuggyCritterException("error in reset method of class " + critter.getClass().getName(), t);
            }
        }
        
        // reset each critter class, if method exists
        for (Map.Entry<String, CritterState> entry : classStateMap.entrySet()) {
            Class<? extends Critter> critterClass = entry.getValue().critterClass;
            try {
                Method resetAllMethod = critterClass.getDeclaredMethod("resetAll");
                if (resetAllMethod != null 
                        && (Modifier.isPublic(resetAllMethod.getModifiers()))
                        && (Modifier.isStatic(resetAllMethod.getModifiers()))) {
                    resetAllMethod.invoke(null);
                }
            } catch (NoSuchMethodException e) {
                // no resetAll method; this is okay and expected for non-Huskies
            } catch (InvocationTargetException e) {
                // student messed up their code
                // throw new BuggyCritterException("error in resetAll method of class " + critterClass.getName(), getUnderlyingCause(e));
            } catch (IllegalAccessException e) {
                // throw new RuntimeException("Your resetAll method must be public.", e);
            }
        }
        setChanged();
        notifyObservers(Event.RESET);
    }
    
    public void unlock(SecurityManager mgr) {
        if (!isLocked()) {
            throw new CritterSecurityException("model is not locked");
        }
        
        if (mgr != security) {
            throw new CritterSecurityException("cannot unlock this model using the given key");
        }
        security = null;
    }
    
    // Moves position of all critters; does collisions, fights, eating, mating, etc.
    public synchronized void update() {
        update(null);
    }
    
    public synchronized void update(SecurityManager mgr) {
        mutateCheck(mgr);
        
        if (partialIndex == 0) {
            moveCount++;
            if (debug) System.out.println("\nBegin overall move #" + moveCount);

            // reorder the list to be fair about move/collision order
            Collections.shuffle(critterList);
        }

        if (debug) {
            if (debug) System.out.println("Begin move #" + 
                    moveCount + " for critter #" + (partialIndex + 1) + " of " + critterList.size());

            updateCritter(partialIndex);
            partialIndex = (partialIndex + 1) % critterList.size();
        } else {
            // move each critter to its new position
            for (int i = partialIndex; i < critterList.size(); i++) {
                i = updateCritter(i);
            }
            partialIndex = 0;
        }
        
        // update each animal class's state (not used much anymore) 
        updateCritterClassStates();
        
        // now process all individual critter animals' state
        updateCritterIndividualStates();

        // respawn new food periodically
        if (moveCount % FOOD_RESPAWN_INTERVAL == 0 && (!debug || partialIndex == 0)) {
            Point open = randomOpenLocation();
            if (debug) System.out.println("  creating new food at " + StringUtils.toString(open));
            food[open.x][open.y] = true;
        }

        updateDisplay(Event.UPDATE, true);
    }
    
    private int updateCritter(int i) {
        Critter critter1 = critterList.get(i);
        CritterState classState1 = classStateMap.get(critter1.getClass().getName());
        CritterState critterState1 = critterStateMap.get(critter1);
        Point location = locationMap.get(critter1);

        if (debug) System.out.println("  " + toString(critterList.get(partialIndex)) + " at " + StringUtils.toString(location));

        // fill the Critter's inherited fields with info about the game state
        // (I don't have to try/catch when calling set___() because they're final)
        critter1.setX(location.x);
        critter1.setY(location.y);
        critter1.setNeighbor(Critter.Direction.CENTER, display[location.x][location.y]);
        for (Critter.Direction dir : Critter.Direction.values()) {
            infoPoint.x = location.x;
            infoPoint.y = location.y;
            movePoint(infoPoint, dir);
            critter1.setNeighbor(dir, display[infoPoint.x][infoPoint.y]);
        }

        if (classState1.isAsleep() || critterState1.isAsleep() ||
                classState1.isMating() || critterState1.isMating()) {
            // this critter doesn't get to move; he is sleeping
            // from eating too much food or something
            if (debug) System.out.println("    asleep or mating; skipping");
            return i;
        }

        // move the critter
        grid[location.x][location.y] = null;
        String critter1ToString = display[location.x][location.y];
        display[location.x][location.y] = EMPTY;

        if (debug) System.out.print("    calling getMove ... ");

        Critter.Direction move = Critter.Direction.CENTER;
        try {
            move = critter1.getMove();   // get actual move
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in getMove method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
        }
        if (debug) System.out.println("returned " + move);
        Point locationCopy = movePoint(new Point(location.x, location.y), move);
        
        // see if anybody else is in the square critter1 moved onto
        Critter critter2 = grid[locationCopy.x][locationCopy.y];
        Critter winner = critter1;
        String winnerToString = critter1ToString;
        
        if (critter2 == null) {
            movePoint(location, move);
            if (debug) System.out.println("    moving critter to " + StringUtils.toString(location));
            critter1.setX(location.x);
            critter1.setY(location.y);
        } else {
            // if two critters from same species want to move together, mate!
            // (also don't actually move the moving animal; leave them still)
            if (critter1.getClass() == critter2.getClass()) {
                CritterState critterState2 = critterStateMap.get(critter2);
                if (!critterState1.hasMate() && !critterState2.hasMate()) {
                    // they fall in love!
                    if (debug) System.out.println("    mating begins at " + StringUtils.toString(location) + " between " + toString(critter1) + " and " + toString(critter2));
                    critterState1.mate = critter2;
                    critterState2.mate = critter1;
                    critterState1.matePenalty = MATING_PENALTY;
                    critterState2.matePenalty = MATING_PENALTY;
                    
                    // notify the critters that they be gettin' it on
                    try {
                        critter1.mate();
                        critter2.mate();
                    } catch (Throwable t) {
                        // student messed up their code
                        throw new BuggyCritterException("error in mate method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
                    }
                }
            } else {
                // square is occupied by an enemy animal; fight!
                movePoint(location, move);
                if (debug) System.out.println("    moving critter to " + StringUtils.toString(location));
                critter1.setX(location.x);
                critter1.setY(location.y);
                String critter2ToString = display[location.x][location.y];
                
                if (debug) System.out.println("    fight with " + toString(critter2));
                winner = fight(critter1, critter2, critter1ToString, critter2ToString);

                Critter loser = (winner == critter1) ? critter2 : critter1;
                if (debug) System.out.println("      winner=" + toString(winner));

                locationMap.remove(loser);
                int indexToRemove;
                if (winner == critter1) {
                    indexToRemove = CollectionUtils.indexOfSafe(critterList, critter2);
                    winnerToString = critter1ToString;
                } else {
                    indexToRemove = i;
                    winnerToString = critter2ToString;
                }
                critterList.remove(indexToRemove);
                if (indexToRemove <= i) {
                    i--;  // so we won't skip a critter by mistake
                }

                // TODO: update the grid and display fields if necessary
                // put null color, "." on location of loser
                // problem: if winner is still there, should put his color/toString,
                // but then we'll get them again when we call updateDisplay...
                // should only call them once per update

                // decrement various counters for each critter type
                String winnerClassName = winner.getClass().getName();
                String loserClassName = loser.getClass().getName();
                classStateMap.get(loserClassName).deaths++;
                classStateMap.get(loserClassName).count--;
                if (!winnerClassName.equals(loserClassName)) {
                    classStateMap.get(winnerClassName).kills++;
                }
                
                loser.setAlive(false);
            }
        }
        grid[location.x][location.y] = winner;
        display[location.x][location.y] = winnerToString;

        if (winner == critter1) {
            // critter is still alive
            critterState1.moves++;
            if (CRITTER_MOVE_FATIGUE_COUNT > 0 && critterState1.moves % CRITTER_MOVE_FATIGUE_COUNT == 0) {
                critterState1.foodPenalty = GLUTTON_PENALTY;
                if (debug) System.out.println("    moved too much; falling asleep for " + GLUTTON_PENALTY + " moves");
                critter1.setAwake(false);
                try {
                    critter1.sleep();
                } catch (Throwable t) {
                    // student messed up their code
                    throw new BuggyCritterException("error in sleep method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
                }
            } else if (food[location.x][location.y]) {
                // check whether this critter should eat food
                if (debug) System.out.print("    food found; calling eat ... ");
                boolean critterEat = false;
                try {
                    critterEat = critter1.eat();
                } catch (Throwable t) {
                    // student messed up their code
                    throw new BuggyCritterException("error in eat method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
                }
                if (debug) System.out.println("returned " + critterEat);
                if (critterEat) {
                    food[location.x][location.y] = false;
                    classState1.foodEaten++;
                    if (!critterStateMap.containsKey(critter1)) {
                        throw new IllegalStateException("Unknown critter.  This should not happen: " + critter1);
                    }
                    critterState1.foodEaten++;

                    // possibly penalize the critter for eating too much
                    if (CRITTER_GLUTTON_COUNT > 0 && critterState1.foodEaten % CRITTER_GLUTTON_COUNT == 0) {
                        int penalty = GLUTTON_PENALTY;
                        if (RANDOMIZE_GLUTTON_PENALTY) {
                            // somewhere between 1 and 2*GLUTTON_PENALTY
                            penalty = rand.nextInt(2 * GLUTTON_PENALTY) + 1;
                        }
                        critterState1.foodPenalty = penalty;
                        
                        if (debug) System.out.println("    ate too much; falling asleep for " + penalty + " moves");
                        critter1.setAwake(false);
                        try {
                            critter1.sleep();
                        } catch (Throwable t) {
                            // student messed up their code
                            throw new BuggyCritterException("error in sleep method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
                        }
                    }

                    // possibly penalize the species as a whole for eating too much
                    if (CRITTER_CLASS_GLUTTON_COUNT > 0 && classState1.foodEaten % CRITTER_CLASS_GLUTTON_COUNT == 0) {
                        classState1.foodPenalty = GLUTTON_PENALTY;
                        if (debug) System.out.println("    class ate too much; falling asleep for " + GLUTTON_PENALTY + " moves");
                        critter1.setAwake(false);
                        try {
                            critter1.sleep();
                        } catch (Throwable t) {
                            // student messed up their code
                            throw new BuggyCritterException("error in sleep method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
                        }
                    }
                }
            }
        }
        
        return i;
    }
    
    private void updateCritterClassStates() {
        // any sleeping classes come one step closer to waking up
        for (CritterState state : classStateMap.values()) {
            if (state.isAsleep()) {
                state.foodPenalty--;
                if (!state.isAsleep()) {
                    // notify all the critters that they've woken up
                    if (debug) System.out.println("  waking up all critters of type " + state.critterClass.getName());
                    for (Critter critter : critterList) {
                        if (critter.getClass() == state.critterClass) {
                            if (debug) System.out.println("    waking up " + toString(critter));
                            critter.setAwake(true);
                            try {
                                critter.wakeup();
                            } catch (Throwable t) {
                                // student messed up their code
                                throw new BuggyCritterException("error in wakeup method of class " + critter.getClass().getName(), t, critter.getClass().getName());
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void updateCritterIndividualStates() {
        // (I make a copy of the map values so I won't get a ConcurrentModificationException
        // if I add a baby to the game board in the middle of the foreach loop)
        List<CritterState> critterStates = new ArrayList<CritterState>(critterStateMap.values());
        for (CritterState state : critterStates) {
            if (state.isAsleep()) {
                // any sleeping animals come one step closer to waking up
                state.foodPenalty--;

                // wake him up, if neither he nor his species is asleep
                if (state.isAsleep()) {
                    if (debug) System.out.println("  " + state.foodPenalty + " moves until wakeup for " + toString(state.critter));
                } else if (classStateMap.containsKey(state.critterClass.getName())
                        && !classStateMap.get(state.critterClass.getName()).isAsleep()) {
                    if (debug) System.out.println("  waking up " + toString(state.critter));
                    state.critter.setAwake(true);
                    try {
                        state.critter.wakeup();
                    } catch (Throwable t) {
                        // student messed up their code
                        throw new BuggyCritterException("error in wakeup method of class " + state.critter.getClass().getName(), t, state.critter.getClass().getName());
                    }
                }
            }
            if (state.isMating()) {
                state.matePenalty--;
                if (state.isMating()) {
                    if (debug) System.out.println("  " + state.matePenalty + " moves until done mating for " + toString(state.critter) + " and " + toString(state.mate));
                } else {
                    // new baby born!
                    CritterState state2 = critterStateMap.get(state.mate);
                    
                    // critter 1 and 2 should be next to each other
                    Point location1 = locationMap.get(state.critter);
                    Point location2 = locationMap.get(state2.critter);
                    
                    if (location1 == null) {
                        throw new RuntimeException(location2 + ": null location 1 for " + state.critterClass.getName() + " " + state.critter + " " + state.critter.hashCode() + ": " + locationMap);
                    } else if (location2 == null) {
                        throw new RuntimeException(location1 + ": null location 2 for " + state2.critterClass.getName() + " " + state2.critter + " " + state2.critter.hashCode() + ": " + locationMap);
                    }
                    
                    // pick a random location for the baby to be born
                    // (prefer a random spot that borders the parents)
                    Set<Point> neighbors = getOpenNeighbors(location1);
                    neighbors.addAll(getOpenNeighbors(location2));
                    List<Point> neighborsList = new ArrayList<Point>(neighbors);
                    Collections.shuffle(neighborsList);
                    Point babyLocation = neighborsList.isEmpty() ? randomOpenLocation() : neighborsList.get(0);
                    
                    if (debug) System.out.println("  done mating for " + toString(state.critter) + " and " + toString(state.mate));
                    if (debug) System.out.println("    baby born at " + StringUtils.toString(babyLocation));
                    try {
                        Critter baby = add(state.critterClass, babyLocation);   // add the baby!
                        CritterState babyState = critterStateMap.get(baby);
                        babyState.daddy = state.critter;
                        
                        // adjust the class's state not to count babies in the initial count
                        CritterState speciesState = classStateMap.get(baby.getClass().getName());
                        speciesState.initialCount--;
                    } catch (IllegalAccessException e) {
                        System.out.println(e);
                    } catch (InvocationTargetException e) {
                        System.out.println(e);
                    } catch (InstantiationException e) {
                        System.out.println(e);
                    }

                    // notify the critters that the boom shaka laka is over
                    state.matePenalty = 0;
                    state2.matePenalty = 0;
                    try {
                        state.critter.mateEnd();
                        state2.critter.mateEnd();
                    } catch (Throwable t) {
                        // student messed up their code
                        throw new BuggyCritterException("error in mateEnd method of class " + state.critter.getClass().getName(), t, state.critter.getClass().getName());
                    }
                }
            }
        }
    }
    
    // Adds a single instance of the given type to this model.
    // If the critter's constructor needs any parameters, gives random values.
    private Critter add(Class<? extends Critter> critterClass)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Point location = randomOpenLocation();
        return add(critterClass, location);
    }

    // Adds a single instance of the given type to this model.
    // If the critter's constructor needs any parameters, gives random values.
    private Critter add(Class<? extends Critter> critterClass, Point location)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (getTotalCritterCount() >= width * height) {
            throw new TooManyCrittersException();
        }

        // create critter
        Constructor<? extends Critter> ctor = getConstructor(critterClass);
        Object[] params = createRandomParameters(critterClass, ctor);
        // Critter critter = ctor.newInstance(params);
        Object obj = null;
        try {
            obj = ctor.newInstance(params);
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in constructor of class " + critterClass.getName(), getUnderlyingCause(t), critterClass.getName());
        }
        
        if (debug) System.out.println("Constructed new " + critterClass.getName() + " (id " + obj.hashCode() + ") at " + StringUtils.toString(location) + " with parameter(s): " + Arrays.toString(params));
        
        Critter critter;
        if (obj instanceof Critter) {
            critter = (Critter) obj;
        } else {
            throw new InvalidCritterClassException(critterClass.getName()
                    + " is not a valid Critter and cannot be loaded.");
        }

        critter.setWidth(width);
        critter.setHeight(height);
        critter.setX(location.x);
        critter.setY(location.y);
        
        critterList.add(critter);

        // place critter on board
        locationMap.put(critter, location);
        grid[location.x][location.y] = critter;

        // count # of critters of each class
        String className = critter.getClass().getName();
        CritterState state = classStateMap.get(className);
        state.count++;
        state.initialCount++;

        // count various things about each critter object
        CritterState objectState = new CritterState(critterClass, critter);
        critterStateMap.put(critter, objectState);
        
        return critter;
    }

    // Fills the board with food in randomly chosen locations.
    private void createRandomFood() {
        // clear out any previous food
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                food[x][y] = false;
            }
        }

        // randomly fill some fraction of all squares
        int squaresToFill = FOOD_PERCENTAGE * width * height / 100;
        for (int i = 0; i < squaresToFill; i++) {
            int randomX = rand.nextInt(width);
            int randomY = rand.nextInt(height);
            food[randomX][randomY] = true;
        }
    }

    // Fills and returns an array of random values of the proper types
    // for the given constructor.
    private Object[] createRandomParameters(
            Class<? extends Critter> critterClass,
            Constructor<? extends Critter> ctor) {
        Class<?>[] paramTypes = ctor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        // build random parameters
        for (int j = 0; j < params.length; j++) {
            if (paramTypes[j] == Integer.TYPE) {
                // int parameters are things like hunger;
                // randomly choose a value from 0 through MAX
                params[j] = new Integer(rand.nextInt(INT_PARAM_MAX) + 1);
            } else if (paramTypes[j] == Boolean.TYPE) {
                params[j] = rand.nextBoolean();
            } else if (paramTypes[j] == String.class) {
                int r = rand.nextInt(RANDOM_LETTERS.length());
                params[j] = RANDOM_LETTERS.substring(r, r + 1);
            } else if (paramTypes[j] == Color.class) {
                params[j] = randomColor();
            } else if (paramTypes[j] == Critter.Attack.class) {
                // special case: Elephant's int parameter is an attack
                int rIndex = rand.nextInt(Critter.Attack.values().length);
                params[j] = Critter.Attack.values()[rIndex];
            } else if (paramTypes[j] == Critter.Direction.class) {
                // special case: Elephant's int parameter is an attack
                List<Critter.Direction> dirs = new ArrayList<Critter.Direction>(Arrays.asList(Critter.Direction.values()));
                dirs.remove(Critter.Direction.CENTER);
                int rIndex = rand.nextInt(dirs.size());
                params[j] = dirs.get(rIndex);
            } else {
                throw new InvalidCritterClassException("when constructing "
                        + critterClass + ":\nbad constructor parameter type: "
                        + paramTypes[j]);
            }
        }
        return params;
    }

    // Conducts a fight between the given two critters.
    // Returns which critter won the game.  The other must die!
    private Critter fight(Critter critter1, Critter critter2, String critter1toString, String critter2toString) {
        Critter.Attack weapon1 = Critter.Attack.FORFEIT;
        Critter.Attack weapon2 = Critter.Attack.FORFEIT;
        
        try {
            // * I have to call .toString() again on the critters because it might
            // have changed since I last stored it (such as if they fight, eat, etc.)
            critter2toString = critter2.toString();
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in toString method of class " + critter2.getClass().getName(), t, critter2.getClass().getName());
        }
        
        try {
            weapon1 = critter1.fight(critter2toString);
            if (weapon1 == null) {
                weapon1 = Critter.Attack.FORFEIT;
            }
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in fight method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
        }

        try {
            // * I have to call .toString() again on the critters because it might
            // have changed since I last stored it (such as if they fight, eat, etc.)
            critter1toString = critter1.toString();
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in toString method of class " + critter1.getClass().getName(), t, critter1.getClass().getName());
        }
        
        try {
            weapon2 = critter2.fight(critter1toString);
            if (weapon2 == null) {
                weapon2 = Critter.Attack.FORFEIT;
            }
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in fight method of class " + critter2.getClass().getName(), t, critter2.getClass().getName());
        }

        if (debug) System.out.println("      " + toString(critter1) + " chooses " + weapon1);
        if (debug) System.out.println("      " + toString(critter2) + " chooses " + weapon2);
        
        Critter winner;

        // special case: if one of the animals is sleeping, it dies
        CritterState classState1 = classStateMap.get(critter1.getClass().getName());
        CritterState classState2 = classStateMap.get(critter2.getClass().getName());
        if (classState1 == classState2) {
            throw new IllegalStateException("BUG: Should not get here.  Two " + critter1.getClass().getName() + "s fighting.  This should not happen!");
        }
        
        CritterState state1 = critterStateMap.get(critter1);
        CritterState state2 = critterStateMap.get(critter2);
        if (((classState1.isAsleep() || state1.isAsleep()) && (classState2.isAsleep() || state2.isAsleep())) || 
            (state1.isMating() && state2.isMating())) {
            // shouldn't get here
            throw new IllegalStateException("BUG: Fight between two sleeping/mating critters.  This should not happen!");
        } else if (classState1.isAsleep() || state1.isAsleep() || state1.isMating()) {
            winner = critter2;
        } else if (classState2.isAsleep() || state2.isAsleep() || state2.isMating()) {
            winner = critter1;
        } else {
            // let's randomly decide that if both animals forfeit, player 1 wins
            if (weapon1 == weapon2) {
                // tie
                winner = RAND.nextBoolean() ? critter1 : critter2;
            } else if ((weapon2 == Critter.Attack.FORFEIT)
                    || (weapon1 == Critter.Attack.ROAR && weapon2 == Critter.Attack.SCRATCH)
                    || (weapon1 == Critter.Attack.SCRATCH && weapon2 == Critter.Attack.POUNCE)
                    || (weapon1 == Critter.Attack.POUNCE && weapon2 == Critter.Attack.ROAR)) {
                // player 1 wins
                winner = critter1;
            } else {
                // player 2 wins
                winner = critter2;
            }
        }

        // inform the critters that they have won/lost
        Critter loser = (winner == critter1) ? critter2 : critter1;
        try {
            winner.win();
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in win method of class " + winner.getClass().getName(), t, winner.getClass().getName());
        }

        try {
            loser.lose();
        } catch (Throwable t) {
            // student messed up their code
            throw new BuggyCritterException("error in lose method of class " + loser.getClass().getName(), t, loser.getClass().getName());
        }
        
        // if the loser was mating, inform the mate to go back to normal
        CritterState loserState = (winner == critter1) ? state2 : state1;
        if (loserState.isMating()) {
            loserState.matePenalty = 0;
            CritterState mateState = critterStateMap.get(loserState.mate);
            mateState.matePenalty = 0;
            try {
                loserState.mate.mateEnd();
            } catch (Throwable t) {
                // student messed up their code
                throw new BuggyCritterException("error in mateEnd method of class " + loserState.mate.getClass().getName(), t, loserState.mate.getClass().getName());
            }
        }
        
        return winner;
    }

    // Gets and returns the constructor for the given class by reflection.
    @SuppressWarnings("unchecked")
    private Constructor<? extends Critter> getConstructor(Class<? extends Critter> critterClass) {
        // TODO: change to getConstructor() (no warning)
        Constructor<? extends Critter>[] ctors = (Constructor<? extends Critter>[]) critterClass.getConstructors();
        if (ctors.length != 1) {
            throw new InvalidCritterClassException(
                    "wrong number of constructors (" + ctors.length + ") for "
                            + critterClass + "; must have only one constructor");
        }
        return ctors[0];
    }

    private Set<Point> getOpenNeighbors(Point location) {
        // pick random place for the baby to appear
        Set<Point> neighbors = new HashSet<Point>();
        for (int x = location.x - 1; x <= location.x + 1; x++) {
            for (int y = location.y - 1; y <= location.y + 1; y++) {
                int realX = (x + width) % width;
                int realY = (y + height) % height;
                if (grid[realX][realY] == null) {
                    neighbors.add(new Point(realX, realY));
                }
            }
        }
        return neighbors;
    }

    // Translates a point's coordinates 1 unit in a particular direction.
    private Point movePoint(Point p, Critter.Direction direction) {
        if (direction == Critter.Direction.NORTH) {
            p.y = (p.y - 1 + height) % height;
        } else if (direction == Critter.Direction.SOUTH) {
            p.y = (p.y + 1) % height;
        } else if (direction == Critter.Direction.EAST) {
            p.x = (p.x + 1) % width;
        } else if (direction == Critter.Direction.WEST) {
            p.x = (p.x - 1 + width) % width;
        } // else direction == Critter.CENTER
        return p;
    }
    
    private void mutateCheck(SecurityManager mgr) {
        if (isLocked() && mgr != security) {
            throw new CritterSecurityException("cannot mutate model without proper security key");
        }
    }

    // Returns a random point that is unoccupied by any critters. 
    private Point randomOpenLocation() {
        // TODO: If board is completely full of animals, throw exception
        if (critterList.size() >= width * height) {
            throw new TooManyCrittersException();
        }
        
        Point p = new Point();
        do {
            p.x = rand.nextInt(width);
            p.y = rand.nextInt(height);
        } while (grid[p.x][p.y] != null);
        return p;
    }

    // Updates the internal string array representing the text to display.
    // Also notifies observers of a new event.
    // Doesn't throw exceptions if colors or toStrings are null.
    private void updateDisplay(Event event) {
        updateDisplay(event, false);
    }

    // Updates the internal string array representing the text to display.
    // Also notifies observers of a new event.
    // Possibly throws exceptions if colors or toStrings are null.
    private void updateDisplay(Event event, boolean throwOnNull) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                updateDisplaySquare(x, y, throwOnNull);
            }
        }

        setChanged();
        notifyObservers(event);
    }
    
    // Updates a square of the internal string array representing the text to display.
    // Possibly throws exceptions if colors or toStrings are null.
    private void updateDisplaySquare(int x, int y, boolean throwOnNull) {
        if (grid[x][y] == null) {
            if (food[x][y]) {
                display[x][y] = FOOD;
                colorDisplay[x][y] = FOOD_COLOR;
            } else {
                display[x][y] = EMPTY;
                colorDisplay[x][y] = Color.BLACK;
            }
        } else {
            try {
                display[x][y] = grid[x][y].toString();
            } catch (Throwable t) {
                // student messed up their code
                throw new BuggyCritterException("error in toString method of class " + grid[x][y].getClass().getName(), t, grid[x][y].getClass().getName());
            }
            if (throwOnNull && display[x][y] == null) {
                throw new IllegalArgumentException(grid[x][y].getClass().getName() + 
                        " returned a null toString result.");
            }

            try {
                colorDisplay[x][y] = grid[x][y].getColor();
            } catch (Throwable t) {
                // student messed up their code
                throw new BuggyCritterException("error in getColor method of class " + grid[x][y].getClass().getName(), t, grid[x][y].getClass().getName());
            }
            if (throwOnNull && colorDisplay[x][y] == null) {
                throw new IllegalArgumentException(grid[x][y].getClass().getName() + 
                        " returned a null getColor result.");
            }
        }
    }

    // Inner class to represent the state of a particular critter class or object.
    public static class CritterState {
        private Class<? extends Critter> critterClass;
        private Critter critter;
        private Critter daddy;
        private int count;
        private int initialCount;
        private int kills;
        private int deaths;
        private int moves;
        private int foodEaten;
        private int foodPenalty;
        private int matePenalty;
        private Critter mate;

        // Constructs object to represent state of the given class.
        public CritterState(Class<? extends Critter> critterClass) {
            this(critterClass, null);
        }

        // Constructs object to represent state of the given class.
        public CritterState(Class<? extends Critter> critterClass, Critter critter) {
            this.critterClass = critterClass;
            this.critter = critter;
        }

        // Returns how many animals are alive.
        public int getCount() {
            return count;
        }

        // Returns how many times an animal of this type has died.
        public int getDeaths() {
            return deaths;
        }

        // Returns how many times an animal of this type has eaten food.
        public int getFoodEaten() {
            return foodEaten;
        }

        // Returns how many moves this type is currently being penalized
        // for eating too much food.
        public int getFoodPenalty() {
            return foodPenalty;
        }

        // Returns how many animals of this type have ever been created.
        public int getInitialCount() {
            return initialCount;
        }

        // Returns how many animals this type has killed.
        public int getKills() {
            return kills;
        }

        // Returns how many moves this type is currently frozen during mating.
        public int getMatePenalty() {
            return matePenalty;
        }

        // Returns how many times this critter has moved.
        public int getMoves() {
            return moves;
        }
        
        // Returns true if this animal has a love partner and has mated.
        public boolean hasMate() {
            return mate != null;
        }

        // Returns true if this class is asleep.
        public boolean isAsleep() {
            return foodPenalty > 0;
        }
        
        public boolean isBaby() {
            return isBaby(true);
        }

        public boolean isBaby(boolean considerMoves) {
            if (considerMoves) {
                return moves < MATING_PENALTY && daddy != null;
            } else {
                return daddy != null;
            }
        }

        // Returns true if this class is currently mating.
        public boolean isMating() {
            return mate != null && matePenalty > 0;
        }

        public String toString() {
            return ReflectionUtils.toStringViaReflection(this);
        }
        
        // Resets the state of this type.
        private void reset() {
            count = 0;
            deaths = 0;
            foodEaten = 0;
            foodPenalty = 0;
            initialCount = 0;
            kills = 0;
            moves = 0;
            mate = null;
            matePenalty = 0;
            daddy = null;
        }

        // TODO: Comment out
//      public Class<? extends Critter> getCritterClass() {
//          return critterClass;
//      }
//      
//      public Critter getCritter() {
//          return critter;
//      }
//      
//      public Critter getMate() {
//          return mate;
//      }
    }

    // Used to signal various types to observers
    public enum Event {
        ADD, MOVE, NEW, REMOVE_ALL, RESET, UPDATE
    }

    // An exception thrown when the model is unable to instantiate a critter
    // class because of DrJava being crappy.
    public static class DrJavaSucksException extends RuntimeException {
        private static final long serialVersionUID = 0;

        public DrJavaSucksException(Exception e) {
            super(e);
        }

        public DrJavaSucksException(String message) {
            super(message);
        }
    }
}
