import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Reward extends GCompound {
//Reward: origin is set to be the middlepoint of the top line. so that the reward location can be conveniently set at the middle of the brick broken.
	public Reward(Brick brick) {
		rewardType = brick.getRewardType();
		if (rewardType!=REWARD_NULL) {
			GRect outline = new GRect(brick.getWidth(),brick.getWidth());
			outline.setColor((brick.isPostiveReward())?(Color.YELLOW):(Color.RED));
			outline.setFilled(true);
			add(outline,-outline.getWidth()/2,0);
			GLabel label = new GLabel("");
			if (rewardType==REWARD_LENGTHENING)	label.setLabel("===");
			else if (rewardType == REWARD_SHOOTER) label.setLabel("GUN");
			else if (rewardType == REWARD_TRIPLE_BALLS) label.setLabel("OOO");
			else if (rewardType == REWARD_SHORTENING) label.setLabel("=");
			else if (rewardType == REWARD_DISARM) label.setLabel("X");
			label.setFont("Helvetica-"+Brick.getRightFontSize(label, outline.getWidth(), outline.getHeight()));
			add(label,-label.getWidth()/2,(outline.getHeight()+label.getAscent())/2);
			setLocation(brick.getX()+brick.getWidth()/2,brick.getY()+brick.getHeight());
		}
	}
	
	public int getRewardType() {
		return rewardType;
	}
	

private int rewardType;

public static final int REWARD_NULL=-100, REWARD_LENGTHENING=0, REWARD_SHOOTER=1, REWARD_TRIPLE_BALLS=2,
REWARD_SHORTENING=-1, REWARD_DISARM=-2,
REWARD_LOWER_BOUND=-2,REWARD_HIGHER_BOUND=2;

}