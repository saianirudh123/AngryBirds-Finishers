package ab.demo;
import java.awt.*;
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
public class SubStructure{
    public ArrayList<ABObject> obj;
    public SubStructure(){
        obj=new ArrayList<ABObject>();
    }
    public void add(ABObject o){
        obj.add(o);
    }
}