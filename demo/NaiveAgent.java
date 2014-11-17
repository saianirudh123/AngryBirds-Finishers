package ab.demo;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
public class NaiveAgent implements Runnable {
    private ActionRobot aRobot;
    public boolean set_weak;
    private Random randomGenerator;
    public int currentLevel = 1,flag,done;
    public static int time_limit = 12;
    private Map<Integer,Integer> scores = new LinkedHashMap<Integer,Integer>();
    TrajectoryPlanner tp;
    private boolean firstShot;
    private Point prevTarget;
    private Point prev_release;
    public int ice_wt=25,wood_wt=30,stone_wt=50;
    public int first=1;
    // a standalone implementation of the Naive Agent
    public NaiveAgent(){
        aRobot = new ActionRobot();
        tp = new TrajectoryPlanner();
        prevTarget = null;
        firstShot = true;
        randomGenerator = new Random();
        // --- go to the Poached Eggs episode level selection page ---
        ActionRobot.GoFromMainMenuToLevelSelection();
    }
    // run the client
    public void run(){
        first=1;
        aRobot.loadLevel(currentLevel);
        while (true) {
            GameState state = solve();
            if (state == GameState.WON) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int score = StateUtil.getScore(ActionRobot.proxy);
                if(!scores.containsKey(currentLevel))
                    scores.put(currentLevel, score);
                else
                {
                    if(scores.get(currentLevel) < score)
                        scores.put(currentLevel, score);
                }
                int totalScore = 0;
                for(Integer key: scores.keySet()){
                    totalScore += scores.get(key);
                    System.out.println(" Level " + key+ " Score: " + scores.get(key) + " ");
                }
                System.out.println("Total Score: " + totalScore);
                first=1;
                //System.out.println("Pavan we can Edit this code woot");
                aRobot.loadLevel(++currentLevel);
                // make a new trajectory planner whenever a new level is entered
                tp = new TrajectoryPlanner();
                // first shot on this level, try high shot first
                firstShot = true;
            } else if (state == GameState.LOST) {
                System.out.println("Restart");
                aRobot.restartLevel();
            } else if (state == GameState.LEVEL_SELECTION) {
                System.out.println("Unexpected level selection page, go to the last current level : "+ currentLevel);
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.MAIN_MENU) {
                System.out.println("Unexpected main menu page, go to the last current level : "+ currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.EPISODE_MENU) {
                System.out.println("Unexpected episode menu page, go to the last current level : "+ currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            }
        }
    }
    private double distance(Point p1, Point p2) {
        return Math.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }

    public ArrayList<Point> WeakPoints_Vertical_blocks_on_Horizontal(List<ABObject> blocks) {
        //System.out.println("Just entered Pavan");
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
        if(dinx==0 || diny==0){
            return new Point(0,0);
        }
        return new Point(numx/dinx,numy/diny);
    }
    public String[][] visualize(List<ABObject> blocks){
        //List<ABObject> blocks = v.findBlocksMBR();
        ABObject block;
        String[][] matrix=new String[blocks.size()][2];
        for(int i=0;i<blocks.size();i++){
            block=blocks.get(i);
            if(block.getType().id==10)// Object ID for Ice
            {
                matrix[block.id][0]="Ice";
                matrix[block.id][1]=String.valueOf(block.shape);
            }
            if(block.getType().id==11) // Object ID for wood
            {
                matrix[block.id][0]="Wood";
                matrix[block.id][1]=String.valueOf(block.shape);
            }
            if(block.getType().id==12) // Object ID for Stone
            {
                matrix[block.id][0]="Stone";
                matrix[block.id][1]=String.valueOf(block.shape);
            }
        }
        for (int i = 0; i <blocks.size() ; i++) {
            block=blocks.get(i);
            //System.out.println(block.getSize());
            //System.out.println(block.id+" "+matrix[block.id][0]+" "+matrix[block.id][1]);
        }
        return matrix;
    }
    public GameState solve()
    {
           set_weak=false;
        // capture Image
        BufferedImage screenshot = ActionRobot.doScreenShot();
        // process image
        Vision vision = new Vision(screenshot);
        // find the slingshot
        Rectangle sling = vision.findSlingshotMBR();
        // confirm the slingshot
        while (sling == null && aRobot.getState() == GameState.PLAYING){
            System.out.println("No slingshot detected. Please remove pop up or zoom out");
            ActionRobot.fullyZoomOut();
            screenshot = ActionRobot.doScreenShot();
            vision = new Vision(screenshot);
            sling = vision.findSlingshotMBR();
        }
        // get all the pigs
        List<ABObject> blocks=vision.findBlocksMBR();
        List<ABObject> pigs = vision.findPigsMBR();
        ArrayList<ABObject> blks=new ArrayList<ABObject>();
        for(int i=0;i<blocks.size();i++){
            blks.add(blocks.get(i));
        }

        ArrayList<ABObject> pgs=new ArrayList<ABObject>();
        for(int i=0;i<pigs.size();i++){
            pgs.add(pigs.get(i));
        }

        System.out.println("# of blocks is"+blks.size());

        //Finisher finish=new Finisher(Point cm,ArrayList<Point> wps);
        //System.out.println(cm);
        GameState state = aRobot.getState();
        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {
            Finisher fin = new Finisher(blks,pgs,sling);
            //String[][] g=visualize(blocks);
            Point cm=centreOfMass(fin.admissible.obj);
            if (!pigs.isEmpty()) {
                Point releasePoint = null;
                Shot shot = new Shot();
                int dx,dy;
                {
                    // random pick up a pig
                    ABObject pig;
                    int height=10000,max=0;
                    for(int i=0;i<=pigs.size()-1;i++){
                        pig=pigs.get(i);
                        if(height>pig.getCenter().y){
                            height=pig.getCenter().y;
                            max=i;
                        }
                    }
                    pig=pigs.get(max);
                    int leastX=10000;
                    int leastX_maxY=0;
                    for(int i=0;i<pigs.size();i++){
                        if(pig.getCenter().y==height){
                            if(pig.getCenter().x<leastX){
                                leastX=pig.getCenter().x;
                                leastX_maxY=i;
                            }
                        }
                    }
                    pig=pigs.get(leastX_maxY);
                    //List<ABObject> blocks=vision.findBlocksMBR();
                    //Point _tpt = pig.getCenter();// if the target is very close to before, randomly choose a
                    //System.out.println("_tpt"+_tpt);
                    Point _tpt = pig.getCenter();
                    //System.out.println("target"+_tpt);

                    if(/*first==1*/true) {

                        ArrayList<Point> wpt=WeakPoints_Vertical_blocks_on_Horizontal(fin.admissible.obj);
                        if(wpt.size()==0){
                            System.out.println("Entered1");
                            set_weak=false;
                            _tpt=cm;
                        }
                        else {
                            int minIndex=0;
                            set_weak=true;
                            System.out.println("Wpts size "+wpt.size());
                            for(int i=0;i<wpt.size();i++){
                                System.out.println("wk#"+i+"-"+wpt.get(i));
                            }
                            int minX=wpt.get(0).x;

                            for(int i=1;i<wpt.size();i++){
                                if(wpt.get(i).x<minX){
                                    minIndex=i;
                                    minX=wpt.get(i).x;
                                }
                            }
                            _tpt = WeakPoints_Vertical_blocks_on_Horizontal(blks).get(minIndex);
                        }
                        //System.out.println("Center Of Mass"+_tpt);
                    }
                    // point near it
                    if (prevTarget != null && distance(prevTarget, _tpt) < 10) {
                        double _angle = randomGenerator.nextDouble() * Math.PI * 2;
                        _tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
                        _tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
                        System.out.println("Randomly changing to " + _tpt);
                    }
                    prevTarget = new Point(_tpt.x, _tpt.y);
                    // estimate the trajectory
                    ArrayList<Point> pts = tp.estimateLaunchPoint(sling, _tpt);
                    // do a high shot when entering a level to find an accurate velocity
                    if(firstShot){
                        prev_release=null;
                    }
                    if (firstShot && pts.size() > 1)
                    {
                        System.out.print(pts.size()+" ");
                        System.out.println("E>1");
                        releasePoint = pts.get(1);
                        if(first==1 ||set_weak==true){
                            releasePoint = pts.get(0);
                        }
                        System.out.println(releasePoint);
                        if(firstShot){
                            flag=1;
                        }
                    }
                    else if (pts.size() == 1) {
                        releasePoint = pts.get(0);
                        double ra = tp.getReleaseAngle(sling,releasePoint);
                        System.out.println(releasePoint);
                        System.out.println("E==1");
                    }
                    else if (pts.size() == 2)
                    {
                        // randomly choose between the trajectories, with a 1 in
                        // 6 chance of choosing the high one
						/*if (randomGenerator.nextInt(6) == 0)
							releasePoint = pts.get(1);
						else
							releasePoint = pts.get(0);*/
                        System.out.println("E2");
                        releasePoint = pts.get(1);
                        if(first==1 || set_weak==true){
                            releasePoint = pts.get(0);
                        }
                        System.out.println(releasePoint);
                    }
                    else
                    if(pts.isEmpty())
                    {
                        System.out.println("No release point found for the target");
                        System.out.println("Try a shot with 45 degree");
                        releasePoint = tp.findReleasePoint(sling, Math.PI/4);
                    }
                    // Get the reference point
                    Point refPoint = tp.getReferencePoint(sling);
                    //prev_release=releasePoint;
                    //Calculate the tapping time according the bird type
                    if (releasePoint != null) {
                        double releaseAngle = tp.getReleaseAngle(sling,releasePoint);
                        //System.out.println("Release Point: " + releasePoint);
                        //System.out.println("Release Angle: " + Math.toDegrees(releaseAngle));
                        int tapInterval = 0;
                        switch (aRobot.getBirdTypeOnSling())
                        {
                            case RedBird:
                                tapInterval = 0; break;               // start of trajectory
                            case YellowBird:
                                tapInterval = 85 + randomGenerator.nextInt(5);break; // 65-90% of the way
                            case WhiteBird:
                                tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
                            case BlackBird:
                                tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
                            case BlueBird:
                                tapInterval =  65 + randomGenerator.nextInt(20);break; // 65-85% of the way
                            default:
                                tapInterval =  60;
                        }
                        int tapTime = tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
                        dx = (int)releasePoint.getX() - refPoint.x;
                        dy = (int)releasePoint.getY() - refPoint.y;
                        shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);
                    }
                    else
                    {
                        System.err.println("No Release Point Found");
                        return state;
                    }
                }

                // check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
                {
                    ActionRobot.fullyZoomOut();
                    screenshot = ActionRobot.doScreenShot();
                    vision = new Vision(screenshot);
                    Rectangle _sling = vision.findSlingshotMBR();
                    if(_sling != null)
                    {
                        double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
                        if(scale_diff < 25)
                        {
                            if(dx < 0)
                            {
                                aRobot.cshoot(shot);
                                state = aRobot.getState();
                                if ( state == GameState.PLAYING )
                                {
                                    screenshot = ActionRobot.doScreenShot();
                                    vision = new Vision(screenshot);
                                    List<Point> traj = vision.findTrajPoints();
                                    tp.adjustTrajectory(traj, sling, releasePoint);
                                    firstShot = false;
                                }
                            }
                        }
                        else
                            System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
                    }
                    else
                        System.out.println("no sling detected, can not execute the shot, will re-segement the image");
                }
            }
        }
        first=0;
        return state;
    }
    public static void main(String args[]) {
        NaiveAgent na = new NaiveAgent();
        if (args.length > 0)
            na.currentLevel = Integer.parseInt(args[0]);
        na.run();
    }
}