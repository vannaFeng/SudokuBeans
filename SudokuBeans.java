import java.util.*;

public class SudokuBeans {

        private int[][] board;
        //    private String difficultyLevel;
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
//            for( int col = 0; col < size; col++ ) {
//                this.board[row][col] = this.solution[row][col];
//            }
            } //the user won't directly access the solution (making adjustments)

            Random generateClue = new Random();
            int preFilledCluesAmount = 0;

            if (difficultyLevel.equalsIgnoreCase("Difficult"))
                preFilledCluesAmount = generateClue.nextInt(20) + 17;
            else if (difficultyLevel.equalsIgnoreCase("Intermediate"))
                preFilledCluesAmount = generateClue.nextInt(20) + 37;
            else if (difficultyLevel.equalsIgnoreCase("Easy"))
                preFilledCluesAmount = generateClue.nextInt(21) + 57;

//        for (int row = 0; row < size; row++)
//            for (int col = 0; col < size; col++) {
//                this.board[row][col] = boardWithSolution[row][col];
//            }

//        for( int row = 0; row < size; row++ ) {
//            for( int col = 0; col < size; col++ ) {
//                this.board[row][col] = this.boardWithSolution[row][col];
//            }
//        }

            List<int[]> cluesPos = new ArrayList<>();
            for (int row = 0; row < size; row++)
                for (int col = 0; col < size; col++) {
                    cluesPos.add(new int[]{row, col}); // pos of the clues in a form of an array
                }
            Collections.shuffle(cluesPos);

//        int removeCluesAmount = 81 - preFilledCluesAmount;
//        for(int i = 0; i < removeCluesAmount; i++) {
//            int[] thePos = cluesPos.get(i);
//            this.board[thePos[0]][thePos[1]] = 0;
//        }
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
//                int clue = this.board[row][col];
//                System.out.print( ( clue==0 ? "." : clue ) + " " );
                    int value = this.board[row][col];
                    if( value == 0 ) {
                        System.out.print( ". " );
                    }
                    else if( isClue[row][col] ) {
                        System.out.print( value + " " );
//                    System.out.print( BLUE_CLUE + value + RESET_FONT + " " );
                    }
                    else {
                        System.out.print( BLUE_FONT + value + RESET_FONT + " " );
//                    System.out.print( value + " " );
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

//    public void playAndCheck() {
//        Scanner readKybd = new Scanner(System.in);
//        while( true ) {
////            want to add clearScreen(), tho not mandatory
//            printBoard();
//            printRemainingNumbers();
//            String rowInput, colInput, numInput;
//            System.out.println( "Input 'Hint' for extra help, 'Undo' to erase previous move, 'Exit' to quit game." );
//            System.out.println( "Select numbers from 1-9 when making a move.\n" );
//            System.out.println( "\nMake your move :3" );
//
//            System.out.print( "Row: " );
//            rowInput = readKybd.next().trim();
//            if( rowInput.equalsIgnoreCase("Exit") ) {
//                System.out.println( "Game Ended... Thank you for playing :)" );
//                break;
//            }
//            if( rowInput.equalsIgnoreCase("Undo") ) {
//                undo();
//                continue;
//            }
//            if( rowInput.equalsIgnoreCase("Hint") ) {
//                getHint();
//                continue;
//            }
//
//            System.out.print( "Column: " );
//            colInput = readKybd.next().trim();
//            if( colInput.equalsIgnoreCase("Exit") ) {
//                System.out.println( "Game Ended... Thank you for playing :)" );
//                break;
//            }
//            if( colInput.equalsIgnoreCase("Undo") ) {
//                undo();
//                continue;
//            }
//            if( colInput.equalsIgnoreCase("Hint") ) {
//                getHint();
//                continue;
//            }
//
//            System.out.print( "Number: " );
//            numInput = readKybd.next().trim();
//            if( numInput.equalsIgnoreCase("Exit") ) {
//                System.out.println( "Game Ended... Thank you for playing :)" );
//                break;
//            }
//            if( numInput.equalsIgnoreCase("Undo") ) {
//                undo();
//                continue;
//            }
//            if( numInput.equalsIgnoreCase("Hint") ) {
//                getHint();
//                continue;
//            }
//
//            System.out.println();
//
//            try {
//                int row = Integer.parseInt(rowInput)-1;
//                int col = Integer.parseInt(colInput)-1;
//                int num = Integer.parseInt(numInput);
//
//                if( row < 0 || row >= size || col < 0 || col >= size || num < 1 || num > 9 ) {
//                    if(row < 0 || row >= size)
//                        System.out.println("Invalid row input.");
//                    if(col < 0 || col >= size)
//                        System.out.println("Invalid column input.");
//                    if(num < 1 || num > 9)
//                        System.out.println("Invalid number input.");
//                    System.out.println( "Input must be from 1-9." );
//                    continue;
//                }
//                if(isClue[row][col]) {
//                    System.out.println( "This is a clue. Try a different one." );
//                    continue;
//                }
//
//                if( this.board[row][col] != 0 ) {
//                    System.out.println( "This cell is already used. Try again." );
//                    continue;
//                }
//                if( !isLegalForPlacement( this.board, row, col, num ) ) {
//                    System.out.println( "Incorrect input placement. Try again." );
//                    this.mistakes++;
//                    continue;
//                }
//
//                this.board[row][col] = num;
//                this.moveHistory.push(new Move(row, col, num));
//                this.numUsages[num-1]++;
//
//                if( isBoardComplete() ) {
//                    printBoard();
//                    System.out.println( "You solved the puzzle! YIPPEE!" );
//                    System.out.println( ":))))))))" );
//                    break;
//                }
//
//            } catch(NumberFormatException e) {
//                System.out.println("Please only input valid numbers :) \n");
//            }
//        }
//    }






//    public void playAndCheck2() {
//        Scanner readKybd = new Scanner(System.in);
//
//        while(true) {
//            printBoard();
//            printRemainingNumbers();
//
//            System.out.println( "Input 'Undo' to erase previous move, 'Exit' to quit game." );
//            System.out.println( "Select numbers from 1-9 when making a move.\n" );
//            System.out.println( "\nMake your move :3" );
//
//            int row = getValidInput( readKybd, "Row" );
//            if(row == 0) break;
//            if(row == 1) continue;
//
//            int col = getValidInput( readKybd, "Column" );
//            if(col == 0) break;
//            if(col == 1) continue;
//
//            if(isClue[row][col]) {
//                System.out.println( "This is a clue. Try a different one." );
//                continue;
//            }
//            if( this.board[row][col] != 0 ) {
//                System.out.println( "This cell is already used. Try again." );
//                continue;
//            }
//
//            int num = getValidInput( readKybd, "Number" );
//            if(num == 0) break;
//            if(num == 1) continue;
//
//            if( !isValidForSolution( this.board, row, col, num ) ) {
//                System.out.println( "Incorrect input placement. Try again." );
//                this.mistakes++;
//                continue;
//            }
//
//            this.board[row][col] = num;
//            this.moveHistory.push(new Move(row, col, num));
//            this.numUsages[num-1]++;
//
//            if( isBoardComplete() ) {
//                printBoard();
//                System.out.println( "You solved the puzzle! YIPPEE!" );
//                System.out.println( ":))))))))" );
//                break;
//            }
//        }
//    }
//    private int getValidInput( Scanner readKybd, String prompt ) {
//        int min = 1;
//        int max = 9;
//        while(true) {
//            System.out.print( prompt + ": " );
//            String input = readKybd.next().trim();
//
//            if( input.equalsIgnoreCase("Exit") ) {
//                System.out.println( "Game Ended... Thank you for playing :)" );
//                return 0;
//            }
//            if( input.equalsIgnoreCase("Undo") ) {
//                undo();
//                return 1;
//            }
////            if( input.equalsIgnoreCase("Hint") ) {}
//            try {
//                int value = Integer.parseInt(input)-1;
//                if( value < min-1 || value > max-1 ) {
////                    System.out.println();
//                    continue;
//                }
//                return value;
//            } catch(NumberFormatException e) {
//                System.out.println("Please only input valid numbers :) \n");
//            }
//        }
//    }




//            if( (rowInput.length() != 1) || (colInput.length() != 1) || (numInput.length() != 1) ||
//                    (rowInput.equals("0")) || (colInput.equals("0")) || (numInput.equals("0")) ) {
//                System.out.println( "Invalid input... please try again" );
//                continue;
//            }


//            if( rowInput.equalsIgnoreCase("Exit") ||
//                    colInput.equalsIgnoreCase("Exit") ||
//                    numInput.equalsIgnoreCase("Exit") ) {
//                System.out.println( "Game Ended... Thank you for playing :)" );
//                break;
//            }

//                if( clue == 0 )
//                    System.out.print(".");
//                else
//                    System.out.print(clue);
//                System.out.print(" ");


//***/
// +-------+-------+-------+
// | 1 2 3 | . . . | 7 8 9 |
// | 1 2 3 | . . . | 7 8 9 |
// | 1 2 3 | . . . | 7 8 9 |
// +-------+-------+-------+
// | 1 2 3 | 4 5 6 | 7 8 9 |
// | 1 2 3 | 4 5 6 | 7 8 9 |
// | 1 2 3 | 4 5 6 | 7 8 9 |
// +-------+-------+-------+
// | 1 2 3 | 4 5 6 | 7 8 9 |
// | 1 2 3 | 4 5 6 | 7 8 9 |
// | 1 2 3 | 4 5 6 | 7 8 9 |
// +-------+-------+-------+

//    private int[][] board;
//    //private final boolean[][] preFilledClues;
//    private String difficultyLevel;
//    private final int size = 9;
//
//    public Sudoku(String difficultyLevel) {
////        this.board = new int[size][size];
//        generateRandomBoard(difficultyLevel);
//    }
//    private void generateRandomBoard(String difficultyLevel) {
//        this.board = new int[size][size];
//        Random randomNum = new Random();
//        int preFilledCellsAmount = 0;
//        if( difficultyLevel.equals("Hard") || difficultyLevel.equals("Difficult") )
//            preFilledCellsAmount = randomNum.nextInt(20)+17;
//        else if( difficultyLevel.equals("Medium") || difficultyLevel.equals("Intermediate") )
//            preFilledCellsAmount = randomNum.nextInt(20)+37;
//        else if( difficultyLevel.equals("Easy") )
//            preFilledCellsAmount =randomNum.nextInt(21)+57;
//
//        int filled = 0;
//        while( filled < preFilledCellsAmount ) {
//            int row = randomNum.nextInt(size);
//            int column = randomNum.nextInt(size);
//            int clue = randomNum.nextInt(size)+1;
//
//
//        }
//    }


//    public Sudoku(String difficulty) {
//        this.board = new int[size][size];
//        this.preFilledClues = new boolean[size][size];
//        this.difficultyLevel = difficulty;
//        generateRandomBoard();
//    }
//    private void generateRandomBoard() {
//        int cellAmountFilled = 0;
//        Random randomNum = new Random();
//        if( difficultyLevel.equals("Hard") )
//            cellAmountFilled = randomNum.nextInt(20) + 17;
//        else if( difficultyLevel.equals("Intermediate"))
//            cellAmountFilled = randomNum.nextInt(20) + 37;
//        else if ( difficultyLevel.equals("Easy"))  // at least 57 clues, at most 77
//            cellAmountFilled = randomNum.nextInt(21) + 57;
//
//        int filled = 0;
//        while( filled < cellAmountFilled ) {
//            int row = randomNum.nextInt(size);
//            int col = randomNum.nextInt(size);
//            int randomClue = randomNum.nextInt(size+1);
//
//            if(board[row][col] == 0 && randomClue != 0 && isLegal(row, col, randomClue)) {
//                board[row][col] = randomClue;
//                preFilledClues[row][col] = true; // mark as clue
//                filled++;
//            }
//        }
//    }
//    private boolean validateBoard(int[][] board) {
//        for(int row = 0; row < size; row++)
//            for(int col = 0; col < size; col++)
//                if(board[row][col] == 0) {
//                    for(int num = 1; num <= 9; num++)
//                        if(isLegal(row, col, num, board)) {
//                            board[row][col] = num;
//                            if(validateBoard(board))
//                                return true;
//                            board[row][col] = 0;
//                        }
//                    return false;
//                }
//        return true;
//    }
//    private void clearBoard() {
//        for( int i = 0; i < size; i++ ) {
//            for( int j = 0; j < size; j++ ) {
//                this.board[i][j] = 0;
//                this.preFilledClues[i][j] = false;
//            }
//        }
//    }
//    public void printBoard() {
//        for( int i = 0; i < size; i++ ) {
//            if( i%3 == 0 && i != 0 ) {
//                System.out.println( "------+------+------" );
//            }
//            for( int j = 0; j < size; j++) {
//                if( j%3 == 0 && j != 0) {
//                    System.out.println( "| " );
//                }
//                System.out.print(board[i][j] == 0 ? ". " : board[i][j] + " ");
//            }
//            System.out.print('\n');
//        }
//    }
//
//    public boolean isLegal(int row, int column, int num, int[][] board) {
//        for( int i = 0; i < size; i++ ) {
//            if( board[row][i] == num || board[i][column] == num)
//                return false;
//        }
//        int boxRow = row - row%3;
//        int boxCol = column - column%3;
//
//        for( int i = boxRow; i < boxRow+3; i++ )
//            for( int j = boxCol; j < boxCol+3; j++ )
//                if( board[i][j] == num )
//                    return false;
//
//        return true;
//    }
//
////    public boolean isSolved() {
////        for( int row = 0; row < size; row++ ) {
////            for( int col = 0; col < size; col++) {
////                if(board[row][col] == 0)
////                    return false;
////                int numHolder = board[row][col];
////                board[row][col] = 0;
////                if(!isLegal(row, col, numHolder)) {
////                    board[row][col] = numHolder;
////                    return false;
////                }
////            }
////        }
////        return true;
////    }
//    public int[][] getBoard() {
//        return board;
//    }
//    public int[][] getBoardCopy() {
//        int[][] copy = new int[size][size];
//        for( int i = 0; i < size; i++ )
//            System.arraycopy(board[i], 0, copy[i], 0, size);
//        return copy;
//    }
//    public void setCell(int row, int col, int num) {
//        if(!preFilledClues[row][col])
//            board[row][col] = num;
//    }
//    public int getCell(int row, int col) {
//        return board[row][col];
//    }
//
//    public static void main( String [] args ) {
//        Scanner readKybd = new Scanner( System.in );
//
//        System.out.print("Choose your choice of difficulty: Easy | Intermediate | Hard");
//        String difficulty = readKybd.next();
//        Sudoku sudoku = new Sudoku( difficulty );
//
//        System.out.println("Generate Sudoku Board:");
//        sudoku.printBoard();
//    }





//    public void printBoard() {
//        Integer emptySpace = null;
//        System.out.println( "------+------+------" );
//        for( int i = 0; i < 9; i++ ) {
//            if( i%3 == 0 && i != 0 )
//                System.out.println("------+------+------");
//            for( int j = 0; j < 9; j++) {
//                if( j%3 == 0 && j != 0 ) {
//                    System.out.print( "| " );
//                }
//                if( board[i][j] == emptySpace ) {
//                    System.out.print(". ");
//                }
//                else {
//                    System.out.print();
//                }
//            }
//        }
//    }
}
