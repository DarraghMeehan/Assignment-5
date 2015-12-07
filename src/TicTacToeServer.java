// Fig. 18.8: TicTacToeServer.java
// This class maintains a game of Tic-Tac-Toe for two client applets.

import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class TicTacToeServer extends JFrame {
    private char[] board;
    private JTextArea outputArea;
    private Player[] players;
    private ServerSocket server;
    private int currentPlayer;
    private int cnt;
    private final int PLAYER_X = 0, PLAYER_O = 1;
    private final char X_MARK = 'X', O_MARK = 'O';
    private String player1 = "player 1";
    private String player2 = "player 2";
    private String winner = "";
    private String gameState;
    private final String result = "The result is: ";
    private final int SIZE = 9;
    private int turns = 0;

    private boolean reset = false;
    private boolean replayBtn = false;
    private boolean replayGame = true;

    // set up tic-tac-toe server and GUI that displays messages
    public TicTacToeServer()
    {
        super( "Tic-Tac-Toe Server" );

        board = new char[ SIZE ];
        players = new Player[ 2 ];
        currentPlayer = PLAYER_X;

        // set up ServerSocket
        try {
            server = new ServerSocket( 12345, 2 );
        }

        // process problems creating ServerSocket
        catch( IOException ioException ) {
            ioException.printStackTrace();
            System.exit( 1 );
        }

        // set up JTextArea to display messages during execution
        outputArea = new JTextArea();
        getContentPane().add( outputArea, BorderLayout.CENTER );
        outputArea.setText( "Server awaiting connections\n" );

        setSize( 300, 300 );
        setVisible( true );

    } // end TicTacToeServer constructor

    // wait for two connections so game can be played
    public void execute()
    {
        // wait for each client to connect
        for ( int i = 0; i < players.length; i++ ) {

            // wait for connection, create Player, start thread
            try {
                players[ i ] = new Player( server.accept(), i );
                players[ i ].start();
            }

            // process problems receiving connection from client
            catch( IOException ioException ) {
                ioException.printStackTrace();
                System.exit( 1 );
            }
        }

        // Player X is suspended until Player O connects.
        // Resume player X now.
        synchronized ( players[ PLAYER_X ] ) {
            players[ PLAYER_X ].setSuspended( false );
            players[ PLAYER_X ].notify();
        }

    }  // end method execute

    // utility method called from other threads to manipulate
    // outputArea in the event-dispatch thread
    private void displayMessage( final String messageToDisplay )
    {
        // display message from event-dispatch thread of execution
        SwingUtilities.invokeLater(
                new Runnable() {  // inner class to ensure GUI updates properly

                    public void run() // updates outputArea
                    {
                        outputArea.append( messageToDisplay );
                        outputArea.setCaretPosition(
                                outputArea.getText().length() );
                    }

                }  // end inner class

        ); // end call to SwingUtilities.invokeLater
    }

    // Determine if a move is valid. This method is synchronized because
    // only one move can be made at a time.
    public synchronized boolean validateAndMove( int location, int player )
    {
        boolean moveDone = false;

        // while not current player, must wait for turn
        while ( player != currentPlayer ) {

            // wait for turn
            try {
                wait();
            }

            // catch wait interruptions
            catch( InterruptedException interruptedException ) {
                interruptedException.printStackTrace();
            }
        }

        // if location not occupied, make move
        if ( !isOccupied( location ) ) {

            turns++;

            // set move in board array
            board[ location ] = currentPlayer == PLAYER_X ? X_MARK : O_MARK;

            // change current player
            currentPlayer = ( currentPlayer + 1 ) % 2;

            // let new current player know that move occurred
            players[ currentPlayer ].otherPlayerMoved( location );

            notify(); // tell waiting player to continue

            // tell player that made move that the move was valid
            return true;
        }

        // tell player that made move that the move was not valid
        else
            return false;

    } // end method validateAndMove

    // determine whether location is occupied
    public boolean isOccupied( int location )
    {
        if ( board[ location ] == X_MARK || board [ location ] == O_MARK )
            return true;
        else
            return false;
    }

    // place code in this method to determine whether game over
    public boolean isGameOver()
    {
        // By Rows
        // Top Row
        if(board[0]=='X' && board[1]=='X' && board[2]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[0]=='O' && board[1]=='O' && board[2]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }
        // Middle Row
        if(board[3]=='X' && board[4]=='X' && board[5]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[3]=='O' && board[4]=='O' && board[5]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }
        // Bottom Row
        if(board[6]=='X' && board[7]=='X' && board[8]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[6]=='O' && board[7]=='O' && board[8]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }

        // By Columns
        // First Column
        if(board[0]=='X' && board[3]=='X' && board[6]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[0]=='O' && board[3]=='O' && board[6]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }
        // Middle Column
        if(board[1]=='X' && board[4]=='X' && board[7]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[1]=='O' && board[4]=='O' && board[7]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }
        // Last Column
        if(board[2]=='X' && board[5]=='X' && board[8]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[2]=='O' && board[5]=='O' && board[8]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }

        // On Diagonals
        // Left to Right
        if(board[0]=='X' && board[4]=='X' && board[8]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[0]=='O' && board[4]=='O' && board[8]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }
        // Right to Left
        if(board[2]=='X' && board[4]=='X' && board[6]=='X') {
            outputArea.append("\nPlayer 1 wins");
            gameState ="Player1";
            return true;
        }
        else if(board[2]=='O' && board[4]=='O' && board[6]=='O') {
            outputArea.append("\nPlayer 2 wins");
            gameState ="Player2";
            return true;
        }

        if(turns == SIZE){
            outputArea.append("\nGame has ended in a draw");
            gameState="Draw";
            return true;
        }
        return false;
    }

    public static void main( String args[] )
    {
        TicTacToeServer application = new TicTacToeServer();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.execute();
    }

    // private inner class Player manages each Player as a thread
    private class Player extends Thread {
        private Socket connection;
        private DataInputStream input;
        private DataOutputStream output;
        private int playerNumber;
        private char mark;
        protected boolean suspended = true;

        // set up Player thread
        public Player( Socket socket, int number )
        {
            playerNumber = number;

            // specify player's mark
            mark = ( playerNumber == PLAYER_X ? X_MARK : O_MARK );

            connection = socket;

            // obtain streams from Socket
            try {
                input = new DataInputStream( connection.getInputStream() );
                output = new DataOutputStream( connection.getOutputStream() );
            }

            // process problems getting streams
            catch( IOException ioException ) {
                ioException.printStackTrace();
                System.exit( 1 );
            }

        } // end Player constructor

        // send message that other player moved
        public void otherPlayerMoved( int location )
        {
            // send message indicating move
            try {
                if(isGameOver())
                {
                    if(winner == "draw")
                        output.writeUTF(result+" "+winner);
                    else
                        output.writeUTF(result+" "+winner+" wins");
                }
                else
                {
                    output.writeUTF("Opponent moved");
                    output.writeInt(location);
                }
            }

            // process problems sending message
            catch ( IOException ioException ) {
                ioException.printStackTrace();
            }
        }

        // control thread's execution
        public void run()
        {
            // send client message indicating its mark (X or O),
            // process messages from client
            try {
                displayMessage( "Player " + ( playerNumber ==
                        PLAYER_X ? X_MARK : O_MARK ) + " connected\n" );

                output.writeChar( mark ); // send player's mark

                // send message indicating connection
                output.writeUTF( "Player " + ( playerNumber == PLAYER_X ?
                        "X connected\n" : "O connected, please wait\n" ) );

                // if player X, wait for another player to arrive
                if ( mark == X_MARK ) {
                    output.writeUTF( "Waiting for another player" );

                    // wait for player O
                    try {
                        synchronized( this ) {
                            while ( suspended )
                                wait();
                        }
                    }

                    // process interruptions while waiting
                    catch ( InterruptedException exception ) {
                        exception.printStackTrace();
                    }

                    // send message that other player connected and
                    // player X can make a move
                    output.writeUTF( "Other player connected. Your move." );
                }

                while (replayGame) {
                    // while game not over
                    while (!isGameOver())
                    {
                        // get move location from client
                        int location = input.readInt();

                        // check for valid move
                        if (validateAndMove(location, playerNumber)) {
                            displayMessage("\nlocation: " + location);
                            output.writeUTF("Valid move.");
                            cnt++;
                        } else
                            output.writeUTF("Invalid move, try again");
                    }
                    if (winner == "draw")
                    {
                        displayMessage("\n" + result + winner);
                        output.writeUTF(result + " " + winner);
                        waitForBtn(input);
                    } else
                    {
                        displayMessage("\n" + result + winner + " wins");
                        output.writeUTF(result + " " + winner + " wins");
                        waitForBtn(input);
                    }
                }
                connection.close(); // close connection to client
            }// end try

            // process problems communicating with client
            catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
        }

        // set whether or not thread is suspended
        public void setSuspended( boolean status )
        {
            suspended = status;
        }

    } // end class Player
    public void waitForBtn(DataInputStream in){
        try {
            while(reset == false) {
                if (replayBtn == false) {
                    players[currentPlayer].output.writeUTF("reset");
                    currentPlayer = (currentPlayer + 1) % 2;
                    players[currentPlayer].output.writeUTF("reset");
                    replayBtn = true;
                }
                if (in.readInt() == 1) {
                    for (int i = 0; i < 9; i++) {
                        board[i] = ' ';
                        cnt = 0;
                        replayBtn = false;
                        reset = true;
                    }
                    currentPlayer = (currentPlayer + 1) % 2;
                    players[currentPlayer].output.writeUTF("reset2");
                    reset = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

} // end class TicTacToeServer

/**************************************************************************
 * (C) Copyright 1992-2003 by Deitel & Associates, Inc. and               *
 * Prentice Hall. All Rights Reserved.                                    *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
