package ab.demo;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.*;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
public class Finisher{
    int [][] potential=new int[3][4];
    int glass=0;
    int wood=1;
    int stone=2;
    int red=0,blue=1,yellow=2,black=3;

    ArrayList<SubStructure> list;
    ArrayList<ABObject> objects;
    ArrayList<ABObject> pigs;
    ArrayList<Point> target;
    Rectangle sling;
    Structure s;
    Point _tpt,cm;
    SubStructure admissible;
    public int ice_wt=25,wood_wt=30,stone_wt=50;
    public Finisher(ArrayList <ABObject> objs ,ArrayList<ABObject> pigs,Rectangle slng) {
        //solve();
        objects = objs;
        this.pigs = pigs;
        this.sling = slng;


        potential[glass][red]=650;
        potential[glass][blue]=250;
        potential[glass][yellow]=200;
        potential[glass][black]=500;
        potential[wood][red]=800;
        potential[wood][blue]=750;
        potential[wood][yellow]=250;
        potential[wood][black]=500;
        potential[stone][red]=1050;
        potential[stone][blue]=1000;
        potential[stone][yellow]=1000;
        potential[stone][black]=500;

        //this.createSubStructures();
        s = new Structure(objects, pigs);
        list = s.list;
        //System.out.println("No of Components"+list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(" ss #" + i + "has" + list.get(i).obj.size());
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).createBorber();
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).haspig = this.hasPigInIt(list.get(i));
            //System.out.println("SS #" + i + " having" + list.get(i).obj.size() + "Objects" + "Has pig" + this.hasPigInIt(list.get(i)));
            /*if(this.hasPigInIt(list.get(i))){
                ArrayList<Point> temp = getWeakPoints(list.get(i));
                for (int j = 0; j < temp.size(); j++) {
                    wpts.add(temp.get(j));
                }
            }*/
        }
        SubStructure ss;
        admissible=null;
        ArrayList<Point> ss_wk_pts=new ArrayList<Point>();
        for(int i=0;i<list.size();i++){
            ss=list.get(i);
            if(ss.haspig==false){
                continue;
            }
            if(admissible==null){
                admissible=ss;
                continue;
            }
            if(ss.left < admissible.left){
                admissible=ss;
            }
        }
        ss=admissible;
        if(ss == null){
            int max1=0;
            for(int i=0;i<list.size();i++){
                SubStructure ss1=list.get(i);
                if(ss==null){
                    ss=ss1;
                    max1=ss.obj.size();
                }
                if(ss.obj.size()>max1){
                    max1=ss1.obj.size();
                    ss=ss1;
                }
            }
        }
        admissible=ss;
        if(ss!=null){
            if(ss.haspig){
                ss_wk_pts=WeakPoints_Vertical_blocks_on_Horizontal(ss.obj);
            }
        }
        /*TrajectoryPlanner tp = new TrajectoryPlanner();
        if (sling != null) {
            Point refPoint = tp.getReferencePoint(sling);

        if(pigs.size()>0 && sling!=null &&objects!=null) {
            ArrayList<Point> pts = tp.estimateLaunchPoint(sling, pigs.get(0).getCenter());
            double releaseAngle = tp.getReleaseAngle(sling,pigs.get(0).getCenter());
            ArrayList<ABObject> obstacles = this.obstacleDetector(objects, sling,releaseAngle,pigs.get(0));
            System.out.println("Obstacle"+obstacles.size());
            for(int i=0;i<obstacles.size();i++){
                System.out.println("OB #"+i+"--"+obstacles.get(i));
            }
            }
        }*/

        /*for(int i=0;i<lst.size();i++){
               for(int j=0;j<lst.get(i).obj.size();j++){
                   System.out.println(i+"#"+lst.get(i).obj.get(j));
               }
                System.out.println("\n\n\n");
        }*/

            //System.out.println(list.get(0).obj.size());
            //new Future(ArrayList <ABObjects> all);//**For Learning Scenario**//
        }
    public boolean iscompalsoryHit(ArrayList<ABObject> objs,ABObject pig,double angle,Rectangle sling,ABObject bird) {
        ArrayList<ABObject> pigs=new ArrayList<ABObject>();
        pigs.add(pig);
        Finisher fin = new Finisher(objs,pigs, sling);
            TrajectoryPlanner tp = new TrajectoryPlanner();
            ArrayList<Point> pts = tp.estimateLaunchPoint(sling, pig.getCenter());
            double releaseAngle = tp.getReleaseAngle(sling, pig.getCenter());
            ArrayList<ABObject> blks = fin.obstacleDetector(objs, sling, releaseAngle, pig);
            if (sling != null) {
                int pot=1000;
                int y=0;
                if(bird.type.equals("blue")){
                        y=1;
                }
                if(bird.type.equals("black")){
                        y=3;
                }
                if(bird.type.equals("yellow")){
                        y=2;
                }
                if(bird.type.equals("red")){
                        y=0;
                }
                for (int j=0;j<blks.size();j++) {
                    if(pot<=0){
                           return false;
                    }
                    if(blks.get(j).type.equals("wood")) {
                        pot = pot - potential[wood][y];
                    }
                    if(blks.get(j).type.equals("glass")) {
                        pot = pot - potential[glass][y];
                    }
                    if(blks.get(j).type.equals("stone")) {
                        pot = pot - potential[stone][y];
                    }

                }
            }
        return true;
    }
    public Point centreOfMass(List<ABObject> blocks){
        int cmx=0,cmy=0;
        int numx=0,dinx=0;
        int numy=0,diny=0;
        for(int i=0;i<blocks.size();i++){
            ABObject block=blocks.get(i);
            Dimension size=block.getSize();
            Point center=block.getCenter();
            if(block.getType().id==10){
                numx=numx+center.x*size.width*size.height*ice_wt;
                dinx+=size.width*size.height*ice_wt;
                numy=numy+center.y*size.width*size.height*ice_wt;
                diny+=size.width*size.height*ice_wt;
                continue;
            }
            if(block.getType().id==11){
                numx=numx+center.x*size.width*size.height*wood_wt;
                dinx+=size.width*size.height*wood_wt;
                numy=numy+center.y*size.width*size.height*wood_wt;
                diny+=size.width*size.height*wood_wt;
                continue;
            }
            if(block.getType().id==12){
                numx=numx+center.x*size.width*size.height*stone_wt;
                dinx+=size.width*size.height*stone_wt;
                numy=numy+center.y*size.width*size.height*stone_wt;
                diny+=size.width*size.height*stone_wt;
                continue;
            }
        }
        //System.out.println("x"+" "+numx/dinx +"  "+"y"+numy/diny);
        if(dinx!=0 && diny!=0) {
            return new Point(numx / dinx, numy / diny);
        }
        return new Point(0,0);
    }
    public ArrayList<ABObject> obstacleDetector(ArrayList<ABObject> objs,Rectangle sling,Double ang, ABObject pig)
    {
        TrajectoryPlanner tp=new TrajectoryPlanner();

        ArrayList<Point> edgesl = new ArrayList<Point>();
        ArrayList<Point> edgesr = new ArrayList<Point>();
        ArrayList<ABObject> blcks = new ArrayList<ABObject>();
        Point releasePoint = tp.findReleasePoint(sling, ang);//estimates the release point according to the given angle

        List<Point> traj1 = new ArrayList<Point>();
        // List<Point> traj2 = new ArrayList<Point>();
        traj1 = tp.predictTrajectory(sling, releasePoint);// predicts the trajectory according to the slingshot and the release point
        for(int i =0 ;i < objs.size();++i) // this is the loop for getting the top left and top right corner of a blck they are being stored in
        //edgesl and edgesr
        {
            Dimension size1 = objs.get(i).getSize();
            Point center1 = objs.get(i).getCenter();
            int wi1 = (int) size1.width;
            int hi1 = (int) size1.height;
            Point ltop1 = new Point(center1.x - (wi1 / 2), center1.y - (hi1 / 2));
            Point lbottom1 = new Point(center1.x - (wi1 / 2), center1.y + (hi1 / 2));
            Point rtop1 = new Point(center1.x + (wi1 / 2), center1.y - (hi1 / 2));
            Point rbottom1 = new Point(center1.x + (wi1 / 2), center1.y + (hi1 / 2));
            int minx = Math.min(Math.min(ltop1.x, rtop1.x), Math.min(lbottom1.x, rbottom1.x));
            int maxx = Math.max(Math.max(ltop1.x, rtop1.x), Math.max(lbottom1.x, rbottom1.x));
            int miny = Math.min(Math.min(ltop1.y, rtop1.y), Math.min(lbottom1.y, rbottom1.y));
            int maxy = Math.max(Math.max(ltop1.y, rtop1.y), Math.max(lbottom1.y, rbottom1.y));
            edgesl.add(ltop1);
            //edges.add(lbottom1);
            edgesr.add(rtop1);

            System.out.println("minx : " + minx);
            System.out.println("maxx : " + maxx);
            System.out.println("miny : " + miny);
            System.out.println("maxy : " + maxy);
            //edges.add(rbottom1);
            {
                for (int j = 0; j < traj1.size(); ++j) {
                    if(traj1.get(i).x < pig.getCenter().x)
                        if ((minx <= traj1.get(i).x && maxx>= traj1.get(i).x) && (miny <= traj1.get(i).y && maxy >= traj1.get(i).y) ) {
                            blcks.add(objs.get(j));
                            break;
                        }
                }
            }
        }
        for(int i = 0 ; i < traj1.size() ; ++i)
        {
            System.out.println(traj1.get(i).x + " "+ traj1.get(i).y);
        }
        for(int i =0 ; i < blcks.size() ; ++i)
        {
            System.out.println("Hello");
            System.out.println(blcks.get(i).getType());
        }

        return blcks;
    }
        /*for (int i = 0; i < traj1.size(); ++i) {
            System.out.println(traj1.get(i).x + " " + traj1.get(i).y);
        }*/
        /*for (int i = 0; i < blcks.size(); ++i) {
            System.out.println(blcks.get(i).getType() + "at" + blcks.get(i));
        }*/
        public ArrayList<Point> weakPoints_Pavans(ArrayList<ABObject> blocks){
        ArrayList<Point> weak=new ArrayList<Point>();
        ArrayList<Point> pts=new ArrayList<Point>();
        ArrayList<ABObject> weak_blocks=new ArrayList<ABObject>();
        ArrayList<Point> topcorners=new ArrayList<Point>();
        ArrayList<Point> bottomcorners=new ArrayList<Point>();
        int minx=10000;
        ABObject temp;
        for(int i=0;i<blocks.size();i++){
            ABObject block=blocks.get(i);
            Dimension size=block.getSize();
            Point center=block.getCenter();
            int wi=(int) size.width;
            int hi=(int) size.height;
            Point top= new Point(center.x - (wi / 2), center.y - (hi / 2));
            Point bottom=new Point(center.x-(wi/2) , center.y+(hi/2));
            if(minx==10000){
                minx=top.x;
                weak_blocks.add(block);
                continue;
            }
            if(Math.abs(minx - top.x)<5){
                weak_blocks.add(block);
                continue;
            }
            if(top.x>minx){
                continue;
            }
            if(top.x<minx){
                minx=top.x;
                weak_blocks.clear();
                weak_blocks.add(block);
            }
        }
        /*for(int i=0;i<weak_blocks.size();i++){
            System.out.println(weak_blocks.get(i).x);
        }*/
        return weak;
    }



    public boolean hasPigInIt(SubStructure ss){
        for(int i=0;i<pigs.size();i++){
            Point pig=pigs.get(i).getCenter();
            if(pig.x>=ss.left && pig.x<=ss.right && pig.y>=ss.top && pig.y <= ss.bottom){
                return true;
            }
        }
        return false;
    }
    public ArrayList<SubStructure> getSubStructures(){
            return list;
        }
    public ArrayList<Point> WeakPoints_Vertical_blocks_on_Horizontal(List<ABObject> blocks) {
        ArrayList<Point> weak_points = new ArrayList<Point>();
        ArrayList<Point> weak_points1 = new ArrayList<Point>();
        // ArrayList<ABObject> weak_blocks=new ArrayList<ABObject>();
        for (int i = 0; i < blocks.size(); i++) {
            int nblocks=0;
            ABObject block = blocks.get(i);
            Dimension size = block.getSize();
            Point center = block.getCenter();
            int wi = (int) size.width;
            int hi = (int) size.height;
            Point top = new Point(center.x - (wi / 2), center.y - (hi / 2));
            Point bottom = new Point(center.x - (wi / 2), center.y + (hi / 2));
            if (wi>hi/*||Math.abs(top.y-bottom.y)<10*/) {
                //System.out.println("iffff");
                for (int j = 0; j < blocks.size(); j++) {
                    ABObject block1 = blocks.get(j);
                    Dimension size1 = block1.getSize();
                    Point center1 = block1.getCenter();
                    int wi1 = (int) size1.width;
                    int hi1= (int) size1.height;
                    Point top1 = new Point(center1.x - (wi1 / 2), center1.y - (hi1 / 2));
                    Point bottom1 = new Point(center1.x - (wi1 / 2), center1.y + (hi1 / 2));
                    //System.out.println("Height"+hi1+"width"+wi1);;
                    if(block1!=block){
                        if(top1.y>bottom.y && hi1>wi1/*&&&&(bottom1.x>=top.x)&&(bottom1.x<=bottom.x)*/&&isconnected(block,block1))
                        {
                            //System.out.println("final iff");
                            nblocks++;
                            Point pt=center;
                            pt.x=top.x;
                            pt.y=top.y+hi;
                            weak_points1.add(pt);
                        }
                    }
                }
                if(nblocks==1||nblocks==2){
                    Point pt=center;
                    pt.x=top.x;
                    pt.y=top.y+hi;
                    weak_points.add(pt);
                }

            }
        }
        if(weak_points.size()==0){
            weak_points=weak_points1;
        }
       /* if(weak_points.size()==0){
            //center of mass
        }*/
        return weak_points;
    }
    public  boolean isconnected(ABObject o, ABObject o1) {

        Dimension size1 = o.getSize();
        Point center1 = o.getCenter();
        int wi1 = (int) size1.width;
        int hi1 = (int) size1.height;
        Point ltop1 = new Point(center1.x - (wi1 / 2), center1.y - (hi1 / 2));
        Point lbottom1 = new Point(center1.x - (wi1 / 2), center1.y + (hi1 / 2));
        Point rtop1 = new Point(center1.x + (wi1 / 2), center1.y - (hi1 / 2));
        Point rbottom1 = new Point(center1.x + (wi1 / 2), center1.y + (hi1 / 2));
        Dimension size2 = o1.getSize();
        Point center2 = o1.getCenter();
        int wi2 = (int) size2.width;
        int hi2 = (int) size2.height;
        Point ltop2 = new Point(center2.x - (wi2 / 2), center2.y - (hi2 / 2));
        Point lbottom2 = new Point(center2.x - (wi2 / 2), center2.y + (hi2 / 2));
        Point rtop2 = new Point(center2.x + (wi2 / 2), center2.y - (hi2 / 2));
        Point rbottom2 = new Point(center2.x + (wi2 / 2), center2.y + (hi2 / 2));


        Point Pts2[] = new Point[4];
        Pts2[0] = ltop2;
        Pts2[1] = lbottom2;
        Pts2[2] = rtop2;
        Pts2[3] = rbottom2;
        Point Pts1[] = new Point[4];
        Pts1[0] = ltop1;
        Pts1[1] = lbottom1;
        Pts1[2] = rtop1;
        Pts1[3] = rbottom1;
        /*System.out.println("Object1 left top:"+ltop1+"Objec1 right bottom:"+rbottom1);
        System.out.println("Object2 left top:"+ltop2+"Objec2 right bottom:"+rbottom2);*/
        int lt = 0, lb = 1, rt = 2, rb = 3;
        int xx, yy;
        for (int i = 0; i < 4; i++)
        {
            xx = Pts2[i].x;
            yy = Pts2[i].y;
            for (int j = 0; j < 4; j++)
            {
                if ((ltop1.x - rtop1.x) * (yy - rtop1.y) == (ltop1.y - rtop1.y) * (xx - rtop1.x) ||
                        (ltop1.x - lbottom1.x) * (yy - lbottom1.y) == (ltop1.y - lbottom1.y) * (xx - lbottom1.x) ||
                        (rbottom1.x - lbottom1.x) * (yy - lbottom1.y) == (rbottom1.y - lbottom1.y) * (xx - lbottom1.x) ||
                        (rbottom1.x - rtop1.x) * (yy - rtop1.y) == (rbottom1.y - rtop1.y) * (xx - rtop1.x))
                {

                    return true;
                }
            }
        }
        return false;
    }
    public static void main(String args[]){
        //calls FInisher Agent
    }
}