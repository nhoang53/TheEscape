package edu.orangecoastcollege.escapethecatcher;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static edu.orangecoastcollege.escapethecatcher.BoardCodes.EXIT;
import static edu.orangecoastcollege.escapethecatcher.BoardCodes.OBSTACLE;


public class GameActivity extends Activity implements GestureDetector.OnGestureListener{

    private GestureDetector aGesture;

    //FLING THRESHOLD VELOCITY
    final int FLING_THRESHOLD = 500;

    //BOARD INFORMATION

    // imageView we set 50x50, but depend on device it will translate difference
    final int SQUARE = 150; // density pixel of obstacle// 150 is fit for Nexus 5 in this case
    final int OFFSET = 5;   // the gap, the margin top-left corner
    final int ROWS = 8;
    final int COLUMNS = 7;
    final int gameBoard[][] = {
            {1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 1, 2, 1, 1},
            {1, 2, 2, 2, 2, 2, 1},
            {1, 2, 1, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 1, 1},
            {1, 2, 2, 2, 2, 2, 3},
            {1, 2, 1, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 1}
    };

    private Player player;
    private Zombie zombie;

    //LAYOUT AND INTERACTIVE INFORMATION
    private ArrayList<ImageView> visualObjects;
    private RelativeLayout activityGameRelativeLayout;
    private ImageView zombieImageView;
    private ImageView playerImageView;
    private ImageView obstacleImageView;
    private ImageView exitImageView;
    private int exitRow;
    private int exitCol;

    //  WINS AND LOSSES
    private int wins;
    private int losses;
    private TextView winsTextView;
    private TextView lossesTextView;

    private LayoutInflater layoutInflater;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        activityGameRelativeLayout = (RelativeLayout) findViewById(R.id.activity_game);
        winsTextView = (TextView) findViewById(R.id.winsTextView);
        lossesTextView = (TextView) findViewById(R.id.lossesTextView);

        //
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resources = getResources();

        visualObjects = new ArrayList<ImageView>();

        wins = 0;
        losses = 0;
        winsTextView.setText(resources.getString(R.string.win) + wins);
        lossesTextView.setText(resources.getString(R.string.losses) + losses);

        // add Gesture
        aGesture = new GestureDetector(activityGameRelativeLayout.getContext(), this);

        startNewGame();
    }

    private void startNewGame() {
        //TASK 1:  CLEAR THE BOARD (ALL IMAGE VIEWS)
        for (int i = 0; i < visualObjects.size(); i++) {
            ImageView visualObj = visualObjects.get(i);
            activityGameRelativeLayout.removeView(visualObj);
        }
        visualObjects.clear();

        //TASK 2:  BUILD THE  BOARD
        buildGameBoard();

        //TASK 3:  ADD THE CHARACTERS
        createZombie();
        createPlayer();
    }

    private void buildGameBoard() {
        // TODO: Inflate the entire game board (obstacles and exit)
        for(int i = 0; i < ROWS; i ++)
            for(int j = 0; j < COLUMNS; j++)
            {
                if(gameBoard[i][j] == OBSTACLE) {
                    obstacleImageView = (ImageView) layoutInflater.inflate(R.layout.obstacle_layout, null);
                    obstacleImageView.setX(j * SQUARE + OFFSET);
                    obstacleImageView.setY(i * SQUARE + OFFSET);
                    activityGameRelativeLayout.addView(obstacleImageView);
                }
                if(gameBoard[i][j] == EXIT){
                    exitImageView = (ImageView) layoutInflater.inflate(R.layout.exit_layout, null);
                    exitImageView.setX(j * SQUARE + OFFSET);
                    exitImageView.setY(i * SQUARE + OFFSET);
                    activityGameRelativeLayout.addView(exitImageView);

                    // set exit position
                    exitRow = i;
                    exitCol = j;
                }
            }
    }

    private void createZombie() {
        // TODO: Determine where to place the Zombie (at game start)
        // TODO: Then, inflate the zombie layout
        // Let's instantiate a new zombie object
        int row = 5;
        int col = 5;
        zombie = new Zombie();
        zombie.setRow(row);
        zombie.setCol(col);

        // Let's inflate the zombie layout at a specific x and y location
        zombieImageView = (ImageView) layoutInflater.inflate(R.layout.zombie_layout, null);
        // Set the x and y coordinate of the imageView
        zombieImageView.setX(col * SQUARE + OFFSET);
        zombieImageView.setY(row * SQUARE + OFFSET);
        // Display the zombie image view within relative layout
        activityGameRelativeLayout.addView(zombieImageView);

        // Add the zombie image view to the ArrayList
        visualObjects.add(zombieImageView); // so image don't stack on each other
    }

    private void createPlayer() {
        // TODO: Determine where to place the Player (at game start)
        // TODO: Then, inflate the player layout
        // Let's instantiate a new player object
        int row = 1;
        int col = 1;
        player = new Player();
        player.setRow(row);
        player.setCol(col);

        // Let's inflate the zombie layout at a specific x and y location
        playerImageView = (ImageView) layoutInflater.inflate(R.layout.player_layout, null);
        // Set the x and y coordinate of the imageView
        playerImageView.setX(col * SQUARE + OFFSET);
        playerImageView.setY(row * SQUARE + OFFSET);
        // Display the zombie image view within relative layout
        activityGameRelativeLayout.addView(playerImageView);

        // Add the zombie image view to the ArrayList
        visualObjects.add(playerImageView); // so image don't stack on each other
    }



    private void movePlayer(float velocityX, float velocityY) {
        // TODO: This method gets called in the onFling event

        // TODO: Determine which absolute velocity is greater (x or y)
        // TODO: If x is negative, move player left.  Else if x is positive, move player right.
        // TODO: If y is negative, move player down.  Else if y is positive, move player up.

        // TODO: Then move the zombie, using the player's row and column position.

        String direction ="";

        // Decide if x or y is bigger:
        if(Math.abs(velocityX) > Math.abs(velocityY))
        {
            // X is larger (MOVE LEFT OR RIGHT)
            // Determine if move is LEFT:
            if(velocityX < -FLING_THRESHOLD)
                direction = "RIGHT";
            else if(velocityX > FLING_THRESHOLD)
                direction = "LEFT";
        }
        else // Y is larger (MOVE UP or DOWN)
        {
            if(velocityY < -FLING_THRESHOLD)
                direction = "DOWN";
            else if(velocityY > FLING_THRESHOLD)
                direction = "UP";
        }

        // ONLY move the player IF direction is NOT an empty string
        if(!direction.equals(""))
        {
            player.move(gameBoard, direction); // move
            playerImageView.setX(player.getCol() * SQUARE + OFFSET);
            playerImageView.setY(player.getRow() * SQUARE + OFFSET);
        }
        // MOVE the zombie no matter what
        zombie.move(gameBoard, player.getCol(), player.getRow());
        zombieImageView.setX(zombie.getCol() * SQUARE + OFFSET);
        zombieImageView.setY(zombie.getRow() * SQUARE + OFFSET);

        // Determine if the game is won or lost
        // Game win
        if(player.getRow() == exitRow && player.getCol() == exitCol) {
            winsTextView.setText(resources.getString(R.string.win) + (++wins));
            startNewGame();
        }

        // Game lost
        if(player.getRow() == zombie.getRow() && player.getCol() == zombie.getCol()) {
            lossesTextView.setText(resources.getString(R.string.losses) + (++losses));
            startNewGame();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1,
                           float velocityX, float velocityY)
    {
        movePlayer(velocityX, velocityY);

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return aGesture.onTouchEvent(event);
    }
}
