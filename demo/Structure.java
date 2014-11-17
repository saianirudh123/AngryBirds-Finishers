package ab.demo;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.Integer;
import java.lang.System;
import java.util.ArrayList;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
public class Structure {
    ArrayList<SubStructure> list;
    ArrayList<ABObject> objects;
    ArrayList<ABObject> pigs;
    ArrayList<Point> wpts;
    public Structure(ArrayList<ABObject> objs,ArrayList<ABObject> pigs) {
        this.objects = objs;
        this.pigs=pigs;
        this.wpts = new ArrayList<Point>();
        list = new ArrayList<SubStructure>();
        this.createSubStructures();
        /*for(int i=0;i<list.size();i++){
            SubStructure ss=list.get(i);
            for(int j=0;j<ss.obj.size();j++){
                System.out.println(ss.obj.get(j));
            }
            System.out.println("\n\n\n");
        }*/
    }
    public ArrayList<Point> getWeakPoints(SubStructure ss) {
        ArrayList<Point> w = new ArrayList<Point>();
        //required to be coded
        return w;
        //return wpts;
    }
    public void createSubStructures(){
        //error here!!!
        //System.out.println("Verify"+objects.size());
        for(int i=0;i<objects.size();i++){
            //System.out.println("C1");
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
                    //System.out.println("C5");
                    ABObject o1=ss1.obj.get(k);
                    //System.out.println(this.isconnected(o,o1));
                    //System.out.println("e");
                    if(this.isconnected(o,o1)){
                        con.add(j);
                        break;
                    }
                }
            }
            if(con.size()==0){
                SubStructure ss2=new SubStructure();
                ss2.add(o);
                list.add(ss2);
                continue;
            }
            //SubStructure temp=list.get(con.get(0));
            int tem=con.get(0);
            list.get(tem).add(o);
            for(int j=1;j<con.size();j++){
                SubStructure temp1=list.get(con.get(j));
                for(int k=0;k<temp1.obj.size();k++){
                    //System.out.println("C2");
                    list.get(con.get(0)).add(temp1.obj.get(k));
                }
            }
            for(int j=1;j<con.size();j++){
                //System.out.println("C3");
                int x=con.get(j);
                //System.out.println("C55");
                if(x>=list.size()){
                    continue;
                }
                list.remove(x);
                //System.out.println("C55e");
            }
        }
    }
     /*public void createSubStructures(){
         //error here!!!
         System.out.println("Verify"+objects.size());
         System.out.println("SS size"+list.size());
         for(int i=0;i<objects.size();i++) {
             ABObject o = objects.get(i);
             for (int j = 0; j < list.size(); j++) {
                 System.out.print(" ss" + j + ":" + list.get(j).obj.size());
             }
             System.out.println("\n");
             if (i == 0) {
                 SubStructure ss = new SubStructure();
                 ss.add(o);
                 list.add(ss);
                 System.out.println("Inintitalized");
                 continue;
             }
             ArrayList<Integer> con = new ArrayList<Integer>();
             for (int j = 0; j < list.size(); j++) {
                 SubStructure ss1 = list.get(j);
                 for (int k = 0; k < ss1.obj.size(); k++) {
                     ABObject o1 = ss1.obj.get(k);
                     //System.out.println(this.isconnected(o,o1));
                     //System.out.println("e");
                     if (this.isconnected(o, o1)) {
                         con.add(j);
                         System.out.println("Object #" + i + "is connected to" + "#" + j);
                         break;
                     }
                 }
             }
             if (con.size() == 0) {
                 System.out.println("new SS Object added to New ss");
                 SubStructure ss2 = new SubStructure();
                 ss2.add(o);
                 list.add(ss2);
                 continue;
             }
             list.get(con.get(0)).add(o);
             for (int j = 1; j < con.size(); j++) {
                 SubStructure temp1 = list.get(con.get(j));
                 for (int k = 0; k < temp1.obj.size(); k++) {
                     //System.out.println("Object # Added to ss"+list.get(con.get(0)).add(o));
                     System.out.println("E");
                     list.get(con.get(0)).add(temp1.obj.get(k));
                 }
             }
             for (int j = 1; j < con.size(); j++) {
                 int x=con.get(j);
                 list.remove(x);
                 System.out.println("removed" + con.get(j));
                 //ArrayList<SubStructure> ass=new ArrayList<SubStructure>();
             }
             /*SubStructure sss=new SubStructure();
             sss.add(o);
             for(int j=0;j<con.size();j++){
                 for(int k=0;k<list.get(con.get(j)).obj.size();k++){
                     sss.add(list.get(con.get(j)).obj.get(k));
                 }
             }
             for(int j=0;j<con.size();j++){
                 list.remove(con.get(j));
             }
             list.add(sss);
         }
         for(int i=0;i<list.size();i++){
             SubStructure ss=list.get(i);
             for(int j=0;j<ss.obj.size();j++){
                 System.out.println(ss.obj.get(j));
             }
             System.out.println("ss #"+i+list.get(i).obj.size());
             //System.out.println("\n\n\n");
         }*/
/*         }
     }*/
    public  boolean isconnected(ABObject o, ABObject o1) {
        //Wrong Some where int this code*/
        //Structure sss=new Structure(new ArrayList<ABObject>());
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
        /*int xx, yy;
        for (int i = 0; i < 4; i++) {
            xx = Pts2[i].x;
            yy = Pts2[i].y;
            for (int j = 0; j < 4; j++) {
                if ((ltop1.x - rtop1.x) * (yy - rtop1.y) == (ltop1.y - rtop1.y) * (xx - rtop1.x) ||
                        (ltop1.x - lbottom1.x) * (yy - lbottom1.y) == (ltop1.y - lbottom1.y) * (xx - lbottom1.x) ||
                        (rbottom1.x - lbottom1.x) * (yy - lbottom1.y) == (rbottom1.y - lbottom1.y) * (xx - lbottom1.x) ||
                        (rbottom1.x - rtop1.x) * (yy - rtop1.y) == (rbottom1.y - rtop1.y) * (xx - rtop1.x)) {
                    return true;
                }
            }
        }*/
        if (Math.abs(Pts2[lb].y - Pts1[lt].y) <= 10) {
            /*ABObject temp = o;
            o = o1;
            o1 = temp;
            Point pt[]=Pts1;
            Pts1=Pts2;
            Pts2=pt;*/


            if (this.isconnected1(o1, o)) return true;
            else return false;


            //System.out.println("Swapped");
        }
        if (Math.abs(Pts1[lb].y - Pts2[lt].y) <= 10) {
            //System.out.println("May be connected");
            /*try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (Pts2[lt].x <= Pts1[rb].x && Pts2[lt].x >= Pts1[lb].x) {
                return true;
            }
            if (Pts2[rt].x <= Pts1[rb].x && Pts2[rt].x >= Pts1[lb].x) {
                return true;
            }
            if (Pts2[lt].x <= Pts1[lb].x && Pts2[rt].x >= Pts1[lb].x) {
                return true;
            }
            if (Pts2[lt].x <= Pts1[rb].x && Pts2[rt].x >= Pts1[rb].x) {
                return true;
            }
        }
        if (Math.abs(Pts2[rt].x - Pts1[lt].x) <= 10) {
            /*ABObject temp = o;
            o = o1;
            o1 = temp;
            Point pt[]=Pts1;
            Pts1=Pts2;
            Pts2=pt;*/


            if (this.isconnected1(o1, o)) return true;
            else return false;


            //System.out.println("Swapped");
        }
        if (Math.abs(Pts1[rb].x - Pts2[lt].x) <= 10) {
            //System.out.println("May be connected");
            /*try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (Pts2[lt].y >= Pts1[rt].y && Pts2[lt].y <= Pts1[rb].y) {
                return true;
            }
            if (Pts2[lb].y <= Pts1[rb].y && Pts2[lb].y >= Pts1[rt].y) {
                return true;
            }
            if (Pts2[lb].y >= Pts1[rt].y && Pts2[lt].y <= Pts1[rt].y) {
                return true;
            }
            if (Pts2[lb].y >= Pts1[rb].y && Pts2[lt].y <= Pts1[rb].y) {
                return true;
            }
            if (Pts2[lt].x < Pts1[rb].x && Pts2[rt].x > Pts1[rb].x) {
                return true;
            }
        }
        /*try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //System.out.println("----dis-connected");
        return false;
    }
    public  boolean isconnected1(ABObject o, ABObject o1) {
        //Wrong Some where int this code*/
        //Structure sss=new Structure(new ArrayList<ABObject>());
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
        /*int xx, yy;
        for (int i = 0; i < 4; i++) {
            xx = Pts2[i].x;
            yy = Pts2[i].y;
            for (int j = 0; j < 4; j++) {
                if ((ltop1.x - rtop1.x) * (yy - rtop1.y) == (ltop1.y - rtop1.y) * (xx - rtop1.x) ||
                        (ltop1.x - lbottom1.x) * (yy - lbottom1.y) == (ltop1.y - lbottom1.y) * (xx - lbottom1.x) ||
                        (rbottom1.x - lbottom1.x) * (yy - lbottom1.y) == (rbottom1.y - lbottom1.y) * (xx - lbottom1.x) ||
                        (rbottom1.x - rtop1.x) * (yy - rtop1.y) == (rbottom1.y - rtop1.y) * (xx - rtop1.x)) {
                    return true;
                }
            }
        }*/
        if (Math.abs(Pts2[lb].y - Pts1[lt].y) <= 10) {
            /*ABObject temp = o;
            o = o1;
            o1 = temp;
            Point pt[]=Pts1;
            Pts1=Pts2;
            Pts2=pt;*/


            /*if (this.isconnected1(o1, o)) return true;
            else return false;*/


            //System.out.println("Swapped");
        }
        if (Math.abs(Pts1[lb].y - Pts2[lt].y) <= 10) {
            //System.out.println("May be connected");
            /*try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (Pts2[lt].x <= Pts1[rb].x && Pts2[lt].x >= Pts1[lb].x) {
                return true;
            }
            if (Pts2[rt].x <= Pts1[rb].x && Pts2[rt].x >= Pts1[lb].x) {
                return true;
            }
            if (Pts2[lt].x <= Pts1[lb].x && Pts2[rt].x >= Pts1[lb].x) {
                return true;
            }
            if (Pts2[lt].x <= Pts1[rb].x && Pts2[rt].x >= Pts1[rb].x) {
                return true;
            }
        }
        if (Math.abs(Pts2[rt].x - Pts1[lt].x) <= 10) {
            /*ABObject temp = o;
            o = o1;
            o1 = temp;
            Point pt[]=Pts1;
            Pts1=Pts2;
            Pts2=pt;*/


            /*if (this.isconnected1(o1, o)) return true;
            else return false;*/


            //System.out.println("Swapped");
        }
        if (Math.abs(Pts1[rb].x - Pts2[lt].x) <= 10) {
            //System.out.println("May be connected");
            /*try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (Pts2[lt].y >= Pts1[rt].y && Pts2[lt].y <= Pts1[rb].y) {
                return true;
            }
            if (Pts2[lb].y <= Pts1[rb].y && Pts2[lb].y >= Pts1[rt].y) {
                return true;
            }
            if (Pts2[lb].y >= Pts1[rt].y && Pts2[lt].y <= Pts1[rt].y) {
                return true;
            }
            if (Pts2[lb].y >= Pts1[rb].y && Pts2[lt].y <= Pts1[rb].y) {
                return true;
            }
            if (Pts2[lt].x < Pts1[rb].x && Pts2[rt].x > Pts1[rb].x) {
                return true;
            }
        }
        /*try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //System.out.println("----dis-connected");
        return false;
    }
}