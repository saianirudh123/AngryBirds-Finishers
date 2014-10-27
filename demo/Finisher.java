package ab.demo;
import java.awt.*;
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
    public class SubStructure{
        public ArrayList<ABObject> obj;
        public SubStructure(){
            obj=new ArrayList<ABObject>();
        }
        public void add(ABObject o){
            obj.add(o);
        }
    }
    public class Structure{
        ArrayList<SubStructure> list;
        ArrayList<ABObject> objects;
        ArrayList<Point> wpts;
        public Structure(ArrayList<ABObject> objs){
            this.objects=objs;
            this.wpts=new ArrayList<Point>();
            list=new ArrayList<SubStructure>();
            this.createSubStructures();
            for(int i=0;i<list.size();i++){
                wpts.add(getWeakPoints(list.get(i)));
            }
        }
        public ArrayList<Point> getWeakPoints(SubStructure ss){
            ArrayList<Point> w=new ArrayLis<Point>();
            //required to be coded
            return w;
            //return wpts;
        }
        public boolean connected(ABObject o,ABObject o1){
            return true;
        }
        public void createSubStructures(){
            for(int i=0;i<objects.size();i++){
                ABObject o=objects.get(i);
                if(i==0){
                    SubStructure ss=new SubStructure();
                    ss.add(o);
                    list.add(ss);
                    continue;
                }
                ArrayList<Integer> con=new ArrayList<Integer>();
                for(int j=0;j<list.size();j++){
                    SubStructure ss1=list.get(j);
                    for(int k=0;k<ss1.obj.size();k++){
                        ABObject o1=ss1.obj.get(k);
                        if(this.connected(o,o1)){
                            con.add(j);
                            break;
                        }
                    }
                }
                if(con.size()==0){
                    SubStructure ss2=new SubStructure();
                    ss2.add(o);
                    list.add(ss2);
                }
                SubStructure temp=list.get(0);
                for(int j=1;j<con.size();j++){
                    SubStructure temp1=list.get(j);
                    for(int k=0;k<temp1.obj.size();k++){
                        temp.add(temp1.obj.get(k));
                    }
                }
                for(int j=1;j<con.size();j++){
                    list.remove(j);
                }
            }
        }
        public ArrayList<SubStructure> getSubStructures(){
            return list;
        }
    }
    public Finisher(ArrayList <ABObject> objects ){
        //solve();
        Structure s=new Structure(objects);
        ArrayList<SubStructure> list=s.getSubStructures();

        //new Future(ArrayList <ABObjects> all);//**For Learning Scenario**//
    }

    public static void main(String args[]){
        //calls FInisher Agent
    }
}