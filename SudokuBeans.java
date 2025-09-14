import java.util.*;

public class SudokuBeans {

        private int[][] board;
        private final int size = 9;
        private int mistakes = 0;
        private int[] numUsages = new int[size];
        private boolean[][] isClue = new boolean[size][size];

        public SudokuBeans(String difficultyLevel ) {
            generateBoard(difficultyLevel);
        }
        private int[][] solution;
        private void generateBoard( String difficultyLevel ) {
            this.board = new int[size][size];
            int[][] boardWithSolution = new int[size][size];
            checkSolution(boardWithSolution);

            this.solution = new int[size][size];
            for( int row = 0; row < size; row++ ) {
                System.arraycopy(boardWithSolution[row], 0, this.solution[row], 0, size);
            } //extra copy of the solution not for the users

            for( int row = 0; row < size; row++ ) {
                System.arraycopy(this.solution[row], 0, this.board[row], 0, size);
            } //the user won't directly access the solution (making adjustments)

            Random generateClue = new Random();
            int preFilledCluesAmount = 0;

            if (difficultyLevel.equalsIgnoreCase("Difficult"))
                preFilledCluesAmount = generateClue.nextInt(20) + 17;
            else if (difficultyLevel.equalsIgnoreCase("Intermediate"))
                preFilledCluesAmount = generateClue.nextInt(20) + 37;
            else if (difficultyLevel.equalsIgnoreCase("Easy"))
                preFilledCluesAmount = generateClue.nextInt(21) + 57;

            List<int[]> cluesPos = new ArrayList<>();
            for (int row = 0; row < size; row++)
                for (int col = 0; col < size; col++) {
                    cluesPos.add(new int[]{row, col}); // pos of the clues in a form of an array
                }
            Collections.shuffle(cluesPos);

            for( int row = 0; row < size; row++ ) {
                for( int col = 0; col < size; col++ ) {
                    this.isClue[row][col] = true;
                }
            }
            int removeCluesAmount = 81 - preFilledCluesAmount;
            for(int i = 0; i < removeCluesAmount; i++) {
                int[] thePos = cluesPos.get(i);             // the order of the pos are already randomized above
                this.board[thePos[0]][thePos[1]] = 0;       // thePos[0] reps row, thePos[1] reps col (position row, position col)
                this.isClue[thePos[0]][thePos[1]] = false;
            }

            Arrays.fill( this.numUsages, 0 );
            for( int row = 0; row < size; row++ )
                for( int col = 0; col < size; col++ ) {
                    int preFilledClue = this.board[row][col];
                    if( preFilledClue != 0 )
                        this.numUsages[preFilledClue-1]++;  // the numbers 1-9 won't be printed if the values (in the array) reach 9
                }
        }
        private boolean checkSolution( int[][] boardWithSolution ) {
            for( int row = 0; row < size; row++ ) {
                for( int col = 0; col < size; col++ ) {
                    if( boardWithSolution[row][col] == 0 ) { //cell is empty or not
                        List<Integer> clues = getRandomOrder();
                        for( int clue : clues ) {
                            if( isLegalForPlacement(boardWithSolution, row, col, clue) ) { //checking legality for each num/clue
                                boardWithSolution[row][col] = clue;
                                if(checkSolution(boardWithSolution)) { //solves the rest of the board
                                    return true;
                                }
                                boardWithSolution[row][col] = 0; // undoes (backtrack) the placement above due to invalid placement
                            }
                        }
                        return false;
                    }
                }
            }
            return true;
        }
        private List<Integer> getRandomOrder() { //preps any order of the numbers 1-9 in a form of an "array"; i.e. 3 6 2 7...
            List<Integer> clues = new ArrayList<>();
            for( int i = 1; i <= size; i++ )
                clues.add(i);
            Collections.shuffle(clues);
            return clues;
        }
        private boolean isLegalForPlacement(int[][] board, int row, int col, int clue ) {
            for( int i = 0; i < size; i++) {
                if( board[row][i] == clue || board[i][col] == clue ) // is the same num/clue already there?
                    return false;
            }
            int boxFirstRow = (row/3)*3;
            int boxFirstCol = (col/3)*3;
            for( int j = 0; j < 3; j++ ) {
                for( int k = 0; k < 3; k++ ) {
                    if(board[boxFirstRow+j][boxFirstCol+k] == clue)  // is the same num/clue already there in the sub-box?
                        return false;
                }
            }
            return true;
        }
        public static final String RESET_FONT = "\u001B[0m";
        public static final String BLUE_FONT = "\u001B[34m";
        public void printBoard() {
            for( int row = 0; row < size; row++ ) {
                if( row%3==0 ) {
                    System.out.println("+-------+-------+-------+");
                }
                for( int col = 0; col < size; col++ ) {
                    if( col%3==0 ) {
                        System.out.print("| ");
                    }
                    int value = this.board[row][col];
                    if( value == 0 ) {
                        System.out.print( ". " );
                    }
                    else if( isClue[row][col] ) {
                        System.out.print( value + " " );
                    }
                    else {
                        System.out.print( BLUE_FONT + value + RESET_FONT + " " );
                    } // the value is from the user
                }
                System.out.print( "|\n" );
            }
            System.out.println("+-------+-------+-------+");
            System.out.println( "Mistakes: " + this.mistakes );
        }

        public void playAndCheck2() {
            Scanner readKybd = new Scanner(System.in);

            printBoard();
            while(true) {
                printRemainingNumbers();
                String rowInput, colInput, numInput;
                System.out.println( "Input 'Hint' for extra help, 'Undo' to erase previous move, 'Exit' to quit game." );
                System.out.println( "Select numbers from 1-9 when making a move.\n" );
                System.out.println( "\nMake your move :3" );

                int row;
                while(true) {
                    System.out.print( "Row: " );
                    rowInput = readKybd.next().trim();
                    if( rowInput.equalsIgnoreCase("Exit") ) {
                        System.out.println( "Game Ended... Thank you for playing :)" );
                        return; // exit implies leaving everything behind, including method
                    }
                    if( rowInput.equalsIgnoreCase("Undo") ) {
                        undo();
                        printBoard();
                        printRemainingNumbers();
                        continue;
                    }
                    if( rowInput.equalsIgnoreCase("Hint") ) {
                        getHint();
                        printBoard();
                        printRemainingNumbers();
                        continue;
                    }

                    try {
                        row = Integer.parseInt(rowInput)-1;
                        if(row < 0 || row >= size) {
                            System.out.println("Invalid row input.");
                            continue;
                        }
                        break; // valid row, so let's leave this loop to check other info
                    } catch(NumberFormatException e) {
                        System.out.println("Please only input valid numbers :) \n");
                    }
                }

                int col;
                while(true) {
                    System.out.print( "Column: " );
                    colInput = readKybd.next().trim();
                    if( colInput.equalsIgnoreCase("Exit") ) {
                        System.out.println( "Game Ended... Thank you for playing :)" );
                        return;
                    }
                    if( colInput.equalsIgnoreCase("Undo") ) {
                        undo();
                        printBoard();
                        printRemainingNumbers();
                        continue;
                    }
                    if( colInput.equalsIgnoreCase("Hint") ) {
                        getHint();
                        printBoard();
                        printRemainingNumbers();
                        continue;
                    }

                    try {
                        col = Integer.parseInt(colInput)-1;
                        if(col < 0 || col >= size) {
                            System.out.println("Invalid column input.");
                            continue;
                        }
                        break;
                    } catch(NumberFormatException e) {
                        System.out.println("Please only input valid numbers :) \n");
                    }
                }

                int num;
                while(true) {
                    System.out.print( "Number: " );
                    numInput = readKybd.next().trim();
                    if( numInput.equalsIgnoreCase("Exit") ) {
                        System.out.println( "Game Ended... Thank you for playing :)" );
                        return; // exit implies leaving everything behind, including method
                    }
                    if( numInput.equalsIgnoreCase("Undo") ) {
                        undo();
                        printBoard();
                        printRemainingNumbers();
                        continue;
                    }
                    if( numInput.equalsIgnoreCase("Hint") ) {
                        getHint();
                        printBoard();
                        printRemainingNumbers();
                        continue;
                    }

                    try {
                        num = Integer.parseInt(numInput);
                        if(num < 1 || num > size) {
                            System.out.println("Invalid number input.");
                            continue;
                        }
                        break; // valid row, so let's leave this loop to check other info
                    } catch(NumberFormatException e) {
                        System.out.println("Please only input valid numbers :) \n");
                    }
                }

                if(isClue[row][col]) {
                    System.out.println( "This is a clue. Try a different one." );
                    continue;
                }

                if( this.board[row][col] != 0 ) {
                    System.out.println( "This cell is already used. Try again." );
                    continue;
                }
                if( !isLegalForPlacement( this.board, row, col, num ) ) {
                    System.out.println( "Incorrect input placement. Try again." );
                    this.mistakes++;
                    continue;
                }

                this.board[row][col] = num;
                this.moveHistory.push(new Move(row, col, num));
                this.numUsages[num-1]++;

                printBoard();

                if( isBoardComplete() ) {
                    printBoard();
                    System.out.println( "You solved the puzzle! YIPPEE!" );
                    System.out.println( ":))))))))" );
                    break;
                }
            }
        }
        private void getHint() {
            List<int[]> emptyCells = new ArrayList<>(); // int[] reps positions

            for( int row = 0; row < size; row++ ) {
                for( int col = 0; col < size; col++ )
                    if( this.board[row][col] == 0 && !this.isClue[row][col] ) //filtering out the empty cells
                        emptyCells.add(new int[]{row, col});
            }
            if( emptyCells.isEmpty() ) {
                System.out.println( "There are no available empty cells for hints." );
                return;
            }

            Random getRandomPosNum = new Random();
            int[] chosenCellForHint = emptyCells.get( getRandomPosNum.nextInt( emptyCells.size() ) );
            int row = chosenCellForHint[0];
            int col = chosenCellForHint[1];
            int theHint = this.solution[row][col];

            this.board[row][col] = theHint;
            this.moveHistory.push(new Move(row, col, theHint));
            this.numUsages[theHint-1]++;

            System.out.println( "\nHint " + theHint + " used on row " + (row+1) + " and column " + (col+1) );
        }
        private boolean isBoardComplete() {
            for( int row = 0; row < size; row++ ) {
                for( int col = 0; col < size; col++ )
                    if( this.board[row][col] == 0 )
                        return false;
            }
            return true;
        }
        private void printRemainingNumbers() {
            System.out.print( "Remaining numbers: " );
            for( int i = 0; i < this.numUsages.length; i++ ) {
                if( this.numUsages[i] == 9 ) {
                    System.out.print( ". " );
                }   // the 9 means a certain number has been used up
                else {
                    System.out.print( (i+1) + " ");
                }   // can the view the indexes as the clue(s) getting subtracted by 1
            }
            System.out.println( "\n" );
        }
        private static class Move {
            int row, col, num;
            public Move( int row, int col, int num ) {
                this.row = row;
                this.col = col;
                this.num = num;
            }
        }
        private Stack<Move> moveHistory = new Stack<>();
        private void undo() {
            if(moveHistory.isEmpty()) {
                System.out.println( "\nThere are no moves to undo." );
                return;
            }
            Move prevMove = moveHistory.pop();
            this.board[prevMove.row][prevMove.col] = 0;
            this.numUsages[prevMove.num-1]--;
            System.out.println( "\nPrevious move undo at row " + (prevMove.row+1) + " and column " + (prevMove.col+1) );
        }

        public static void main( String[] args ) {
            Scanner readKybd = new Scanner(System.in);

            System.out.println("SUDOKU: <3~UwU");
            System.out.println("Select Difficulty: Easy, Intermediate, Difficult");
            String difficulty = readKybd.next().trim();

            while( !difficulty.equalsIgnoreCase("Easy") &&
                    !difficulty.equalsIgnoreCase("Intermediate") &&
                    !difficulty.equalsIgnoreCase("Difficult") ) {
                System.out.println("Invalid difficulty, please enter Easy, Intermediate, or Difficult.");
                difficulty = readKybd.next().trim();
            }
            SudokuBeans newGame = new SudokuBeans(difficulty);
            System.out.println("\nYour SUDOKU Puzzle <3");
            newGame.playAndCheck2();
        }
}
