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
    public int left,right,top,bottom;
    public boolean haspig;
    public ArrayList<ABObject> obj;
    public SubStructure(){
        obj=new ArrayList<ABObject>();
    }
    public void add(ABObject o){
        obj.add(o);
    }
    public void createBorber(){
        left=100000;right=-10;top=10000000;bottom=-10;
        for(int i=0;i<obj.size();i++){
            ABObject o=obj.get(i);
            Dimension size1 = o.getSize();
            Point center1 = o.getCenter();
            int wi1 = (int) size1.width;
            int hi1 = (int) size1.height;
            Point ltop1 = new Point(center1.x - (wi1 / 2), center1.y - (hi1 / 2));
            Point lbottom1 = new Point(center1.x - (wi1 / 2), center1.y + (hi1 / 2));
            Point rtop1 = new Point(center1.x + (wi1 / 2), center1.y - (hi1 / 2));
            Point rbottom1 = new Point(center1.x + (wi1 / 2), center1.y + (hi1 / 2));
            if(ltop1.x<left){
                left=ltop1.x;
            }
            if(ltop1.y<top){
                top=ltop1.y;
            }
            if(rbottom1.x>right){
                right=rbottom1.x;
            }
            if(rbottom1.y>bottom){
                bottom=rbottom1.y;
            }
        }
    }
}