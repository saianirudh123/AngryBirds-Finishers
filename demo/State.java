package ab.demo;
import java.util.ArrayList;
import java.awt.*;

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class State{
    public ArrayList<ABObject> objects;
    public ArrayList<ABObject> pigs;
    public ArrayList<Point> pigs_pos;
    public ArrayList<Point> objects_pos;
    public ArrayList<Point> weak_pos;
    public State(){
        objects=new ArrayList<ABObject>();
        pigs=new ArrayList<ABObject>();
        this.objects_pos=new ArrayList<Point>();
    }
    public State(ArrayList<ABObject> objects,ArrayList<ABObject> pigs){
        this.objects=objects;
        this.pigs=pigs;
    }
    public State(ArrayList<ABObject> objects){
           this.pigs=pigs;
    }
    public static void main(String args[]){

    }
}