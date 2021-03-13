// Connect 4 
// Ethan Frank
// Date: 5/31/17
// This project extends the Jpanel class. In order to draw items on this panel you need use the Graphics2D's methods.

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;


public class ConnectFourPanel extends JPanel implements MouseListener{
	private static int WIDTH = 701;
	private static int HEIGHT = 601;
	
	private static int numWide = 7;
	private static int numHigh = 6;
	private byte[][] board; 
	
	private int player; // will equal 1 or 2 to represent who's turn it is. use 1 for black 2 for red
	private int turn;
	private int lastRow;
	private int lastCol;
	private int gameover = 0;//0: not, 1: someone won, 2: tie
	
	static int choice = 0;

	//This timer activates when it is the AI's turn
	Timer t = new Timer(0,new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {


			int depthLimit = 10;
			long maxTime = 500;
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			int expectedValue = 0;
			int spacesLeft = spacesLeft(board);

			// Use iterative deepening
			do {
				expectedValue = minimax(board,player,depthLimit,Integer.MIN_VALUE,Integer.MAX_VALUE);//Run minimax to calculate best move
				endTime = System.currentTimeMillis();
				depthLimit++;

				// Loop until too much time has passed,
				// Or the depth limit reaches the end of the game
			} while(endTime - startTime < maxTime && depthLimit < spacesLeft);

			System.out.println("Searched to depth: " + (depthLimit - 1));

			if (expectedValue < -50000)
			{
				System.err.println("You are probably going to lose");
			}
			else if (expectedValue > 50000)
			{
				System.err.println("Uh oh I may have to admit defeat");
			}


			makeMove(board,choice,player);//make the move
			repaint(); //redraw
			turn++; //A turn happened
			if(wins(board,player,lastRow,lastCol)){ //if it was a winning move, set gameover to WIN(1)
				gameover = 1;
			}else if(fullBoard(board)){ //if it was a tying move, set gameover to TIE(2)
				gameover = 2;
			}
			
			// if the game is not over, other player's turn
			if(gameover == 0){ 
				player = 3 - player;
			}
			
			t.stop();//end AI's turn
		}
	});
	
	public ConnectFourPanel(){
		setPreferredSize(new Dimension(WIDTH, HEIGHT)); //Set window dimensions
		this.setFocusable(true);
		this.addMouseListener(this);
		
		//Set everything to starting values
		player = 1;
		turn = 0;
		lastRow = 0;
		lastCol = 0;
		gameover = 0;
		board = new byte[numHigh][numWide];
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, WIDTH, HEIGHT); 
		
		// this code loads an image so that you can later paint it onto your component.
		// this would load a picture named blue.png that should be saved in a folder
		// named images, which should be located in your src folder.
		ImageIcon blueImage;
		ImageIcon redImage;
		
		ClassLoader cldr = this.getClass().getClassLoader();
		String imagePath = "images/blue.png";
		URL imageURL = cldr.getResource(imagePath);
		blueImage = new ImageIcon(imageURL);
		
		imagePath = "images/red.png";
		imageURL = cldr.getResource(imagePath);
		redImage = new ImageIcon(imageURL);
		
		// draws the connect four board	
		for(int r = 0; r <= board.length; r++){
			for(int c = 0; c <= board[0].length; c++){
				g2.setColor(Color.GRAY);
				g2.drawLine(c*100, 0, c*100, HEIGHT);
				g2.drawOval(c*100+5, r*100+5, 90, 90);
				g2.fillOval(c*100+9, r*100+9, 82, 82);
			}
			g2.drawLine(0, r*100, WIDTH, r*100);
		}
		
		//draw pieces
		for(int r = 0; r < board.length; r++){
			for(int c = 0; c < board[0].length; c++){
				if(board[r][c] == 1 )
					blueImage.paintIcon(this, g, c*100+10, (5-r)*100+10);
				else if(board[r][c] == 2 )
					redImage.paintIcon(this, g, c*100+10, (5-r)*100+10);
			}
		}
		
		//If someone won, say that player one
		if(gameover == 1){
			g2.setColor(Color.CYAN);
			g2.setFont(new Font(Font.SERIF, Font.BOLD, 100));
			g2.drawString("Game Over", 50, 230);
			g2.setFont(new Font(Font.SERIF, Font.BOLD, 50));
			g2.drawString("Player " + player + "wins!", 200, 280);
		}else if (gameover == 2){ //Otherwise, say it is a tie
			g2.setColor(Color.CYAN);
			g2.setFont(new Font(Font.SERIF, Font.BOLD, 100));
			g2.drawString("Tie!", 263, 230);
		}

	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	
		//if the game is not over
		if(gameover == 0){
			//get column they clicked in
			int c = e.getX()/100; 
			
			//if that column isn't full
			if(!columnFull(board,c)){ 
				makeMove(board,c,player); // put the piece in
				repaint(); //redraw
				turn++; // a turn happened
				//If the move made them win set gameover to WIN(1)
				//if they tied set gameover to TIE(2)
				if(wins(board,player,lastRow,lastCol)){
					gameover = 1;
				}else if(fullBoard(board)){
					gameover = 2;
				}
				
				//If the game isn't over, it is the other player's turn
				if(gameover == 0){
					player = 3 - player;
					t.start(); //Make the AI take it's turn
				}
				
			}	
		}
	}

	//Method: columnFull
	//Description: Takes in byte[][] board and int c (column)
	//If the column is full, returns true. Otherwise returns false
	public boolean columnFull(byte[][] board, int c){
		return board[numHigh-1][c] != 0;//the square at the top of the row is full
	}

	//Method: fullBoard
	//Description: Takes in byte[][] board. If the board is full returns true.
	//Otherwise returns false.
	public boolean fullBoard(byte[][] board){
		//Check if any column is not full
		for(int c = 0; c<numWide; c++){
			if(!columnFull(board,c)) return false;
		}
		//otherwise, board is full
		return true;
	}

	//Method: spacesLeft
	//Description: Returns how many more moves are to be played in the game
	public int spacesLeft(byte[][] board)
	{
		int movesLeft = 0;
		for (int c = 0; c < numWide; c++)
		{
			for (int r = numHigh - 1; r >= 0; r--)
			{
				if (board[r][c] == 0)
				{
					movesLeft ++;
				}
				else
				{
					break;
				}
			}
		}
		return movesLeft;
	}
	
	//Method: makeMove
	//Description: Takes in byte[][] board, int move (column to put piece in),
	//and int player (player making the move)
	//Puts the player's move into the array at the location it goes in. (I assume
	// you know how to play connect 4).
	//Returns the row they went in
	public int makeMove(byte[][] board, int move, int player){
		lastCol = move; //record the column they went in
		//find first empty space, put piece there
		for(int r = 0; r<numHigh; r++){
			if(board[r][move] == 0){
				board[r][move] = (byte) player;
				lastRow = r; //record the row they went in
				return r;
			}
		}
		return -1;
	}

	//Method: wins(4 input)
	//Inputs: byte[][] board, int player, int row, int col
	//Description: If the specified player has won the game
	//returns true. Otherwise, returns false.
	//Only checks for wins in the row and column
	public static boolean wins(byte[][] board, int player, int row, int col){
		int counter = 0;
		//check for vertical wins
		if(row>=3){//make sure there is enough space below to actually win
			for(int r = row; r>=0; r--){
				if(board[r][col] == player){
					counter++;
					if(counter == 4)return true;
				}else{
					counter=0;
				}	
			}
		}
		
		//check for horizontal wins
		if(board[row][3] == player){//to make 4 in a row the middle col has to be filled
			counter = 0;
			for(int c = 0; c<numWide; c++){
				if(board[row][c] == player){
					counter++;
					if(counter == 4)return true;
				}else{
					counter=0;
				}	
			}
		}	
		
		//Check diagonal (right and up)
		int left = 0;
		int right = 0;
		int num = row-col; //for right and up, each diagonal is defined by row-col
		
		//calculate which column the left and right ends of the diagonal end at
		if(num>-4 && num<3){
			if(num>=0){
				left = 0;
			}else{
				left = -num;
			}
			
			if(num > -1){
				right = 5-num;
			}else{
				right = 6;
			}
			
			
			if(board[num+3][3] == player){//To win diagonal middle col has to be filled
				counter = 0;
				for(int i = left; i<=right; i++){
					if(board[num+i][i] == player){
						counter++;
						if(counter == 4)return true;
					}else{
						counter=0;
					}	
				}
			}	
		}
		
		//Check diagonals (right and down)
		
		num = row+col; //for right and down, each diagonal is defined by row+col
		
		//calculate which column the left and right ends of the diagonal end at
		if(num>2 && num<9){
			if(num<=5){
				left = 0;
			}else{
				left = num-5;
			}
			
			if(num >= 6){
				right = 6;
			}else{
				right = num;
			}

			
			if(board[num-3][3] == player){//To win diagonal middle col has to be filled
				counter = 0;
				for(int i = left; i<=right; i++){
					if(board[num-i][i] == player){
						counter++;
						if(counter == 4)return true;
					}else{
						counter=0;
					}	
				}
			}	
		}
		
		//if all the checks fail they haven't won
		return false;
	}
	
	//Method: countInARow
	//Takes in a byte[][] board, int player, and the amount in a row to look for
	//Counts how many instances of amount in a row there are
	public int countInARow(byte[][] board, int player, int amount){
			int count = 0;
			int counter = 0;
			
			//Count Vertical
			for(int r = 0; r<numHigh; r++){
				counter = 0;
				for(int c = 0; c<numWide; c++){
					if(board[r][c] == player){
						counter++;
						if(counter >= amount){count++;break;}
					}else{
						counter=0;
					}	
				}
			}
			
			//count horizontal
			for(int c = 0; c<numWide; c++){
				counter = 0;
				for(int r = 0; r<numHigh; r++){
					if(board[r][c] == player){
						counter++;
						if(counter >= amount){count++;break;}
					}else{
						counter=0;
					}	
				}
			}
			
			return count;
		}
	//Method: countInARow2
	//Takes in a byte[][] board, int player, and the amount in a row to look for
	//Counts how many instances of amount in a row there are 
	//Takes into account things like XOXX
	public int countInARow2(byte[][] board, int player, int amount){
			int count = 0;
			int counter4 = 0;
			int counter1 = 0;
			
			//Count Horizontal
			for(int r = 0; r<numHigh; r++){
				for(int c = 0; c<4; c++){
					counter4 = 0;
					counter1 = 0;
					for(int i = 0; i<4; i++){
						if(board[r][c+i] == player) counter4++;
						else if(board[r][c+i] == 0) counter1++;
					}
					if(counter4 == 3 && counter1 == 1){count++;c+=3;}
				}
			}
			
			//count vertical
			for(int c = 0; c<numWide; c++){
				for(int r = 0; r<numHigh; r++){
					if(board[r][c] == 0){
						if(r>2){
							if((board[r-1][c] + board[r-2][c] + board[r-3][c])/3.0 == player){
								count++;
							}
						}
						break;
					}
				}
			}
			
			return count;
		}
		
	//Method: score
	//Takes in a board and a depth, returns the score relative to player 2
	//Player 2 wants the lowest score. d is how many turns have passed.
	//Wants 2 wants player 1 to win as late as possible and wants to
	//win as soon as possible
	//Winner is who won the game
	public int score(byte[][] board, int player, int d, int winner){
		int score = 0;
		
		if(winner != 0){
			//Depending on the player, a winning board scores really high or really low
			score += (winner == 1?1:-1) * (100000-100*d);
		}
		
		//Get points for how may 2 and 3 in a row you have, lose point for your opponent having them
		//Opponents 3 in row weighted more heavily
		//score+=150*((player==1?1:3)*countInARow(board, 1, 2)-(player==1?3:1)*countInARow2(board, 2, 2));
		score+=500*((player==1?1:3)*countInARow2(board, 1, 3)-(player==1?3:1)*countInARow2(board, 2, 3));
		
		//If there are places where you going makes your
		//opponent win when they go in the same column
		//you lose points
		for(int c = 0; c<numWide; c++){
			if(board[4][c] == 0){
				int r = makeMove(board, c, player);
				if(!wins(board,player,lastRow,lastCol)){
					makeMove(board,c,3-player);
					if(wins(board,3-player,lastRow,lastCol)){
						score+=(player==1?-1:1)*3000;
					}
					board[lastRow][lastCol] = 0;
				}
				board[r][lastCol] = 0;
			}
		}
		
		return score;
	}
	
	public int minimax(byte[][] board, int player, int d, int alpha, int beta){
		//Do thing where it doesn't calculate move list each time
		int winner = -1; //Who won, used to do less calculations
		
		if(d == 0){
			winner = 0;
		}else if(wins(board,3-player,lastRow,lastCol)){
			winner = 3-player;
		}else if (fullBoard(board)){//board full
			winner = 0;
		}

		if(winner>=0) {return score(board,player,d,winner);}
		
		ArrayList<Integer> scores = new ArrayList<Integer>();
		ArrayList<Integer> moves = new ArrayList<Integer>(Arrays.asList(3, 4, 5, 6, 2, 1, 0));

		int winMove = 3;//start search in middle, so it goes in the middle if everything is a tie
		for(int i = 0; i<numWide; i++){
			//copy board
			byte[][] newBoard2 = new byte[board.length][board[0].length];
			for(int k = 0; k<board.length; k++){
				for(int j = 0; j<board[0].length; j++){
					newBoard2[k][j]=board[k][j];
				}
			}

			//test every move. If it is a winning move, start there to save time
			makeMove(newBoard2, i, player);
			if(wins(newBoard2,player,lastRow,lastCol)){
				winMove = i;
				//break;
				choice = i;
				//return score(newBoard2,player,d,player);
			}
		}

		// Move winning move to the front
		int moveIndex = moves.indexOf(winMove);

		Collections.swap(moves, 0, moveIndex);


		// Remove moves that are not valid
		for (int i = moves.size() - 1; i >=0; i--)
		{
			if (columnFull(board, moves.get(i)))
			{
				moves.remove(i);
			}
		}

		//for every move
		for(int i: moves){
			
			//copy board
			byte[][] newBoard = new byte[board.length][board[0].length];
			for(int k = 0; k<board.length; k++){
				for(int j = 0; j<board[0].length; j++){
					newBoard[k][j]=board[k][j];
				}
			}
			//generate board with new move
			makeMove(newBoard,i,player);
			int value = minimax(newBoard,3-player,d-1,alpha,beta);
			//prune based on alpha beta values
			if(player == 1 && value > alpha){
				alpha = value;
			}else if(player == 2 && value < beta){
				beta = value;
			}
			if(beta<=alpha){
				return value;
			}
			scores.add(value);
		}
		//if(d==0)System.out.println(scores.toString());
		
		//find max or min score
		//set choice equal to that move, when everything ends this is the move the AI will make
		//Just look up minimax algorithm, it will help better then my comments
		//Or maybe I should just get better at describing
		if(player == 1){
			int num = 0;
			num = scores.stream().reduce(Integer.MIN_VALUE, (x,y)->y>x?y:x);//max
			choice = moves.get(scores.indexOf(num));
			return num;
		}else{
			int num = 0;
			num = scores.stream().reduce(Integer.MAX_VALUE, (x,y)->y<x?y:x);//min
			choice = moves.get(scores.indexOf(num));
			return num;
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}