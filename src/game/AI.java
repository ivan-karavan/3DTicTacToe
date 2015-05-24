package game;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import game.model.Coordinate;
import game.model.Cross;
import game.model.Game;
import game.model.Nought;
import game.state.PCvsHumanAppState;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  Computer opponent of the real player
 */
public class AI extends Player {
    /**
     * All possible lines by four coordinates, which player can fill and then win
     */
    private static ArrayList<Line> combinations;

    private Coordinate[] corners = {
            new Coordinate(0, 0, 0),
            new Coordinate(0, 0, 3),
            new Coordinate(0, 3, 3),
            new Coordinate(3, 3, 3),
            new Coordinate(0, 3, 0),
            new Coordinate(3, 3, 0),
            new Coordinate(3, 0, 0),
            new Coordinate(3, 0, 3)
    };

    /**
     * Class incapsulates four coordinates on one line
     */
    class Line implements Iterable<Coordinate> {
        private ArrayList<Coordinate> points;

        Line(Coordinate startPoint, Coordinate dimension) {
            points = new ArrayList<>(4);
            points.add(startPoint);
            Coordinate next = startPoint;
            for (int i = 0; i < 3; i++) {
                next = new Coordinate(next, dimension);
                points.add(next);
            }
        }

        public boolean contains(Coordinate coordinate) {
            for (Coordinate c : points) {
                if (coordinate.equals(c)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Iterator<Coordinate> iterator() {
            return points.iterator();
        }
    }

    /**
     * Middle strenght bot
     */
    class MyPriorityDeque {
        private ArrayList<Coordinate> priority0;
        private ArrayList<Coordinate> priority1;
        private ArrayList<Coordinate> priority2;
        private ArrayList<Coordinate> priority3;
        private ArrayList<Coordinate> priority4;
        private ArrayList<Coordinate> priority5;
        private int size;

        public MyPriorityDeque() {
            priority0 = new ArrayList<>();
            priority1 = new ArrayList<>();
            priority2 = new ArrayList<>();
            priority3 = new ArrayList<>();
            priority4 = new ArrayList<>();
            priority5 = new ArrayList<>();
        }

        public void add(Coordinate coordinate, int priority) {
            switch (priority) {
                case 0:
                    if (priority0.contains(coordinate)) {
                        if (priority3.contains(coordinate)) {
                            priority3.remove(coordinate);
                            if (priority3.size() > 0) {
                                priority3.set(0, coordinate);
                            } else {
                                priority3.add(coordinate);
                            }
                        } else {
                            priority3.add(coordinate);
                            ++size;
                        }
                    } else {
                        priority0.add(coordinate);
                        ++size;
                    }
                    break;
                case 1:
                    priority1.add(coordinate);
                    break;
                case 2:
                    if (priority2.contains(coordinate)) {
                        if (priority4.contains(coordinate)) {
                            priority4.remove(coordinate);
                            if (priority4.size() > 0) {
                                priority4.set(0, coordinate);
                            } else {
                                priority4.add(coordinate);
                            }
                        } else {
                            priority4.add(coordinate);
                            ++size;
                        }
                    } else {
                        priority2.add(coordinate);
                        ++size;
                    }
                    break;
                case 3:
                    priority3.add(coordinate);
                    ++size;
                    break;
                case 4:
                    priority4.add(coordinate);
                    ++size;
                    break;
                case 5:
                    priority5.add(coordinate);
                    ++size;
                    break;
            }
        }

        public void add(ArrayList<Coordinate> coordinates, int priority) {
            for (Coordinate coordinate : coordinates) {
                add(coordinate, priority);
            }
        }

        public Coordinate getBest() {
            if (priority5.size() > 0) {
                return priority5.get(0);
            }
            if (priority4.size() > 0) {
                return priority4.get(0);
            }
            if (priority3.size() > 0) {
                return priority3.get(0);
            }
            if (priority2.size() > 0) {
                return priority2.get(0);
            }
            if (priority1.size() > 0) {
                return priority1.get(0);
            }
            if (priority0.size() > 0) {
                return priority0.get(0);
            }
            return null;
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            if (size == 0) {
                return true;
            }
            return false;
        }
    }

    /**
     * PriorityQueue from JDK is not usefull in this situation, because
     * head of the queue must have the highest priority
     */
    class MyPriorityQueue {

        class QueueElement {
            private int priority;
            private final Coordinate coordinate;
            private QueueElement next;

            public QueueElement(Coordinate coordinate, int priority) {
                this.priority = priority;
                this.coordinate = coordinate;
                this.next = null;
            }
        }

        private QueueElement head;
        private QueueElement tail;
        private int size;

        public MyPriorityQueue() {
            size = 0;
        }

        public void add(Coordinate coordinate, int priority) {
            if (size == 0) {
                head = tail = new QueueElement(coordinate, priority);
                ++size;
            } else if (size == 1) {
                if (head.coordinate.equals(coordinate)) {
                    head = tail = new QueueElement(coordinate, priority + head.priority);
                } else {
                    if (head.priority < priority) {
                        head = new QueueElement(coordinate, priority);
                    } else {
                        tail = new QueueElement(coordinate, priority);
                    }
                    head.next = tail;
                    ++size;
                }
            } else {
                QueueElement current = head;
                int newPriority = priority;
                boolean finded = false;
                // find the same
                // if is not found newPriority = priority
                // else remove finded and newPriority = findedPriorit + priority
                while (current.next != null) {
                    if (current.next.coordinate.equals(coordinate)) {
                        newPriority = current.next.priority + priority;
                        finded = true;
                        current.next = current.next.next;
                        break;
                    }
                    current = current.next;
                }

                if (!finded) {
                    ++size;
                }
                QueueElement newElement = new QueueElement(coordinate, newPriority);
                if (head.priority < newPriority) {
                    newElement.next = head;
                    head = newElement;
                    return;
                }
                if (tail.priority > newPriority) {
                    tail.next = newElement;
                    tail = newElement;
                    return;
                }

                current = head;
                while (current.next.priority > newPriority) {
                    current = current.next;
                }
                newElement.next = current.next;
                current.next = newElement;
            }
        }

        public void add(ArrayList<Coordinate> coordinates, int priority) {
            for (Coordinate coordinate : coordinates) {
                add(coordinate, priority);
            }
        }

        public boolean isEmpty() {
            if (size == 0) {
                return true;
            }
            return false;
        }

        public Coordinate getBest() {
            return head.coordinate;
        }
    }

    /**
     * constructor of the player with AI
     *
     * @param gameClass Nought or Cross
     * @param game      instance of Game in which player can move
     */
    public AI(Class gameClass, Game game) {
        super(gameClass, game);
        // all possible lines for win
        combinations = new ArrayList<>();
        Coordinate dimension100 = new Coordinate(1, 0, 0);
        Coordinate dimension010 = new Coordinate(0, 1, 0);
        Coordinate dimension001 = new Coordinate(0, 0, 1);
        Coordinate dimension110 = new Coordinate(1, 1, 0);
        Coordinate dimensionM110 = new Coordinate(-1, 1, 0);
        Coordinate dimension011 = new Coordinate(0, 1, 1);
        Coordinate dimension01M1 = new Coordinate(0, 1, -1);
        Coordinate dimension101 = new Coordinate(1, 0, 1);
        Coordinate dimensionM101 = new Coordinate(-1, 0, 1);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                combinations.add(new Line(new Coordinate(0, i, j), dimension100));
                combinations.add(new Line(new Coordinate(j, 0, i), dimension010));
                combinations.add(new Line(new Coordinate(j, i, 0), dimension001));
            }
            combinations.add(new Line(new Coordinate(0, 0, i), dimension110));
            combinations.add(new Line(new Coordinate(3, 0, i), dimensionM110));

            combinations.add(new Line(new Coordinate(i, 0, 0), dimension011));
            combinations.add(new Line(new Coordinate(i, 0, 3), dimension01M1));

            combinations.add(new Line(new Coordinate(0, i, 0), dimension101));
            combinations.add(new Line(new Coordinate(3, i, 0), dimensionM101));
        }
        combinations.add(new Line(new Coordinate(0, 0, 0), new Coordinate(1, 1, 1)));
        combinations.add(new Line(new Coordinate(3, 0, 0), new Coordinate(-1, 1, 1)));
        combinations.add(new Line(new Coordinate(0, 3, 0), new Coordinate(1, -1, 1)));//(1,1,-1)
        combinations.add(new Line(new Coordinate(0, 0, 3), new Coordinate(1, 1, -1)));
    }

    /**
     * Find next move of the AI
     *
     * @return move of the AI
     */
    public Coordinate doMove() {
        MyPriorityDeque positions = new MyPriorityDeque();
        //MyPriorityQueue positions = new MyPriorityQueue();
        int numberOfCrosses;
        int numberOfNoughts;
        ArrayList<Coordinate> emptyCells = new ArrayList<>(4);
        // creating the list of possible positions to move
        for (Line line : combinations) {
            numberOfCrosses = 0;
            numberOfNoughts = 0;
            emptyCells.clear();
            for (Coordinate coordinate : line) {
                // feeling the lists of numbers of cross, nought and empties
                if (game.getTable().get(coordinate) == null) {
                    emptyCells.add(coordinate);
                    continue;
                } else {
                    if (game.getTable().get(coordinate).getClass() == Cross.class) {
                        ++numberOfCrosses;
                        continue;
                    } else {
                        ++numberOfNoughts;
                    }
                }
            }
            // list of the rules of adding cells into the priority queue of coordinates to move
            if (numberOfNoughts == 3 && numberOfCrosses == 0) {
                return emptyCells.get(0);
            }
            if (numberOfCrosses > 0 && numberOfNoughts > 0) {
                continue;
            }
            if (numberOfCrosses == 3) {
                positions.add(emptyCells.get(0), 5);
            }
            if (numberOfCrosses == 2) {
                positions.add(emptyCells, 0);
            }
            if (numberOfNoughts == 2) {
                positions.add(emptyCells, 2);
            }
            if (numberOfNoughts == 1) {
                positions.add(emptyCells, 1);
            }

        }// end of creating
        if (positions.isEmpty()) {
            for (Coordinate coordinate : corners) {
                if (game.getTable().get(coordinate) == null) {
                    return coordinate;
                }
            }
        }
        return positions.getBest();
    }

    /**
     * Draw the next move of the AI if it is possible
     *
     * @param inputManager
     * @param assetManager
     * @param cam
     * @param cubeNode
     * @param state
     * @return true if move has done
     */
    @Override
    public boolean doMove(InputManager inputManager,
                          AssetManager assetManager, Camera cam, Node cubeNode, PCvsHumanAppState state) {
        Coordinate move = doMove();
        // drawing
        int x = move.getX();
        int y = move.getY();
        int z = move.getZ();
        String name = "Nothing" + x + y + z;
        Spatial target = cubeNode.getChild(name);
        Vector3f location = target.getLocalTranslation();
        game.updateTable(x, y, z, new Nought(x, y, z));
        //System.out.println("nought: " + x + y + z);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = assetManager.loadTexture("Textures/texture2.jpg");
        mat.setTexture("ColorMap", cube1Tex);
        Geometry nought = state.createNought("Nought" + x + y + z, location, mat);
        cubeNode.detachChild(target);
        cubeNode.attachChild(nought);
        return true;
    }
}
