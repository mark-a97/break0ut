import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.application.Platform;

// The model represents all the actual content and functionality of the app
// For Breakout, it manages all the game objects that the View needs
// (the bat, ball, bricks, and the score), provides methods to allow the Controller
// to move the bat (and a couple of other fucntions - change the speed or stop 
// the game), and runs a background process (a 'thread') that moves the ball 
// every 20 milliseconds and checks for collisions 


public class Model 
{
    // First,a collection of useful values for calculating sizes and layouts etc.

    public int B              = 6;      // Border round the edge of the panel
    public int M              = 40;     // Height of menu bar space at the top
    public int B_AREA 		  = -50;
    
    public int BALL_SIZE      = 30;     // Ball side
    public int BRICK_WIDTH    = 50;     // Brick size
    public int BRICK_HEIGHT   = 30;
    public int MAX_BRICKS = 60; // - number of bricks
    public int brickCounter = 0;

    public int BAT_MOVE       = 5;      // Distance to move bat on each keypress
    public int BALL_MOVE      = 3;      // Units to move the ball on each step
    
    
    public int ballCount = 0;
    

    public boolean messageShown = false;
    public int playedSound = 0;


    public int HIT_BRICK      = 50;     // Score for hitting a brick
    public int HIT_BOTTOM     = -200;   // Score (penalty) for hitting the bottom of the screen

    public boolean gotUpgrade = false;
    public int upgradeCount = 0;
    public int upgrade_WIDTH = 50;
    public int upgrade_HEIGHT = 50;
    public int upgrade_MOVE = 1;
    
    public int starWave = 0;
    public int star_WIDTH = 25;
    public int star_HEIGHT = 25;
    public int star_MOVE = 1;
    
    public boolean upgradeOne = false;
    public boolean upgradeTwo = false;
    public boolean starOne = false;
    public boolean starTwo = false;
    public boolean laserUpgrade = false;

    public int currentGameStatus = 0;
    public boolean levelOne = true;
    public boolean levelTwo = false;
    
    public int BAT_WIDTH = 50; //making a bat_width for level two so it doesn't interfere with the brick width
    					
    // The other parts of the model-view-controller setup
    View view;
    Controller controller;

    // The game 'model' - these represent the state of the game
    // and are used by the View to display it
    public ArrayList<GameObj> upgrades;
    public ArrayList<GameObj> balls;                // The ball array
    public ArrayList<GameObj> bricks;   // The bricks
    public ArrayList<GameObj> stars;
    public GameObj bat;                 // The bat
    public int score = 0;               // The score
    public int playerLives = 5;			// The lives

    // variables that control the game 
    public boolean gameRunning = true;  // Set false to stop the game
    public boolean fast = false;        // Set true to make the ball go faster
    public int fastPaced = 0;
    public int batPos = 30;	// position of the bat
    
    // initialisation parameters for the model
    public int width;                   // Width of game
    public int height;                  // Height of game


    // CONSTRUCTOR - needs to know how big the window will be
    public Model( int w, int h )
    {
        Debug.trace("Model::<constructor>");  
        width = w; 
        height = h;
        initialiseGame();
    }

    // Initialise the game - reset the score and create the game objects 
    /**Initialises the game and sets the game objects on the canvas
     * 
     */
    public void initialiseGame()
    {       	
    	if(levelOne == true) {
        
	        int WALL_ONE_ORANGE = 210;                     // how far down the screen the wall starts        
	        int WALL_ONE_BLUE = 255;
	        int WALL_TWO_BLUE = 165;       
	        int WALL_ONE_RED = 345;
	        int WALL_TWO_RED = 300;
	        
	        score = 0;
	        playerLives = 5;
	        
	        bat    = new GameObj(width/2, height - BRICK_HEIGHT*3/2, BRICK_WIDTH*3, BRICK_HEIGHT/4, Color.GRAY);
	        bricks = new ArrayList<>();
	        balls = new ArrayList<>();
	        upgrades = new ArrayList<>();
	        stars = new ArrayList<>();
	        
	        // *[1]******************************************************[1]*
	        // * Fill in code to add the bricks to the arrayList            *
	        // **************************************************************
	        
	        GameObj ball_ONE   = new GameObj(width/2, height/2, BALL_SIZE, BALL_SIZE, Color.RED );  // adding more balls to the by using an array
	        GameObj ball_TWO   = new GameObj(width/2, height/2, BALL_SIZE, BALL_SIZE, Color.BLUE ); // like the bricks.
	        
	        balls.add(ball_ONE);  
	        balls.add(ball_TWO);
	        
	        ball_TWO.visible = false;
	
	        int NUM_BRICKS = width/BRICK_WIDTH;     // how many bricks fit on screen
	        for (int i=0; i < NUM_BRICKS; i++) {

	            GameObj brick_ONE = new GameObj(BRICK_WIDTH*i, WALL_ONE_ORANGE, BRICK_WIDTH-1, BRICK_HEIGHT, Color.ORANGE);
	            GameObj brick_TWO = new GameObj(BRICK_WIDTH*i, WALL_ONE_BLUE, BRICK_WIDTH-1, BRICK_HEIGHT, Color.BLUE);
	            GameObj brick_FIVE = new GameObj(BRICK_WIDTH*i, WALL_TWO_BLUE, BRICK_WIDTH-1, BRICK_HEIGHT, Color.BLUE);
	            GameObj brick_THREE = new GameObj(BRICK_WIDTH*i, WALL_ONE_RED, BRICK_WIDTH-1, BRICK_HEIGHT, Color.PINK);
	            GameObj brick_FOUR = new GameObj(BRICK_WIDTH*i, WALL_TWO_RED, BRICK_WIDTH-1, BRICK_HEIGHT, Color.RED);
	
	            bricks.add(brick_ONE);      // add this brick to the list of bricks
	            bricks.add(brick_TWO); 
	            bricks.add(brick_THREE);
	            bricks.add(brick_FOUR);
	            bricks.add(brick_FIVE);   
	
	            brick_ONE.brickHealth = true;
	        	brick_TWO.brickHealth = false;   // - Setting this specific row of bricks health higher than the others
	        	brick_THREE.brickHealth = false; // - so that it will take more hits to destroy the blocks.
	        	brick_FOUR.brickHealth = true;
	        	brick_FIVE.brickHealth = false;	
	        }
        }
    	else if(levelTwo == true){
            int WALL_POS = 300;
            int BRICK_WIDTH = 30; //changing the brick width for level 2
           
                        
            bat    = new GameObj(width/2, height - BRICK_HEIGHT*3/2, BAT_WIDTH*3, BRICK_HEIGHT/4, Color.GRAY);
            
            bricks = new ArrayList<>();
            balls = new ArrayList<>();
            upgrades = new ArrayList<>();
            stars = new ArrayList<>();

            int NUM_BRICKS = width/BRICK_WIDTH;     
            for (int i=0; i < NUM_BRICKS; i++) {

                GameObj brick_ONE = new GameObj(BRICK_WIDTH*i, WALL_POS+26, BRICK_WIDTH-1, BRICK_HEIGHT-5, Color.AQUA);
                GameObj brick_TWO = new GameObj(BRICK_WIDTH*i, WALL_POS+52, BRICK_WIDTH-1, BRICK_HEIGHT-5, Color.BLUEVIOLET);
                
                GameObj brick_THREE = new GameObj(BRICK_WIDTH*i, WALL_POS-26, BRICK_WIDTH-1, BRICK_HEIGHT-5, Color.LIME);
                GameObj brick_FOUR = new GameObj(BRICK_WIDTH*i, WALL_POS-26*2, BRICK_WIDTH-1, BRICK_HEIGHT-5, Color.BROWN);
                
                GameObj brick_FIVE = new GameObj(BRICK_WIDTH*i, WALL_POS-26*4, BRICK_WIDTH-1, BRICK_HEIGHT-5, Color.CADETBLUE);
                GameObj brick_SIX = new GameObj(BRICK_WIDTH*i, WALL_POS-26*5, BRICK_WIDTH-1, BRICK_HEIGHT-5, Color.LIGHTSALMON);
               
                // - 26 space above/below
                // - 1 space inbetween
                
                bricks.add(brick_ONE); 
                bricks.add(brick_TWO);
                bricks.add(brick_THREE);
                bricks.add(brick_FOUR); //adding the bricks to the game
                bricks.add(brick_FIVE);
                bricks.add(brick_SIX);
            }
    	}
    }

    // Animating the game
    // The game is animated by using a 'thread'. Threads allow the program to do 
    // two (or more) things at the same time. In this case the main program is
    // doing the usual thing (View waits for input, sends it to Controller,
    // Controller sends to Model, Model updates), but a second thread runs in 
    // a loop, updating the position of the ball, checking if it hits anything
    // (and changing direction if it does) and then telling the View the Model 
    // changed.
    
    // When we use more than one thread, we have to take care that they don't
    // interfere with each other (for example, one thread changing the value of 
    // a variable at the same time the other is reading it). We do this by 
    // SYNCHRONIZING methods. For any object, only one synchronized method can
    // be running at a time - if another thread tries to run the same or another
    // synchronized method on the same object, it will stop and wait for the
    // first one to finish.
    
    
    
    // Start the animation thread
    public void startGame()
    {
        Thread t = new Thread( this::runGame );     // create a thread runnng the runGame method
        t.setDaemon(true);                          // Tell system this thread can die when it finishes
        t.start();                                  // Start the thread running  	 
    }
    
    // The main animation loop
    

    /** 
     *  Runs the actual game and has a continuous while loop which runs through other methods until
     *  the game is finished
     */
    public void runGame()
    {
        try
        {
            // set gameRunning true - game will stop if it is set false (eg from main thread)
            setGameRunning(true);
            while (getGameRunning())
            {	
            	visible();							// reveal the second ball
            	checkScore();
               	noLives();							// run no lives method
                updateGame();                        // update the game state
                modelChanged();                      // Model changed - refresh screen
                Thread.sleep( getFast() ? 10 : 20 ); // wait a few milliseconds
                fastPaceSound();
            }
        } catch (Exception e) { 
            Debug.error("Model::runAsSeparateThread error: " + e.getMessage() );
        }
    }
    
    /**
     * When the score reaches over 500 the second ball is released
     */
    public synchronized void visible() {

    	if(levelOne == true) {
	    	if(score >= 500) {
	    		for(GameObj ball: balls) {	//checks the score, if it's over 500, make the ball visible
	    			ball.visible = true;
	    		}
	    	}    	
    	}   	
    }
 
    /**
     * Plays a specific sound from the game folder which is named fastSpeed.wav
     * but only if the fastPaced variable is set to 2
     */
    public synchronized void fastPaceSound() {  	
    	if(fastPaced == 2) {
    		File fastSpeed = new File("fastSpeed.wav"); //check whether the int fastPaced is set to 1
    		playMusic(fastSpeed);						//if it is, play the music
    		fastPaced = 1;								// sets fastPaced to 2 so that the sound isn't continuously repeated
    	}   	
    }

    /**
     * Checks the score and whenever it reaches a certain point, the game will link to the fastPaceSound method
     * and play the wav file 
     * Also checks that when levelTwo is finished, it will give the user a message box along with a victory sound file
     */
    public synchronized void checkScore()
    {
    	if(levelOne == true) {
    		if(score >= 500 && playedSound == 0) {
        		BALL_MOVE = 4;
        		fastPaced = 2;
        		playedSound = 1;
        		
    		}
    		if(score >= 2000 && playedSound == 1){
        		BALL_MOVE = 5;
            	fastPaced = 2;
            	playedSound = 2;
    		}
    		else if(score >= 6000 && playedSound == 2) {
        		BALL_MOVE = 6;
            	fastPaced = 2;
            	playedSound = 3;
    		}
    		
	    	if(MAX_BRICKS == 0) {	    		
	    		wonGame();
	    		gameRunning = false;
	    	}
    	}
    	else if(levelTwo == true) {
	    	if(MAX_BRICKS == 0) {	   //checks to see whether all bricks have been destroyed, when they have
	    		wonGame();			//play this sound method and stop the game
	    		gameRunning = false;
	    		JOptionPane.showMessageDialog(null, "Congratulations, you have completed the game!"); //Show message box
	    	}
    	}
    }
    
    /**
     * Method which enables sound to be played
     * @param Sound two parameters are required (the file, and the sound).
     */
	public synchronized void playMusic(File Sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Sound));	// method to play sound
			clip.start();
		}
		catch(Exception e) {
			Debug.error("Music Error: " + e.getMessage()); //try and catch, if it doesn't work, display a debug error with
		}												   // the reason it failed	
	}
	
	public synchronized void gameOver() {
		File gameOver = new File("gameOver.wav");
		playMusic(gameOver);
	}
	
	public synchronized void wonGame() {
    	File wonGame = new File("wonGame.wav"); 
    	playMusic(wonGame);						
	}
	
	public synchronized void gotStar() {
		File gotStar = new File("gotStar.wav");
		playMusic(gotStar);
	}
	
	public synchronized void upgradeSound() {
		File collectedUpgrade = new File("upgrade.wav");
		playMusic(collectedUpgrade);
	}
    
	public synchronized void downgradeSound() {
		File gotDowngrade = new File("downgrade.wav");
		playMusic(gotDowngrade);
	}
	
	public synchronized void ballHitBrick() {
		File ballHitBrick = new File("hitBrick.wav");
		playMusic(ballHitBrick);
	}
	
	public synchronized void hitBottom() {
		File ballHitBottom = new File("hitBottom.wav"); // plays a sound clip when the player has no lives left
		playMusic(ballHitBottom);
	}

	public synchronized void noLives()
    {
    	if(playerLives == 0)
    	{
    		setGameRunning(false); // sets game running to false once player has no lives left
    		gameOver();
    	}
    }
    
    // updating the game - this happens about 50 times a second to give the impression of movement
    public synchronized void updateGame()
    {    
    	if(levelOne == true) {
    		if(MAX_BRICKS == 0) {
	    		if(currentGameStatus == 0 || currentGameStatus == 1) {
	    			JDialog.setDefaultLookAndFeelDecorated(true);
	    			int response = JOptionPane.showConfirmDialog(null, "Do you want to continue to the next level?", "Confirmation",
	    					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //JOptionPane with a yes/no response box.
	    			
	    			if (response == JOptionPane.NO_OPTION) {
	    				Debug.trace("Model::Button clicked: No"); //Tells what to run when player selects no
	    				setGameRunning(false);
	    				currentGameStatus = 2;
	    			} 
	    			else if (response == JOptionPane.YES_OPTION) {
	    				Debug.trace("Model::Button clicked: Yes"); 
	    				currentGameStatus = 1;
	    				levelOne = false;
	    				levelTwo = true;
	    				startGame();
	    				initialiseGame();
	    				startLevelTwo();
	    		    	
	    		    	JOptionPane.showMessageDialog(null, "Level 2:" 
	    		    	+ "\nMore included balls" 
	    		    	+ "\nDifferent sized bricks"		//Shows another message box describing level two.
	    		    	+ "\nDifferent speed and more upgrades"
	    		    	+ "\nPress 'F' to speed up/slow down."
	    		    	+ "\n:)");

	    			} 
	    			else if (response == JOptionPane.CLOSED_OPTION) {
	    				Debug.trace("Model::Button clicked: Close::Shutting Down");
	    				currentGameStatus = 3;	//If the player clicks the X, the game shuts down.
	    				setGameRunning(false);
	    			}
	    		}
	    	}
    	}
    	
        // move the ball one step (the ball knows which direction it is moving in)
       	for(GameObj upgrade: upgrades) {
    		if(levelOne == true) {
    		if(upgrade.visible == true) {
    			if(laserUpgrade == false) {
    				upgrade.moveY(upgrade_MOVE);
    			}
    			else {
    				int y = upgrade.topY;
    				upgrade.moveYUp(upgrade_MOVE+2); //a new method which moves the object upwards
    	    		if(y <= 0 + M) { //If the upgrade goes off the screen, remove the upgrade.
    	    			upgrade.visible = false;
    	    			laserUpgrade = false;
    	    		}
    			}
    		}
    		
    		}
    		else {
        		if(upgrade.visible == true) {
        			upgrade.moveY(upgrade_MOVE);
        		}
    		}
    		int y = upgrade.topY;
    		if(y >= height - B_AREA - upgrade_HEIGHT) { //If the upgrade goes off the screen, remove the upgrade.
    			upgrade.visible = false;
    		}
    	}
    	for(GameObj star: stars) {
    		if(star.visible == true) {
    			star.moveY(star_MOVE);
    		}
    		int y = star.topY;
    		if(y >= height - B_AREA - star_HEIGHT) {
    			star.visible = false;
    		}
    	}
        
    	for(GameObj ball: balls) {

    			if(levelTwo == true) {
    				BALL_MOVE = 1;		//sets the ball move speed to 1
    				currentGameStatus = 0;
    			}
    			if(ball.visible == true) { //checks to see whether the ball can be seen
    			ball.moveX(BALL_MOVE);                      
    			ball.moveY(BALL_MOVE);
    			// get the current ball possition (top left corner)
    			int x = ball.topX;  
    			int y = ball.topY;
    			// Deal with possible edge of board hit
    			if (x >= width - B - BALL_SIZE)  ball.changeDirectionX();
    			if (x <= 0 + B)  ball.changeDirectionX();
    			if (y >= height - B - BALL_SIZE)  // Bottom
    			{ 
    				ball.changeDirectionY(); 
    				addToScore( HIT_BOTTOM );     // score penalty for hitting the bottom of the screen
    				playerLives--; // decreases the player lives by 1 each time the ball hits the bottom
    				if(playerLives > 0) {
    					hitBottom();
    				}
    			}
    			if (y <= 0 + M)  ball.changeDirectionY();

    			// check whether ball has hit a (visible) brick
    			boolean hit = false;
    			// *[3]******************************************************[3]*
    			// * Fill in code to check if a visible brick has been hit      *
    			// * The ball has no effect on an invisible brick               *
    			// * If a brick has been hit, change its 'visible' setting to   *
    			// * false so that it will 'disappear'                          * 
      			// **************************************************************
 
    			GraphicsContext gc = view.canvas.getGraphicsContext2D();   			
    			for (GameObj brick: bricks) {
        				if(levelOne == true) {
    					for(GameObj upgrade: upgrades) {
    			   			if(laserUpgrade == true && brick.visible == true) {
    							if(upgrade.visible && brick.hitBy(upgrade)) {
    								brick.visible = false;
    								ballHitBrick();
    								MAX_BRICKS--;
    								addToScore(HIT_BRICK);
    							}
    			   			}
    					}
        			}
				if (brick.visible && brick.hitBy(ball)) { //checks through the brick when it's hit
					if(brick.brickHealth == true) {
						view.changeCol(gc, brick);
						hit = true;
						brick.brickHealth = false;	//sets the bricks health to false if was previously true
						ballHitBrick();
						brick.brickHit = true;
					}				
					else { // if this time the brickHealth == false
						hit = true;														      // turn the brick invisible
						brick.visible = false;      // set the brick invisible
						if(brick.brickHit == true) {	//checks if this brick has already been hit before
							addToScore( HIT_BRICK+100 );    // add bonus points if this is true
						}
						addToScore( HIT_BRICK );
						ballHitBrick();
						MAX_BRICKS --; // decreases the destroyed brick counter by 1
						
						if(levelOne == true) {
							if(MAX_BRICKS == 35) {
								if(laserUpgrade == false) {
									GameObj upgrade_TWO = new GameObj(545, 10, upgrade_HEIGHT, upgrade_WIDTH, Color.PURPLE );
									upgrades.add(upgrade_TWO); //adds the upgrade to the game
								}
							}
							switch(MAX_BRICKS){
							case 59:
								GameObj star_ONE = new GameObj(5, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
								GameObj star_TWO = new GameObj(295, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
								GameObj star_THREE = new GameObj(570, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
								stars.add(star_ONE);
								stars.add(star_TWO);
								stars.add(star_THREE);
								starWave = 1;
								break;
							case 30:
								GameObj star_FOUR = new GameObj(5, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
								GameObj star_FIVE = new GameObj(295, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
								GameObj star_SIX = new GameObj(570, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
								stars.add(star_FOUR);
								stars.add(star_FIVE);
								stars.add(star_SIX);
								starWave = 2;
								break;
							case 15:
								GameObj star_SEVEN = new GameObj(5, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );  //Simple switch statement.
								GameObj star_EIGHT = new GameObj(295, 10, star_HEIGHT, star_WIDTH, Color.YELLOW ); //if/when MAX_BRICKS reaches 50, 30 or 15,
								GameObj star_NINE = new GameObj(570, 10, star_HEIGHT, star_WIDTH, Color.YELLOW ); //add these star objects into the game.
								stars.add(star_SEVEN);
								stars.add(star_EIGHT);
								stars.add(star_NINE);
								starWave = 3; //star waves links to another switch statement which gives the player different bonuses.
								break;
							}
						}
					}
					brickCounter++;
					Debug.trace("Model::brickCounter: " + brickCounter); //logs to the system how many bricks have been hit by the ball
				}
				
				for(GameObj star: stars) { 
					if(levelOne == true) {
						if(star.visible == true && star.hitBy(bat)) {
							switch(starWave) { //This links to the other switch statement above.
							case 1:
								GameObj upgrade_THREE = new GameObj (bat.topX, height - BRICK_HEIGHT*3/2, star_HEIGHT, star_WIDTH, Color.PINK );
								upgrades.add(upgrade_THREE);
								score += 200;
								star.visible = false;
					    		gotStar();
					    		laserUpgrade = true;
								break;
							case 2: 
								score += 500; //All give different score points and set the star invisible once collected.
								star.visible = false;
								gotStar();
								break;
							case 3:
								score += 1000;
								star.visible = false;
								gotStar();
								break;						
							}
						}
					}
				}
				for(GameObj star: stars) {
					for(GameObj upgrade: upgrades) {
						if(levelOne == true) {
							if(upgrade.visible == true && upgrade.hitBy(bat)) {
								if(laserUpgrade == false) {
									bat    = new GameObj(width/3, height - BRICK_HEIGHT*3/2, BRICK_WIDTH*6, BRICK_HEIGHT/4, Color.GREEN); //Gives the player an upgraded bat
									gotUpgrade = true; //Tells the system they have the upgrade
									upgrade.visible = false; //Sets the upgrade block to false so it can no longer be seen.
									batPos = 21; //Update the batPos as it is bigger than before.
									Debug.trace("Model::gotUpgrade: " + gotUpgrade); //Logs that the player has the upgrade.
									upgradeSound();
								}
							}
						}
					
						else if(levelTwo == true) {								
							if(upgrade.visible == true && upgrade.hitBy(bat)) {
								if(upgradeOne == true) {
									upgrade.visible = false; 
									score -= 500;
									downgradeSound();
									Debug.trace("Model::downgrade: Minus Score");
									upgradeOne = false;
								}
								
								else if(upgradeTwo == true) { 
									bat    = new GameObj(width/2, height - BRICK_HEIGHT*3/2, BRICK_WIDTH*3, BRICK_HEIGHT/4, Color.GRAY);  //star
									gotUpgrade = false;
									upgrade.visible = false;
									batPos = 30;
									Debug.trace("Model::gotUpgrade: " + gotUpgrade);
									downgradeSound();								
									Debug.trace("Model::Better bat");
								}
							}
							if(star.visible == true && star.hitBy(bat)) {
								if(starOne == true) {
									star.visible = false;
									playerLives +=10;
									score += 1000;
									upgradeSound();
									Debug.trace("Model::Lives & Score increase");
									starOne = false;
								}							
								else if(starTwo == true) { 
									bat    = new GameObj(width/3, height - BRICK_HEIGHT*3/2, BAT_WIDTH*6, BRICK_HEIGHT/4, Color.GREEN);
									gotUpgrade = true;
									star.visible = false;
									batPos = 21;
									Debug.trace("Model::gotUpgrade: " + gotUpgrade);
									upgradeSound();
									Debug.trace("Model::Downgraded bat");
								}
							}
								/* These are all the upgrades for level two and I have coded this using a switch statement rather than using
								 * loads of if/else ifs statements. Case 1 and 2 are very simple and just add on extra lives and add/remove points.
								 * Case 3 and 4 change the size of the bat like in level one.					
								 */							
						}
					}			
				}   
			}
    		if (hit)
    		ball.changeDirectionY();
       
    		// check whether ball has hit the bat
    		if ( ball.hitBy(bat) ) {
    				ball.changeDirectionY();
    				File ballHitBat = new File("hitBat.wav");
    				playMusic(ballHitBat);
    			}
    		}
    	}
    	
    	if(levelTwo == true) {
    		if(ballCount == 0) { //An integer declared at the beginning to check how many balls have been added.
    		GameObj ball_ONE   = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.PURPLE );  // adding more balls to the by using an array
    		balls.add(ball_ONE); //adds the ball into the game here rather than in the initialiseGame method. 
    		ballCount = 1; //sets the ball count to 1 once level two has started.
   			}
    		switch(MAX_BRICKS) {
	        	case 117:
	        	if(ballCount == 1) {
	        		GameObj ball_TWO = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.AQUA ); // like the bricks.
	        		balls.add(ball_TWO); //Same as above but this time it is in a switch statement and adds the second
	        		ballCount = 2;		// ball when the MAX_BRICKS integer has been reduced to 117.
	        	}						//This is the same for all of the other balls that are added.
	        	break;
	        	case 116:
	        	if(ballCount == 2) {
	        		GameObj ball_THREE = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.VIOLET);
	        	 	balls.add(ball_THREE);
	        	 	ballCount = 3;
					upgradeCount = 1;	//At this point I have also added upgrades into the switch statement, same as the ball
					//count, I have also added an upgrade count.
	        	}	
	        	break;
        	case 114:
        	if(ballCount == 3) {
    			GameObj star_ONE = new GameObj(550, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
    			stars.add(star_ONE);
    			starOne = true;
        		GameObj ball_FOUR   = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.ORANGE ); // like the bricks.
        		balls.add(ball_FOUR);	
        		ballCount = 4;	
        	}
        		break;
        	case 110:
        	if(ballCount == 4) {
        		GameObj ball_FIVE = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.TAN);
        		GameObj upgrade_ONE = new GameObj(5, 10, upgrade_HEIGHT/2, upgrade_WIDTH/2, Color.BROWN );
        		upgradeOne = true;
        		upgrades.add(upgrade_ONE);
        	 	balls.add(ball_FIVE);
        	 	ballCount = 5;
        	}
        		break;
        	case 105:
        		if(ballCount == 5) {
        			GameObj ball_SIX = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.MEDIUMORCHID ); // like the bricks.
        			balls.add(ball_SIX);
        			ballCount = 6;
        		}
        		break;
        	case 90:
        		if(ballCount == 6) {
        			GameObj ball_SEVEN = new GameObj(width/2, height/2, BALL_SIZE/2, BALL_SIZE/2, Color.KHAKI);
          			balls.add(ball_SEVEN);   			
        			ballCount = 7;
        			upgradeCount = 2;
        		}
        		break;
        		case 60:
        		if(upgradeCount < 3) {
        			GameObj star_TWO = new GameObj(570, 10, star_HEIGHT, star_WIDTH, Color.YELLOW );
        			stars.add(star_TWO);
        			starOne = false;
        			starTwo = true;
        			upgradeCount = 3;
        		}
        		break;
        		case 40:
        		if(upgradeCount < 4) {
        			GameObj upgrade_TWO = new GameObj(350, 10, upgrade_HEIGHT/2, upgrade_WIDTH/2, Color.BROWN );
        			upgrades.add(upgrade_TWO);
        			upgradeCount = 4;
        			upgradeOne = false;
        			upgradeTwo = true;
	        	}
        		/*
        		 * I found it better added the ball objects from here rather than in the initialiseGame method like before.
        		 * Rather than having to set the object to invisible and waiting till the player reaches a certain point,
        		 * I learnt that I could just do it this way instead.
        		 */
	        }
    	}
    }
    

    // This is how the Model talks to the View
    // Whenever the Model changes, this method calls the update method in
    // the View. It needs to run in the JavaFX event thread, and Platform.runLater 
    // is a utility that makes sure this happens even if called from the
    // runGame thread
    public synchronized void modelChanged()
    {
        Platform.runLater(view::update);
    }
        
    // Methods for accessing and updating values
    // these are all synchronized so that the can be called by the main thread 
    // or the animation thread safely
    
    // Change game running state - set to false to stop the game
    public synchronized void setGameRunning(Boolean value)
    {  
        gameRunning = value;
    }
    
    // Return game running state
    public synchronized Boolean getGameRunning()
    {  
        return gameRunning;
    }

    // Change game speed - false is normal speed, true is fast
    public synchronized void setFast(Boolean value)
    {  
        fast = value;
    }
    
    // Return game speed - false is normal speed, true is fast
    public synchronized Boolean getFast()
    {  
        return(fast);
    }

    // Return bat object
    public synchronized GameObj getBat()
    {
        return(bat);
    }
    
    // return balls
    public synchronized ArrayList<GameObj> getBalls() //changed into an array.
    {
        return(balls);
    }  
        
    // return bricks
    public synchronized ArrayList<GameObj> getBricks()
    {
        return(bricks);
    }
    
    public synchronized ArrayList<GameObj> getUpgrades()
    {
        return(upgrades);
    }  
    
    public synchronized ArrayList<GameObj> getStars()
    {
    	return(stars);
    }
    
    // return score
    public synchronized int getScore()
    {
        return(score);
    }
    
    public synchronized int getPlayerLives()
    {
    	return(playerLives);
    }

  
	// update the score
    public synchronized void addToScore(int n)    
    {
        score += n;        
    }
    
  
    public synchronized void startLevelTwo() {
    	score = 0;
    	batPos = 30;
    	setFast(false);
    	BALL_MOVE = 3;
    	MAX_BRICKS = 120;		//Setting integers and boolean values once level one has finished
    	brickCounter = 0;
    	gotUpgrade = false;
    	playerLives = 20;
    }

    public synchronized void batLeft() {
	    if(levelOne == true) { //checks for level one
	    	if(gotUpgrade == false) //if the player has not got the bat upgrade
	    	{
	    		if(batPos != 1) { // check the bat positions and move appropriately 
	    			batPos --;
	    			bat.moveX(-10); //moves the bat -10 steps if it is more than 1
	    		}
	    		else {
	    			batPos = 1; //sets the position back to 1 if player attempts to go off screen
	    		}
	    	}
	    	else {
	    		if(batPos != 2) {
	    			batPos --;
	    			bat.moveX(-10); //Same as above but with the bats increased size with an upgrade, the integers are a little different.
	    		}
	    		else {
	    			batPos = 2; 
	    		}
	    	}
	    }
	    else if(levelTwo == true) {
	    	if(gotUpgrade == false)
	    	{
	    		if(batPos != 16) {
	    			batPos --;
	    			bat.moveX(-20);
	    		}
	    		else {
	    			batPos = 16;
	    		}
	    	}
	    	else {
	    		if(batPos != 12) {
	    			batPos --;
	    			bat.moveX(-20);
	    		}
	    		else {
	    			batPos = 12; 
	    		}
	    	}
	    }
    }
    public synchronized void batRight() {
    	if(levelOne == true) {
	    	if(gotUpgrade == false) {
	    		if(batPos != 44) {
	    			batPos ++;
	    			bat.moveX(+10);
	    		}
	    		else {
	    			batPos = 44;
	    		}
	    	}
	    	else {
	    		if(batPos != 29) {
	    			batPos ++;
	    			bat.moveX(+10);
	    		}
	    		else {
	    			batPos = 29;
	    		}
	    	}
    	}
    	else if(levelTwo == true) {
	    	if(gotUpgrade == false) {
	    		if(batPos != 37) {
	    			batPos ++;
	    			bat.moveX(+20);
	    		}
	    		else {
	    			batPos = 37;
	    		}
	    	}
	    	else {
	    		if(batPos != 25) {
	    			batPos ++;
	    			bat.moveX(+20);
	    		}
	    		else {
	    			batPos = 25;
	    		}
	    	}
    	}
    }
    
    /* I changed the way the bat works a little bit because it was not working well for me when I was trying to make it
     * so that the bat woulndn't go off of the screen. I soon realised this was my mistake as I had set something to 
     * true instead of checking if it was true in an if statement.
     * I didn't feel the need to revert back to the old one so I have left it as it works as intended.
     */
    
    /*public synchronized void moveBat( int direction )
    {        
        int dist = direction * BAT_MOVE;    // Actual distance to move
        Debug.trace( "Model::moveBat: Move bat = " + dist );
        
        	if(batPos >= 15) { //right
        		batPos = 15;
        		
  	  		}
        	else if(batPos <= 0) { // left
        		batPos = 0;
        	}      		
        	else{moveSuccess = 1;
      		bat.moveX(dist);}
    	
        
    }
    */
}   
