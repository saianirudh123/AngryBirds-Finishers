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
public class Finisher{
    ArrayList<SubStructure> lst;
    ArrayList<ABObject> objects;
    Structure s;
    public Finisher(ArrayList <ABObject> objs ){
        //solve();
        objects=objs;
        //this.createSubStructures();
        s=new Structure(objects);
        lst=s.list;
        System.out.println("No of Components"+lst.size());
        /*for(int i=0;i<lst.size();i++){
               for(int j=0;j<lst.get(i).obj.size();j++){
                   System.out.println(i+"#"+lst.get(i).obj.get(j));
               }
                System.out.println("\n\n\n");
        }*/
        //System.out.println(list.get(0).obj.size());
        //new Future(ArrayList <ABObjects> all);//**For Learning Scenario**//
    }
    public ArrayList<SubStructure> getSubStructures(){
            return lst;
        }
    public static void main(String args[]){
        //calls FInisher Agent
    }
}