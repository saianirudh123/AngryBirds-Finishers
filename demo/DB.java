package ab.demo;
import java.awt.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.Integer;
import java.util.ArrayList;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
public class DB{
    ArrayList<Double> wkpts;
    ArrayList<Double> compalsory_hit;
    ArrayList<Double> cm_of_Substructure;
    ArrayList<Integer> u_wkpts;
    ArrayList<Integer> u_compalsory_wkpts;
    ArrayList<Integer> u_cm_of_Substructure;
    public DB(ArrayList<Double> wkpts,ArrayList<Double> compalsory_hit,ArrayList<Double> cm_of_Substructure){
        this.wkpts=wkpts;
        this.compalsory_hit=compalsory_hit;
        this.cm_of_Substructure=cm_of_Substructure;

    }

}