package ZombieTerminator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ZombieTerminator.GameConfig.*;

/**
 * Created by yicliu on 9/22/20.
 */
public class GameBoard {

    private JButton[][] chessOnBoard = new JButton[BOARD_X_SIZE][BOARD_Y_SIZE];
    private JButton zombieDescriber;
    private JButton turnDescriber;
    private Set<JButton> gameController;
    private JFrame frame = new JFrame("Zombie Terminator");
    private JPanel panel;
    private List<Zombie> zombies;
    private GameParticipant player;
    private int turnCount;
    private Random random = new Random();
    private Set<GameParticipant> brains;

    public GameBoard() {
        initializeGame();
    }

    private void initializeGameBoard() {
        frame.getContentPane();
        panel = new JPanel();
        panel.setLayout(null);
        turnCount = 0;

        zombieDescriber = new JButton();
        turnDescriber = new JButton();

        setupGridOnBoard();
        updateParticipants();
        checkPlayer();
        setUtilButtonInfo(zombieDescriber, String.format(ZOMBIE_INFO, zombies.size()), 720, 60);
        setUtilButtonInfo(turnDescriber, String.format(TURN_INFO, 0), 720, 120);
        setupResetButton(RESET, 790, 0);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        refreshJframe();
    }

    private void setupGridOnBoard() {
        for (int i = 0; i < BOARD_X_SIZE; ++i) {
            for (int j = 0; j < BOARD_Y_SIZE; ++j) {
                chessOnBoard[i][j] = new JButton();
                chessOnBoard[i][j].setPreferredSize(new Dimension(60, 60));
                chessOnBoard[i][j].setBounds(j * 60, i * 60, 60, 60);
                chessOnBoard[i][j].setFocusPainted( true );
                chessOnBoard[i][j].setOpaque(true);
                chessOnBoard[i][j].setBackground(Color.DARK_GRAY);
                chessOnBoard[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                panel.add(chessOnBoard[i][j]);
            }
        }
    }

    private void updateParticipants() {
        cleanupBoard();
        updatePlayerOnBoard();
        updateBrainOnBoard();
        updateZombieOnBoard();
        cleanUpDeadZombies();
        checkPlayer();
    }

    private void cleanupBoard() {
        for (int i = 0; i < BOARD_X_SIZE; ++i) {
            for (int j = 0; j < BOARD_Y_SIZE; ++j) {
                chessOnBoard[i][j].setText(EMPTY);
            }
        }
    }

    private void checkPlayer() {
        boolean isPlayer = false;
        for (int i = 0; i < BOARD_X_SIZE; ++i) {
            for (int j = 0; j < BOARD_Y_SIZE; ++j) {
                if (chessOnBoard[i][j].getText().equals(PLAYER)){
                    isPlayer = true;
                }
            }
        }

        if(!isPlayer) {
            if(turnCount > 0) {
                gameOver(false);
            }
            else {
                frame.remove(panel);
                initializeGame();
            }
        }
    }

    private void updateZombieOnBoard() {
        zombies.forEach(zombie -> {
            String occupant = chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].getText();
            switch(occupant) {
                case ZOMBIE:
                    chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].setText("2");
                    break;
                case EMPTY:
                    chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].setText(ZOMBIE);
                    break;
                case PLAYER:
                    chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].setText(ZOMBIE);
                    break;
                case BRAIN:
                    zombie.setHp(zombie.getHp()-1);
                    zombie.setMovable(false);
                    Predicate<GameParticipant> zombieNotEat = e -> !e.equals(new GameParticipant(zombie.getxLocation(), zombie.getyLocation()));
                    brains = brains.stream().filter(zombieNotEat).collect(Collectors.toSet());
                    if(zombie.getHp() > 0) {
                        chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].setText(ZOMBIE);
                    }
                    else {
                        chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].setText(EMPTY);
                    }
                    break;
                default :
                    int zombieCount = Integer.parseInt(occupant) + 1;
                    chessOnBoard[zombie.getxLocation()][zombie.getyLocation()].setText(Integer.toString(zombieCount));
                    break;
            }
        });
    }

    private void cleanUpDeadZombies() {
        zombies = zombies.stream().filter(zombie -> zombie.getHp() > 0 ).collect(Collectors.toList());
        if(zombies.size() == 0) {
            gameOver(true);
        }
        else {
            zombieDescriber.setText(String.format(ZOMBIE_INFO, zombies.size()));
        }
    }

    private void updatePlayerOnBoard() {
        chessOnBoard[player.getxLocation()][player.getyLocation()].setText(PLAYER);
    }

    private void updateBrainOnBoard() {
        brains.forEach(brain -> {
            String occupant = chessOnBoard[brain.getxLocation()][brain.getyLocation()].getText();
            if(occupant.equals(EMPTY)) {
                chessOnBoard[brain.getxLocation()][brain.getyLocation()].setText(BRAIN);
            }
        });
    }

    private void initializeGame() {
        initializeZombies();
        initializePlayer();
        initializeBrains();
        initializeGameBoard();
        initializeGameController();
    }

    private void setUtilButtonInfo(JButton button, String text, int xLocation, int yLocation) {
        button.setText(text);
        button.setPreferredSize(new Dimension(300, 60));
        button.setBounds(xLocation, yLocation, 300, 60);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        panel.add(button);
    }

    private void refreshJframe() {
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void setupResetButton(String text, int xLocation, int yLocation) {
        JButton jbutton  = new JButton(text);
        jbutton.setPreferredSize(new Dimension(60, 60));
        jbutton.setBounds(xLocation, yLocation,60,60);
        jbutton.setFont(new Font("Arial", Font.PLAIN, 12));
        jbutton.addActionListener(e -> handleResetButtonClick(e, this));
        panel.add(jbutton);
    }

    private JButton setupGameControllerButton(String text, int xLocation, int yLocation) {
        JButton jbutton  = new JButton(text);
        jbutton.setPreferredSize(new Dimension(60, 60));
        jbutton.setBounds(xLocation, yLocation,60,60);
        jbutton.setBackground(Color.BLUE);
        jbutton.setOpaque(true);
        jbutton.setFont(new Font("Arial", Font.PLAIN, 40));
        jbutton.addActionListener(e -> handleControllerButtonClick(e, this, jbutton.getText()));
        panel.add(jbutton);
        return jbutton;
    }

    private void handleControllerButtonClick(ActionEvent e, GameBoard gb, String move) {
        turnCount++;
        turnDescriber.setText(String.format(TURN_INFO, turnCount));
        participantMove(player, move);
        zombiesMove();
        updateParticipants();
        refreshJframe();
    }

    private void handleResetButtonClick(ActionEvent e, GameBoard gb) {
        frame.remove(panel);
        initializeGame();
    }

    private void initializeZombies() {
        zombies = new ArrayList<>();
        for(int i = 1; i<= 1; i++) {
            zombies.add(new Zombie(random.nextInt(BOARD_X_SIZE), random.nextInt(BOARD_Y_SIZE)));
        }
    }

    private void initializePlayer() {
        player = new GameParticipant(random.nextInt(BOARD_X_SIZE), random.nextInt(BOARD_Y_SIZE));
    }

    private void initializeBrains() {
        brains = new HashSet<>();
    }

    private void initializeGameController() {
        gameController = new HashSet<>();
        gameController.add(setupGameControllerButton(UP, 790, 180));
        gameController.add(setupGameControllerButton(LEFT, 730, 240));
        gameController.add(setupGameControllerButton(BRAIN, 790, 240));
        gameController.add(setupGameControllerButton(RIGHT, 850, 240));
        gameController.add(setupGameControllerButton(DOWN, 790, 300));
    }

    //Game over implementation
    private void gameOver(boolean isPlayerWin) {
        if(isPlayerWin) {
            zombieDescriber.setText(PLAYER_WIN);
        }
        else {
            zombieDescriber.setText(ZOMBIE_WIN);
        }
        refreshJframe();

        gameController.forEach(controller -> {
            controller.setEnabled(false);
        });
        frame.setEnabled(false);
    }

    private void zombiesMove() {
        zombies.forEach(zombie -> {
            if(zombie.isMovable()) {
                int rand = random.nextInt(4);
                String direction;
                switch (rand) {
                    case 0:
                        direction = UP;
                        break;
                    case 1:
                        direction = DOWN;
                        break;
                    case 2:
                        direction = LEFT;
                        break;
                    default:
                        direction = RIGHT;
                        break;
                }
                participantMove(zombie, direction);
            }

            if(zombie.getHp() < 2) {
                zombie.setMovable(!zombie.isMovable());
            }
        });
    }

    private void participantMove(GameParticipant gameParticipant, String direction) {
        switch (direction) {
            case UP:
                if(gameParticipant.getxLocation() > 0) {
                    gameParticipant.setxLocation(gameParticipant.getxLocation() - 1);
                }
                break;
            case DOWN:
                if(gameParticipant.getxLocation() < BOARD_X_SIZE - 1) {
                    gameParticipant.setxLocation(gameParticipant.getxLocation() + 1);
                }
                break;
            case LEFT:
                if(gameParticipant.getyLocation() > 0) {
                    gameParticipant.setyLocation(gameParticipant.getyLocation() - 1);
                }
                break;
            case RIGHT:
                if(gameParticipant.getyLocation() < BOARD_Y_SIZE - 1) {
                    gameParticipant.setyLocation(gameParticipant.getyLocation() + 1);
                }
                break;
            case BRAIN:
                dropBrain(gameParticipant.getxLocation(), gameParticipant.getyLocation());
                break;
        }
    }

    private void dropBrain(int xLocation, int yLocation) {
        boolean isBrainExist = false;
        for(GameParticipant brain : brains) {
            if(brain.getxLocation() == xLocation && brain.getyLocation() == yLocation) {
                isBrainExist = true;
                break;
            }
        }
        if(!isBrainExist) {
            GameParticipant brain = new GameParticipant(xLocation, yLocation);
            brains.add(brain);
        }
    }
}
