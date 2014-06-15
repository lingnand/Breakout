/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;
	
/** Color of the paddle */
	private static final Color PADDLE_COLOR = Color.BLACK;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;
	
/** Color of the ball */
	private static final Color BALL_COLOR = Color.GRAY;
	
/** ball velocity in x and y */
	private static final double BALL_VEL_X_LB = 5, BALL_VEL_X_HB = 10, BALL_VEL_Y = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	
	
/** Game Settings */
	//no. of lives for each game run
	private static final int LIFE_LIMIT = 5;
	//time delayed for each cycle, in milliseconds...Fps = 1000/DELAY
	private static final double DELAY = 50;
	//time delayed for each cycle of color change in label "You win!"
	private static final double LABEL_FLASH_DELAY = 2000;
	//probability of reward
	private static final double REWARD_PROBABILITY=0.3;
	//velocity of the reward coming down
	private static final double REWARD_VEL = BALL_VEL_Y/3;
	//bullet specifications
	private static final double BULLET_WIDTH = 2, BULLET_HEIGHT = 3, BULLET_VEL = BALL_VEL_Y*1.5;
	//statusBar flash time in milliseconds
	private static final long STATUSBAR_FLASH_TIME = 4000;
	
	public static void main(String[] args) {
		new Breakout().start(args);
	}

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		addMouseListeners();
		setup();
		while (true) {
			if (gameOver()) {
				win = false;
				break;
			}
			if (gameWin()) {
				win = true;
				break;
			}
			checkLifeOver();
			moveBall();
			movePaddle();
			moveReward();
			checkBallCollision();
			checkRewardCollision();
			rewardEffect();
			statusBarEffect();
			pause(DELAY);
		}
		printNotice();
	}
	
	private void setup() {
		iniBricks();
		iniPaddle();
		addBall();
		iniLabels();
	}
	
	private void iniBricks() {
		Brick[][] array = new Brick[NBRICK_ROWS][NBRICKS_PER_ROW];
		brickCount = NBRICK_ROWS*NBRICKS_PER_ROW;
		for (int i=0; i<array.length; i++) {
			for (int j=0; j<array[i].length; j++) {
				array[i][j] = new Brick(BRICK_WIDTH,BRICK_HEIGHT,
						50-i/(NBRICK_ROWS/5)*10, 
						rgen.nextBoolean(REWARD_PROBABILITY)?
								(rgen.nextInt(Brick.REWARD_LOWER_BOUND, Brick.REWARD_HIGHER_BOUND)):(Brick.REWARD_NULL));
				array[i][j].setLocation((getWidth()-NBRICKS_PER_ROW*BRICK_WIDTH-(NBRICKS_PER_ROW-1)*BRICK_SEP)/2+j*(BRICK_WIDTH+BRICK_SEP),
										BRICK_Y_OFFSET+i*(BRICK_HEIGHT+BRICK_SEP));
				add(array[i][j]);
			}
		}
		//old method, just for reference
		/*for (int i=0; i<NBRICK_ROWS; i++) {
			for (int j=0; j<NBRICKS_PER_ROW; j++) {
				GRect brick = new GRect(BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				switch (i/(NBRICK_ROWS/5)) {
				case 0: brick.setColor(Color.RED);break;
				case 1: brick.setColor(Color.ORANGE);break;
				case 2: brick.setColor(Color.YELLOW);break;
				case 3: brick.setColor(Color.GREEN);break;
				case 4: brick.setColor(Color.CYAN);break;
				}
				add(brick,
					(getWidth()-NBRICKS_PER_ROW*BRICK_WIDTH-(NBRICKS_PER_ROW-1)*BRICK_SEP)/2+j*(BRICK_WIDTH+BRICK_SEP),
					BRICK_Y_OFFSET+i*(BRICK_HEIGHT+BRICK_SEP));
			}
		}
		brickCount = NBRICK_ROWS*NBRICKS_PER_ROW;
		*/
	}
			
	private void iniPaddle() {
		paddle = new GRect ((getWidth()-PADDLE_WIDTH)/2,
				getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET,
				PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(PADDLE_COLOR);
		add(paddle);
	}
	
	private void addBall() {
		GOval ball = new GOval (paddle.getX()+paddle.getWidth()/2-BALL_RADIUS,paddle.getY()-BALL_RADIUS*2,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(BALL_COLOR);
		ballArray.add(ball);
		add(ball);
		ballVel.add(new double[2]);
		setBallVel(ballVel.size()-1,0,0);
	}
	
	private void iniLabels() {
		lifeLabel = new GLabel ("Lives: "+life);
		scoreLabel = new GLabel ("Score: "+score);
		statusBar = new GLabel ("");
		lifeLabel.setFont("Helvetica-10");
		scoreLabel.setFont("Helvetica-10");
		statusBar.setFont("Helvetica-10");
		add(lifeLabel,0,getHeight()-PADDLE_Y_OFFSET/2+lifeLabel.getAscent()/2);
		add(scoreLabel,getWidth()-scoreLabel.getWidth(),getHeight()-PADDLE_Y_OFFSET/2+scoreLabel.getAscent()/2);
	}
	
	private void refreshLabels() {
		lifeLabel.setLabel("Lives: "+life);
		scoreLabel.setLabel("Score: "+score);
		lifeLabel.setLocation(0,getHeight()-PADDLE_Y_OFFSET/2+lifeLabel.getAscent()/2);
		scoreLabel.setLocation(getWidth()-scoreLabel.getWidth(),getHeight()-PADDLE_Y_OFFSET/2+scoreLabel.getAscent()/2);
	}
	
	private boolean gameOver() {
		return life==0;
	}
	
	private boolean gameWin() {
		return brickCount==0;
	}
	
	private void checkLifeOver() {
		for (int i=0; i<ballArray.size();) {
			if (((GOval)ballArray.get(i)).getY()>getHeight()-((GOval)ballArray.get(i)).getHeight()) {
				remove((GOval)ballArray.get(i));
				ballArray.remove(i);
				ballVel.remove(i);
			}
			else i++;
		}
		if (ballArray.size()==0) {
			life--;
			refreshLabels();
			restart=true;
			addBall();
			setRewardEffects(false);
		}
	}
	
	private void moveBall() {
		if (restart) ((GOval)ballArray.get(0)).setLocation(mouseX-BALL_RADIUS,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET-BALL_RADIUS*2);
		else {
			for (int i=0; i<ballArray.size(); i++) {
				((GOval)ballArray.get(i)).move(((double[])ballVel.get(i))[0], ((double[])ballVel.get(i))[1]);
			}
		}
	}
	
	public void mouseClicked (MouseEvent e) {
		if (restart) {
			restart=false;
			iniBallVel (0);
		}
		if (shooterEnabled) {
			GOval bullet = new GOval (BULLET_WIDTH,BULLET_HEIGHT);
			bullet.setLocation(mouseX-bullet.getWidth()/2,paddle.getY());
			bullet.setFilled(true);
			bulletArray.add(bullet);
			add(bullet);
		}
	}
	
	private void iniBallVel (int index) {
		setBallVel(index,rgen.nextDouble(BALL_VEL_X_LB, BALL_VEL_X_HB)*((rgen.nextBoolean())?(1):(-1)),-BALL_VEL_Y);
	}
	
	private void setBallVel (int index, double vx, double vy) {
		((double[])ballVel.get(index))[0] = vx;
		((double[])ballVel.get(index))[1] = vy;
	}
	
	private void movePaddle() {
		paddle.setLocation(mouseX-paddle.getWidth()/2, getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
	}
	
	public void mouseMoved (MouseEvent e) {
		mouseX=e.getX();
	}
	
	private void checkBallCollision() {
		for (int i=0; i<ballArray.size(); i++) {
			GOval ball = (GOval)ballArray.get(i);
			double xBall = ball.getX(), yBall = ball.getY(),
				width = ball.getWidth();
			GPoint up = new GPoint (xBall+width/2,yBall-1);
			GPoint down = new GPoint (xBall+width/2,yBall+width+1);
			GPoint left = new GPoint (xBall-1,yBall+width/2);
			GPoint right = new GPoint (xBall+width+1,yBall+width/2);
			//check walls on the left and right
			if (xBall<0 || xBall>getWidth()-width) {
				((double[])ballVel.get(i))[0] *= -1;
				ball.move((xBall<0?(-xBall):(getWidth()-xBall-width)),0);
				continue;
			}
			//check walls on top
			if (yBall<0) {
				((double[])ballVel.get(i))[1] *= -1;
				ball.move(0,-yBall);
				continue;
			}
			//check for gobj
			char axis='-';
			GObject gobjCollided = getElementAt(up);
			if (gobjCollided!=null) axis='y';
			else {
				gobjCollided = getElementAt(down);
				if (gobjCollided!=null) axis='y';
				else {
					gobjCollided = getElementAt(left);
					if (gobjCollided!=null) axis='x';
					else {
						gobjCollided = getElementAt(right);
						if (gobjCollided!=null) axis='x';
					}
				}
			}
			if (gobjCollided==null || gobjCollided==scoreLabel || gobjCollided==lifeLabel || gobjCollided==statusBar || gobjCollided instanceof Reward) continue;
			else {
				if (gobjCollided instanceof Brick) {
					brickBreak((Brick)gobjCollided);
				}
				switch(axis){
				case'x': ((double[])ballVel.get(i))[0] *= -1;break;
				case'y': ((double[])ballVel.get(i))[1] *= -1;;break;
				default: break;
				}
				ball.move(((double[])ballVel.get(i))[0], ((double[])ballVel.get(i))[1]);
			}
		}
	}	
	
	private void brickBreak(Brick brick) {
		remove(brick);
		brickCount--;
		score+=brick.getScore();
		refreshLabels();
		createReward(brick);
	}
				
	private void printNotice() {
		for (int i=0; i<ballArray.size(); i++) {
			remove((GOval)ballArray.get(i));
		}
		remove(paddle);
		GLabel notice = new GLabel (win?"You win!":"You lose!");
		notice.setFont("Helvetica-36");
		add(notice, (getWidth()-notice.getWidth())/2, (getHeight()+notice.getAscent())/2);
		if (!win) return;
		while (true) {
			notice.setColor(rgen.nextColor());
			pause(LABEL_FLASH_DELAY);
		}
	}
	
	private void createReward(Brick brick) {
		if (brick.getRewardType()!=Brick.REWARD_NULL) {
			Reward reward = new Reward(brick);
			rewardArray.add(reward);
			add(reward);
		}
	}
	
	private void moveReward() {
		for (int i=0; i<rewardArray.size(); i++) {
			((Reward)rewardArray.get(i)).move(0,REWARD_VEL);
		}
	}
	
	private void checkRewardCollision() {
		for (int i=0; i<rewardArray.size(); i++) {
			Reward reward = (Reward)rewardArray.get(i);
			GPoint left = new GPoint (reward.getX()-reward.getWidth()/2,reward.getY()+reward.getHeight());
			GPoint middle = new GPoint (reward.getX(),reward.getY()+reward.getHeight());
			GPoint right = new GPoint (reward.getX()+reward.getWidth()/2,reward.getY()+reward.getHeight());
			GObject fall = getElementAt(left);
			if (fall==null) {
				fall = getElementAt(middle);
				if (fall==null) {
					fall = getElementAt(right);
				}
			}
			if (fall==paddle) {
				remove(reward);
				rewardArray.remove(i);
				int type = reward.getRewardType();
				if (type == Reward.REWARD_LENGTHENING) {
					paddle.setSize(paddle.getWidth()*1.5,paddle.getHeight()); 
					statusBar.setLabel("Lengthening!");
				}
				else if (type == Reward.REWARD_SHORTENING) {
					paddle.setSize(paddle.getWidth()/1.5,paddle.getHeight());
					statusBar.setLabel("Shortening!");
				}
				else if (type == Reward.REWARD_SHOOTER) {
					shooterEnabled = true;
					statusBar.setLabel("Shooter!");
				}
				else if (type == Reward.REWARD_DISARM) 	{
					shooterEnabled = false;
					statusBar.setLabel("ShooterOff!");
				}
				else if (type == Reward.REWARD_TRIPLE_BALLS) {
					for (int j=0; j<2; j++) {
						addBall();
						iniBallVel(ballArray.size()-1);
					}
					statusBar.setLabel("Triple balls!");
				}
				add(statusBar,(getWidth()-statusBar.getWidth())/2,getHeight()-PADDLE_Y_OFFSET/2+statusBar.getAscent()/2);
				statusBarLabelStartTime = new Long(System.currentTimeMillis());
			}
		}
	}
	
	private void setRewardEffects (boolean on) {
		shooterEnabled = on;
		if (!on) {
			paddle.setSize(PADDLE_WIDTH, PADDLE_HEIGHT);
		}
	}
	
	private void rewardEffect() {
		for (int i=0; i<bulletArray.size();) {
			((GOval)bulletArray.get(i)).move(0, -BULLET_VEL);
			GObject gobjshot = getElementAt(((GOval)bulletArray.get(i)).getLocation());
			if (gobjshot instanceof Brick) {
				brickBreak((Brick)gobjshot);
				remove((GOval)bulletArray.get(i));
				bulletArray.remove(i);
			}
			else if (((GOval)bulletArray.get(i)).getY()<-BULLET_HEIGHT) {
				remove((GOval)bulletArray.get(i));
				bulletArray.remove(i);
			}
			else i++;
		}
	}
	
	private void statusBarEffect() {
		if (statusBarLabelStartTime!=null) {
			if (System.currentTimeMillis()-statusBarLabelStartTime.longValue()>=STATUSBAR_FLASH_TIME){
				remove(statusBar);
				statusBarLabelStartTime=null;
			}
		}
	}
	
	private int brickCount;
	private double mouseX;
	private int life=LIFE_LIMIT, score=0;
	private boolean restart=true, win;
	private boolean shooterEnabled=false;
	private Long statusBarLabelStartTime=null;

	private GLabel lifeLabel, scoreLabel, statusBar;
	private GRect paddle;
	private ArrayList ballArray=new ArrayList(), ballVel=new ArrayList(), rewardArray=new ArrayList(),bulletArray=new ArrayList();
	private RandomGenerator rgen = new RandomGenerator();
					
				
				
}
