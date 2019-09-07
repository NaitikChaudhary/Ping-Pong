package com.naitik.tennis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView racket,racket2 , ball;
    private int screenWidth;
    private int screenHeight;
    private TextView startText;
    private LinearLayout mainLayout;

    //ball motion
    private int hitX = 0, hitYUp = 0, hitYDown = 0, hitSwing = 0;
    private boolean startFlag = false, resetGame = false;

    //----pointers----
    int x = -100, y = -100, x2 = -100, y2 = -100;

    //---player scores-----
    int player1score = 0, player2score = 0;


    //----racket dimensions--------
    int height, width, height2, width2;

    boolean commit = false, commit2 = false, gameStart = true;


    //co-ordinates
    private float ballX, ballY, racketX, racketY, racket2X, racket2Y;

    //Time for calculating speed
    private float t = 0, resetTime = 0;

    //Motion parameters
    private float ball_Velocity = 0;

    float swingHelper1 = 0, swingHelper2 = 0, swingHelper3 = 0, swingHelper4 = 0;
    float swing = 0;
    float swingSET1 = 0, swingSET2 = 0;


    String where;


    // Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        racket = (ImageView) findViewById(R.id.racket);
        racket2 = (ImageView) findViewById(R.id.racket2);
        ball = (ImageView) findViewById(R.id.ball);
        startText = (TextView) findViewById(R.id.startText);

        ballX = -100;
        ballY = -100;

        racketX = -100;
        racketY = -100;
        racket2X = -100;
        racket2Y = -100;

        racket.setX(racketX);
        racket.setY(racketY);
        racket2.setX(racket2X);
        racket2.setY(racket2Y);

        ball.setY(ballY);
        ball.setX(ballX);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(startFlag) {
                            if(startFlag) {
                                startText.setText(String.valueOf(player1score) + "           -           " + String.valueOf(player2score));
                            }
                            ballTrajectory();
                        }
                    }
                });
            }
        }, 0, 8);


    }


    private void ballTrajectory() {

        if(resetGame) {
            resetTime += 0.008;
        }

        if(resetTime > 2) {
            if(where == "Right") {

                ballX = (4*screenWidth/5 - ball.getWidth());
                ballY = screenHeight/2 - ball.getHeight()/2;

                ball.setY(ballY);
                ball.setX(ballX);

                commit2 = false;

            } else if(where == "Left") {

                ballX = (screenWidth/5);
                ballY = screenHeight/2 - ball.getHeight()/2;

                ball.setY(ballY);
                ball.setX(ballX);

                commit = false;

            }
            resetTime = 0;
            resetGame = false;
        }

        if (ballHit2() && !commit2) {

            hitX = 2;
            t=0;
            commit = false;
            commit2 = true;
            hitYUp = 2;
            hitYDown = 1;
            ball_Velocity = 2*getDistanceAwayFromCenterHit2();
            setSwingValue();

        }

        if(ballHit() && !commit) {

            commit2 = false;
            hitSwing = 0;
            commit = true;
            t = 0;
            hitX = 1;
            hitYUp = 2;
            hitYDown = 1;
            ball_Velocity = 2*getDistanceAwayFromCenterHit();
            setSwingValue();

        }

        if(hitX == 2) {
            if(ballX > racketX + racket.getWidth() + screenWidth/50)
                swingHelper1 = racketY + racket.getHeight()/2;
            if(ballX > racketX + racket.getWidth() + screenWidth/48)
                swingHelper2 = racketY + racket.getHeight()/2;
        }

        if(hitX == 1) {
            if(ballX < racket2X - screenWidth/50)
                swingHelper3 = racket2Y + racket2.getHeight()/2;
            if(ballX < racket2X - screenWidth/48)
                swingHelper4 = racket2Y + racket2.getHeight()/2;
        }

        setXMotion();
        swingBall();
        setYMotion();

    }

    private void endGame() {

        if(player1score > 2) {

            Intent resultIntent = new Intent(getApplicationContext(), ResultsActivity.class);
            resultIntent.putExtra("result", String.valueOf(player1score) + "           -           " + String.valueOf(player2score));
            resultIntent.putExtra("winningPlayer","Player 1 is Winner!");
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            gameStart = false;
            startActivity(resultIntent);
            finish();

        } else if (player2score > 2){

            Intent resultIntent = new Intent(getApplicationContext(), ResultsActivity.class);
            resultIntent.putExtra("result", String.valueOf(player1score) + "           -           " + String.valueOf(player2score));
            resultIntent.putExtra("winningPlayer","Player 2 is Winner!");
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            gameStart = false;
            startActivity(resultIntent);
            finish();

        }

    }

    private void swingBall() {
        if(hitX == 1) {
            if(ballX > 2*screenWidth/5) {
                setSwingMotion(4);
                t+=0.02;
            } else if(ballX > 2*screenWidth/3) {
                setSwingMotion(0.8f);
                t+=0.02;
            }
        } else if (hitX == 2) {
            if(ballX < 3*screenWidth/5) {
                setSwingMotion(4);
                t+=0.02;
            } else if(ballX < screenWidth/3) {
                setSwingMotion(0.8f);
                t+=0.02;
            }
        }
    }

    private void setSwingMotion( float speed) {

        if(swing > 0) {
            if(hitAtTop()){

                ball_Velocity += swing*t;
                t=0;
                swing = 0;

            }

            swingYUp(swing, speed);

        } else if (swing < 0) {
            float swinging = - swing;
            if(hitAtTop()){
                hitSwing = 0;
            }
            if(hitAtBottom()) {
                hitSwing = 1;
            }
            if(hitSwing == 0) {
                swingYDown(swinging, speed);
            } else if(hitSwing == 1) {
                swingYUp(swinging, speed);
            }
        }

    }

    private void setSwingValue() {

        if(hitX == 1) {
            swingSET1 = racketY + racket.getHeight()/2 - swingHelper1;
            swingSET2 = racketY + racket.getHeight()/2 - swingHelper2;
        } else if(hitX == 2) {
            swingSET1 = racket2Y + racket2.getHeight()/2 - swingHelper3;
            swingSET2 = racket2Y + racket2.getHeight()/2 - swingHelper4;
        }


        swing = (swingSET1 + swingSET2 )/2;

        if(swing > 50) {
            swing = 25;
        } else if(swing > 25) {
            swing = 20;
        } else if(swing > 8) {
            swing = 15;
        } else if(swing < -50) {
            swing = -25;
        } else if(swing < -25) {
            swing = -20;
        } else if(swing < -8) {
            swing = -15;
        }

    }

    private void setYMotion() {

        if(ball_Velocity > 0) {
            setYMotionUp(3*ball_Velocity);
        } else {
            setYMotionDown(-3*ball_Velocity);
        }

    }

    private void setYMotionDown(float speed) {
        if(hitAtTop()) {
            hitYDown = 1;
        }
        if(hitAtBottom()) {
            hitYDown = 2;
        }
        if(hitYDown == 2) {
            moveYUp(speed);
        } else if(hitYDown == 1) {
            moveYDown(speed);
        }
    }

    private void setYMotionUp(float speed) {

        if(hitAtTop()) {
            hitYUp = 3;
        }
        if(hitAtBottom()) {
            hitYUp = 2;
        }
        if(hitYUp == 2) {
            moveYUp(speed);
        } else if(hitYUp == 3) {
            moveYDown(speed);
        }

    }


    private float getDistanceAwayFromCenterHit () {
        return racketY + racket.getHeight()/2 - ballY - ball.getHeight()/2;
    }

    private float getDistanceAwayFromCenterHit2 () {
        return racket2Y + racket2.getHeight()/2 - ballY - ball.getHeight()/2;
    }

    private boolean ballHit() {

        if(racketX + racket.getWidth() > ballX && racketX < ballX + racket.getWidth()
                && racketY + racket.getHeight() > ballY + ball.getHeight()/2
                && racketY < ballY + ball.getHeight()/2) {

            return true;

        } else
            return false;

    }

    private boolean ballHit2() {

        if(racket2X < ballX + ball.getWidth() && racket2X + racket2.getWidth() > ballX + ball.getWidth()
                && racket2Y + racket2.getHeight() > ballY + ball.getHeight()/2
                && racket2Y < ballY + ball.getHeight()/2) {

            return true;

        } else
            return false;

    }

    private void setXMotion() {

        if(hitX == 1) {
            moveXRight(screenWidth/150);       //current X velocity is 10
            if(ballX + ball.getWidth() > screenWidth) {
                hitX = 3;
                swing = 0;
                where = "Right";
                resetGame = true;
                commit2 = true;
                hitYUp = 5;
                hitYDown = 5;
                player1score++;
            }
        }
        if(hitX == 2) {
            moveXLeft(screenWidth/150);       //current X velocity is 10
            if(ballX < 0) {
                hitX = 3;
                commit = true;
                swing = 0;
                where = "Left";
                hitYUp = 5;
                resetGame = true;
                hitYDown = 5;
                player2score++;
            }
        }

    }

    private void swingYUp(float speed, float swingSpeed) {
        ballY -= speed*t/swingSpeed;                //Upswing
        ball.setY(ballY);
    }

    private void swingYDown(float speed, float swingSpeed) {
        ballY += speed*t/swingSpeed;                //Downswing
        ball.setY(ballY);
    }

    private void moveYUp(float speed) {
        ballY -= speed/100;
        ball.setY(ballY);
    }

    private void moveYDown(float speed) {
        ballY += speed/100;
        ball.setY(ballY);
    }

    private void moveXRight(int speed) {
        ballX += speed;
        ball.setX(ballX);
    }

    private void moveXLeft (int speed) {
        ballX -= speed;
        ball.setX(ballX);
    }

    private boolean hitAtTop() {
        if(ballY < 0) {
            return true;
        } else return false;
    }

    private boolean hitAtBottom() {
        if(ballY + ball.getHeight() > screenHeight) {
            return true;
        } else return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!startFlag) {

            mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
            screenWidth = mainLayout.getWidth();
            screenHeight = mainLayout.getHeight();

            ballX = (screenWidth/5);
            ballY = screenHeight/2 - ball.getHeight()/2;

            ball.setY(ballY);
            ball.setX(ballX);

            height = racket.getHeight();
            width = racket.getWidth();

            height2 = racket2.getHeight();
            width2 = racket2.getWidth();

            racketX = (screenWidth/8);
            racket2X = 7*screenWidth/8 - racket2.getWidth();
            racketY = screenHeight/2 - racket.getHeight()/2;
            racket2Y = screenHeight/2 - racket2.getHeight()/2;

            racket.setX(racketX);
            racket.setY(racketY);
            racket2.setX(racket2X);
            racket2.setY(racket2Y);

            if(event.getAction() == MotionEvent.ACTION_UP) {
                startFlag = true;
            }

        } else {

            if(gameStart) {
                endGame();
            }

            if(event.getPointerCount() == 1) {
                if(event.getX() < 4*screenWidth/9){
                    x = (int)event.getX();
                    y = (int)event.getY();
                } else if (event.getX() > 5*screenWidth/9) {
                    x2 = (int)event.getX();
                    y2 = (int)event.getY();
                }
            } else if(event.getPointerCount() > 1) {
                if(event.getX(event.findPointerIndex(event.getPointerId(0))) < 4*screenWidth/9) {
                    x = (int) event.getX(event.findPointerIndex(event.getPointerId(0)));
                    y = (int) event.getY(event.findPointerIndex(event.getPointerId(0)));
                    if(event.getX(event.findPointerIndex(event.getPointerId(1))) > 5*screenWidth/9) {
                        x2 = (int) event.getX(event.findPointerIndex(event.getPointerId(1)));
                        y2 = (int) event.getY(event.findPointerIndex(event.getPointerId(1)));
                    }
                } else if(event.getX(event.findPointerIndex(event.getPointerId(0))) > 5*screenWidth/9) {
                    x2 = (int) event.getX(event.findPointerIndex(event.getPointerId(0)));
                    y2 = (int) event.getY(event.findPointerIndex(event.getPointerId(0)));
                    if(event.getX(event.findPointerIndex(event.getPointerId(1))) < 4*screenWidth/9) {
                        x = (int) event.getX(event.findPointerIndex(event.getPointerId(1)));
                        y = (int) event.getY(event.findPointerIndex(event.getPointerId(1)));
                    }
                }
            }


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(x < 4*screenWidth/9 && x>0) {
                        racket.setX(x-width/2 + screenWidth/20);
                        racket.setY(y-height/2);
                        racketX = x-width/2 + screenWidth/20;
                        racketY = y-height/2;
                    }
                case MotionEvent.ACTION_MOVE:
                    if(x < 4*screenWidth/9 && x>0) {
                        racket.setX(x-width/2 + screenWidth/20);
                        racket.setY(y-height/2);
                        racketX = x-width/2 + screenWidth/20;
                        racketY = y-height/2;
                    }
                case MotionEvent.ACTION_POINTER_DOWN:
                    if(x2 > 0) {
                        racket2X = x2 - width2/2 - screenWidth/20;
                        racket2Y = y2 - height2/2;
                        racket2.setX(racket2X);
                        racket2.setY(racket2Y);
                    }
                case MotionEvent.ACTION_UP:

            }

        }
        return false;
    }

}