package com.dom.gui;

import com.dom.game.GameConstants;

import java.awt.*;
import java.awt.event.InputEvent;


public class BasicMoves {

    static Robot robot;

    static {

        try {

            robot = new Robot();
        }
        catch( AWTException e ) {

            System.out.println( "Problem creating java.awt.Robot()" );
        }
    }


    static void mulliganCard( int cardIndex, boolean isFirst ) {

        double divisor = isFirst ? 2 : 3;
        int firstX = isFirst ? PositionConstants.MULL_FIRST_CARD1_X : PositionConstants.MULL_SECOND_CARD1_X;
        int lastX = isFirst ? PositionConstants.MULL_FIRST_CARD3_X : PositionConstants.MULL_SECOND_CARD4_X;

        assert( cardIndex <= divisor ):
                "illegal mulligan card index " + cardIndex + " when going " + ( isFirst ? "first" : "second" );

        int clickX = (int) ( firstX + cardIndex * ( lastX - firstX ) / divisor );

        click( clickX, PositionConstants.MULL_Y );
    }


    static void playMinion( int minionIndex, int numMinionsOnBoard ) {

        assert( minionIndex < numMinionsOnBoard + 1 ):
                "cannot place minion in " + minionIndex + "th position if oard if there are only " +
                numMinionsOnBoard + " friendly minions on the board";

        int boardX = PositionConstants.MIDDLE_BOARD_X +
                (int) ( ( minionIndex - 0.5 * numMinionsOnBoard ) * PositionConstants.SPACE_BETWEEN_MINION );

        click( boardX, PositionConstants.MY_BOARD_Y );
    }


    static void clickMinion( int minionIndex, int numMinionsOnBoard, boolean isEnemy ) {

        assert( minionIndex < numMinionsOnBoard ):
                "cannot click " + (minionIndex + 1) + "th " + ( isEnemy ? "enemy" : "friendly" ) +
                " on board if there are only " + numMinionsOnBoard;

        int minionX = PositionConstants.MIDDLE_BOARD_X +
                (int) ( ( minionIndex - 0.5 * ( numMinionsOnBoard - 1 ) ) * PositionConstants.SPACE_BETWEEN_MINION );

        int minionY = isEnemy ? PositionConstants.HIS_BOARD_Y : PositionConstants.MY_BOARD_Y;

        click( minionX, minionY );
    }


    static void chooseCard( int cardIndex, int numCardsInHand ) {

        int firstX = PositionConstants.FIRST_CARD_X[ numCardsInHand ];
        int lastX = PositionConstants.LAST_CARD_X[ numCardsInHand ];

        assert( cardIndex <= GameConstants.MAX_CARDS_IN_HAND ): "chose card " + (cardIndex + 1) +
                " but cannot have more than " + GameConstants.MAX_CARDS_IN_HAND + " cards";

        assert( cardIndex < numCardsInHand ): "chose card " + (cardIndex + 1) +
                " but only have " + numCardsInHand + " cards in hand";

        int cardX = (int) ( firstX + cardIndex * ( lastX - firstX ) / ( (double) ( numCardsInHand - 1) ) );

        click( cardX, PositionConstants.CARD_Y );
    }


    static void clickFace() {

        click( PositionConstants.HIS_FACE_X, PositionConstants.HIS_FACE_Y );
    }


    static void confirmMulligan() {

        click( PositionConstants.MULL_CONFIRM_X, PositionConstants.MULL_CONFIRM_Y );
    }


    static void endTurn() {

        click( PositionConstants.END_TURN_X, PositionConstants.END_TURN_Y );
    }


    static void clickHeroPower() {

        click( PositionConstants.HERO_POWER_X, PositionConstants.HERO_POWER_Y );
    }


    static void moveMouse( int x, int y ) {

        robot.mouseMove( x, y );
    }


    static void click( int x, int y ) {

        moveMouse( x, y );

        wait( 500 );

        robot.mousePress( InputEvent.BUTTON1_MASK );

        robot.mouseRelease( InputEvent.BUTTON1_MASK );

        wait( 10 );
    }


    static void wait( int millis ) {

        try {

            synchronized ( Thread.currentThread() ) {
                Thread.currentThread().wait( millis );
            }
        }
        catch ( InterruptedException e ) {

            System.out.println( "Wait interrupted" );
        }

    }



    public static void main( String[] args ) {

        click( 100, 100 ); // click on the game window

        wait( 100 ) ;

        /*
        mulliganCard(1, true);

        mulliganCard( 2, true );

        confirmMulligan();
        */

        /*
        clickHeroPower();

        wait( 500 );

        endTurn();
        */

        chooseCard( 1, 4 );

        wait(400);

        clickMinion(0, 1, true);

        wait( 400 );

        chooseCard(1, 3);

        wait( 400 );

        clickMinion( 0, 1, true );

        wait( 400 );

        clickMinion( 0, 1, false );

        wait( 400 );

        clickFace();

        wait(400);

        clickMinion( 1, 1, false );

        wait( 400 );

        clickFace();

        wait(400);

        clickHeroPower();

        wait( 400 );

        endTurn();

        /*

        clickMinion( 0, 2, false );

        wait( 500 );

        chooseCard( 0, 7 );

        wait( 500 );

        clickMinion( 0, 3, true );

        wait( 500 );

        clickMinion( 0, 2, false );

        wait( 500 );

        clickMinion( 0, 2, true );

        //wait( 500 );

        endTurn();
        */

    }
}
