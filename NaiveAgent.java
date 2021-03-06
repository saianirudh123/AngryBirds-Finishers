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
    public NaiveAgent() {
        aRobot = new ActionRobot();
        tp = new TrajectoryPlanner();
        prevTarget = null;
        firstShot = true;
        randomGenerator = new Random();
        // --x- go to the Poached Eggs episode level selection page ---
        ActionRobot.GoFromMainMenuToLevelSelection();
    }
    // run the client
    public void run() {
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
                    System.out.println(" Level " + key
                            + " Score: " + scores.get(key) + " ");
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
                System.out
                        .println("Unexpected level selection page, go to the last current level : "
                                + currentLevel);
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.MAIN_MENU) {
                System.out
                        .println("Unexpected main menu page, go to the last current level : "
                                + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.EPISODE_MENU) {
                System.out
                        .println("Unexpected episode menu page, go to the last current level : "
                                + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            }

        }

    }

    private double distance(Point p1, Point p2) {
        return Math.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }
    public ArrayList<Point> WeakPoints(List<ABObject> blocks){
        ArrayList<Point> weak = new ArrayList<Point>();
        ArrayList<Point> pts = new ArrayList<Point>();
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
            Point top  = new Point(center.x - (wi / 2), center.y - (hi / 2));
            Point bottom = new Point(center.x-(wi/2) , center.y+(hi/2));
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
        for(int i=0;i<weak_blocks.size();i++){
            System.out.println(weak_blocks.get(i).x);
        }

        return weak;
    }
    public ArrayList<Point> WeakPoints_Vertical_blocks_on_Horizontal(List<ABObject> blocks) {
        ArrayList<Point> weak_points = new ArrayList<Point>();
        ArrayList<ABObject> weak_blocks=new ArrayList<ABObject>();
        for (int i = 0; i < blocks.size(); i++) {
            ABObject block = blocks.get(i);
            Dimension size = block.getSize();
            Point center = block.getCenter();
            int wi = (int) size.width;
            int hi = (int) size.height;
            Point top = new Point(center.x - (wi / 2), center.y - (hi / 2));
            Point bottom = new Point(center.x - (wi / 2), center.y + (hi / 2));
            if (Math.abs(top.y-bottom.y)<10) {
                for (int j = 0; j < blocks.size(); j++) {
                    ABObject block1 = blocks.get(i);
                    size = block1.getSize();
                    center = block1.getCenter();
                    wi = (int) size.width;
                    hi = (int) size.height;
                    Point top1 = new Point(center.x - (wi / 2), center.y - (hi / 2));
                    Point bottom1 = new Point(center.x - (wi / 2), center.y + (hi / 2));
                    if(block1!=block){
                        if(Math.abs(bottom1.y-top.y)<10&&Math.abs(top1.x-bottom1.x)<10&&(bottom1.x>=top.x)&&(bottom1.x>=bottom.x)){
                            weak_points.add(block1.getCenter());
                        }
                    }
                }
            }
        }
        return weak_points;
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
        String[][] g = visualize(blocks);
        Point cm = centreOfMass(blocks);
        ArrayList<Point> wps = WeakPoints(blocks);
        List<ABObject> pigs = vision.findPigsMBR();
        //System.out.println(cm);
        GameState state = aRobot.getState();
        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {

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
                    if(first==1) {
                        _tpt = cm;
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
                        System.out.println("E>1");
                        releasePoint = pts.get(1);
                        if(first==1){
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
                        /*if(prev_release!=null) {
                            double pra = tp.getReleaseAngle(sling, prev_release);
                            if (pra - 5 <= ra && pra + 5 >= ra) {
                                releasePoint = pts.get(1);
                            }
                        }*/
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
                        if(first==1){
                            releasePoint = pts.get(0);
                        }
                        /*double ra = tp.getReleaseAngle(sling,releasePoint);
                        if(prev_release!=null) {
                            double pra = tp.getReleaseAngle(sling, prev_release);
                            if(flag==2){
                                if (pra - 3 <= ra && pra + 3 >= ra) {
                                    System.out.println("E1");
                                    releasePoint = pts.get(1);
                                    flag=1;
                                    done=1;
                                }
                            }
                            if (done ==0 && flag==1 && pra - 3 <= ra && pra + 3 >= ra) {
                                System.out.println("E2");
                                releasePoint = pts.get(0);
                                flag=2;
                            }
                        }
                        done=0;*/
                        System.out.println(releasePoint);
                        /*if(firstShot){
                            System.out.println("f1");
                            flag=1;
                        }*/
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
                        double releaseAngle = tp.getReleaseAngle(sling,
                                releasePoint);
                        //System.out.println("Release Point: " + releasePoint);
                        //System.out.println("Release Angle: " + Math.toDegrees(releaseAngle));
                        int tapInterval = 0;
                        switch (aRobot.getBirdTypeOnSling())
                        {

                            case RedBird:
                                tapInterval = 0; break;               // start of trajectory
                            case YellowBird:
                                tapInterval = 65 + randomGenerator.nextInt(25);break; // 65-90% of the way
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