import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;


public class Brick extends GCompound {
	public Brick (double wid, double hgt, int scr, int rewType) {
		GRect brick = new GRect (wid,hgt);
		score = scr;
		rewardType= rewType;
		brick.setFilled(true);
		switch(score) {
			case SCORE_RED: brick.setColor(Color.RED);break;
			case SCORE_ORANGE: brick.setColor(Color.ORANGE);break;
			case SCORE_YELLOW: brick.setColor(Color.YELLOW);break;
			case SCORE_GREEN: brick.setColor(Color.GREEN);break;
			case SCORE_CYAN: brick.setColor(Color.CYAN);break;
		}
		add(brick,0,0);
		if (rewardType!=REWARD_NULL){
			mark = new GLabel (rewardType>=0?"?":"!");
			mark.setFont("Helvetica-"+getRightFontSize(mark,brick.getWidth(),brick.getHeight()));
			mark.setColor(Color.WHITE);
			add(mark,(brick.getWidth()-mark.getWidth())/2,(brick.getHeight()+mark.getAscent())/2);
		}
	}
	
	//use -1(or 0) as width or height to signal that width or height is unrestricted
	public static int getRightFontSize (GLabel label, double width, double height) {
		if (width<=0 && height<=0) return -1;
		for (int i=2;;i++) {
			label.setFont("Helvetica-"+i);
			if (width>0) {
				if (label.getBounds().getWidth()>width) return i-1;
			}
			if (height>0) {
				if (label.getBounds().getHeight()>height) return i-1;
			}
		}
	}
	
	public int getScore() {
		return score;
	}
	
	public int getRewardType() {
		return rewardType;
	}
	
	public boolean isPostiveReward() {
		return rewardType>=0;
	}
	
	private GLabel mark;
	private int score;
	private int rewardType;
	
	public static final int REWARD_NULL=-100, REWARD_LENGTHENING=0, REWARD_SHOOTER=1, REWARD_TRIPLE_BALLS=2,
											REWARD_SHORTENING=-1, REWARD_DISARM=-2,
											REWARD_LOWER_BOUND=-2,REWARD_HIGHER_BOUND=2;
	public static final int SCORE_RED=50, SCORE_ORANGE=40, SCORE_YELLOW=30, SCORE_GREEN=20, SCORE_CYAN=10;
}
