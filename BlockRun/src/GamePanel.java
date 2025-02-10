import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;



public class GamePanel extends JPanel implements ActionListener {

    public final long programStartTime = System.currentTimeMillis();

    private static int playerSize = 75;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public int WIDTH = screenSize.width;
    public int HEIGHT = screenSize.height;

    

    private static final int blockWidth = 700;
    private static final double GRAVITY = 2;
    private static final int JUMP_STRENGTH = -25;
    private static final int startHeight = 150;
    private static final int blockHeightRange = 125;
    private static final int speed = 12;

    private int backgroundX = 0;
    private int backgroundY = 0;
    private int backgroundSpeed = speed * 1/6;

    private int playerY = HEIGHT - startHeight - playerSize;
    private int playerX = WIDTH / 3;
    private double playerVelocity = 0;
    private long score = 0;
    private int scoreMultiplier = 1;
    private double condensedScore;
    private String displayedScore;
    private int deaths = 0;
    private boolean gameoverAnimation = false;
    private boolean gameOver = false;
    private int stringWidth;
    private boolean homeScreen = true;
    private boolean jumping = false;
    private boolean touchingGround = true;
    private boolean touchingWall = false;
    private int blockHeight;
    private int spikeWidth = 45;
    private int spikeHeight = 50;
    private int spikeSpawn = WIDTH;
    private int temp;
    private int timerDelay = 20;
    private int blockGenDirection;
    private int starting = 0;

    public static final Color brown = new Color(133, 91, 1);
    public static final Color lightBrown = new Color(173, 101, 0);
    public static final Color green = new Color(13, 196, 0);
    public static final Color gray = new Color(105, 105, 105);
    public static final Color purple = new Color(153, 92, 237);

    private int playButtonSize = 250;
    private int playButtonX = -playButtonSize;
    private int playButtonY;
    private Rectangle playButtonRect = new Rectangle(playButtonX, playButtonY, playButtonSize, playButtonSize);
    private Point mouseLocation;
    private int mouseX;
    private int mouseY;
    private int playButtonSmooth = 50;
    private int playButtonStartX, playButtonTargetX;
    private int playButtonAnimationDuration = 1000; // Duration in milliseconds
    private long animationStartTime;
    private boolean playButtonTouched;
    private boolean buttonAnimating = false;

    private int selectionButtonSize = 200;
    private boolean selectionScreen = false;
    private int selectionScreenX;
    private int selectionScreenY;
    private int selectionScreenStartX;
    private int selectionScreenTargetX;

    private int mapSelectionButtonSizeX = WIDTH * 9/20;
    private int mapSelectionButtonSizeY = WIDTH * 3/10;
    private int mapSelectionButtonX;
    private int mapSelectionButtonY;

    private int coins = 0;
    private float coinsFloat = 0;

    private boolean animating;

    private float loadingPercentage = -20F;
    private boolean loading = true;
    private float alpha = 1.0f; 
    private boolean fading = false;
    private boolean fadingOut = true; 
    private int stuckPoint;
    private boolean fadeDirection;

    private int tipX = -800;
    private boolean tipAnimating = false;
    private int tipTargetX = 370;
    private int tipStartX = -800;

    private int gameOverX = 4/9 * WIDTH;
    private float augh = 4F/9F;
    private int gameOverTargetX = (int)(augh * WIDTH);
    private int gameOverStartX;

    AudioInputStream audioInputStream;
    private Clip clip;
    private Clip music;
    private ArrayList<Clip> runningSounds;
    private boolean musicIsDone;
    String jumpSound = "resources/jump.au";
    String deathSound = "resources/death.au";
    String gameMusic = "resources/gamemusic.au";
    String clickSound = "resources/click.au";
    String deathMusic = "resources/deathmusic.au";
    String homeScreenMusic = "resources/homescreenmusic.au";
    String loadingSound = "resources/loadingsound.au";

    Font titlefont;
    Font regularFont;
    Font titlefontoutline;

    String[] fontNames = {"titlefont", "regularFont", "titlefontoutline"};
    Font[] fonts = {titlefont, regularFont, titlefontoutline};

    private int cloudX;
    private int cloudY;

    private String gameTitle = "Blok Run";

    private Timer timer;
    private Timer animationTimer;
    private Timer playButtonTimer;
    private Timer alphaTimer;
    private Timer volumeTimer;
    
    private ArrayList<Rectangle> backgroundRect;
    private ArrayList<Rectangle> blocks;
    private ArrayList<Polygon> spikes;
    private ArrayList<Rectangle> cloudRect;
    int[] xPoints = {WIDTH * 4 / 5, WIDTH * 4 / 5 + spikeWidth / 2, WIDTH * 4 / 5 + spikeWidth};
    int[] yPoints = {HEIGHT - startHeight, HEIGHT - startHeight - spikeHeight, HEIGHT - startHeight};
    int nPoints = 3;
    private Random random;

    private String[] characterRunningSkins = {"rock1", "rock2", "rock3", "rock4", "rock5", "rock6", "rock7", "rock8"};
    private ArrayList<Image> characterRunningImages;
    private Image tempCharacterImage;
    private double currentImageIndex = 0;
    private Image currentImage;
    private String[] characterJumpingSkins = {"jump1", "jump2", "jump3", "jump4"};
    private ArrayList<Image> characterJumpingImages;
    private String[] characterDeathSkins = {"dead1", "dead2" , "dead", "dead4", "dead5"};
    private ArrayList<Image> characterDeathImages;
    private String[] playButtonStages = {"playbuttonanimation1", "playbuttonanimation2", "playbuttonanimation3", "playbuttonanimation4", "playbuttonanimation5", "playbuttonanimation6", "playbuttonanimation7", "playbuttonanimation8", "playbuttonanimation9"};
    private ArrayList<Image> playButtonImages;
    private Image[] cloudImages = {null, null};
    private ArrayList<Image> terrainImages;
    private Image[] backgroundImages = {null, null, null};
    private Image[] spikeImages = {null, null, null};
    private Image[] plainsTerrain = {null, null, null, null};
    private Image[] desertTerrain = {null, null, null, null};
    private Image[] cityTerrain = {null, null, null, null};

    private String theme;
    private Image currentThemeBackground;
    private Image[] currentTheme = {null, null, null, null};
    private Image[] selectionIcons = {null, null, null};
    private int currentIconIndex;
    private Image currentIcon;
    private int[] nextArrowXPoints = {selectionScreenX + WIDTH - 200, selectionScreenX + WIDTH - 200, selectionScreenX + WIDTH - 200 + 80};
    private int[] nextArrowYPoints = {(HEIGHT - 80)/2, (HEIGHT - 80)/2 + 80, (HEIGHT - 80)/2 + 40};
    private int[] previousArrowXPoints = {selectionScreenX + 200, selectionScreenX + 200, selectionScreenX + 200 - 80};
    private int[] previousArrowYPoints = {(HEIGHT - 80)/2, (HEIGHT - 80)/2 + 80, (HEIGHT - 80)/2 + 40};

    private ImageIcon iib;
    private Image blockImage;
    private Image terrain;
    private Image background;
    private Image character;
    private Image scorepanel;
    private Image gameoverpanel;
    private Image spikeImage;
    private Image homescreen;
    private Image playButton;
    private Image playbuttonhover;
    private Image playButtonState;
    private Image cloud;
    private Image loadingImage;
    private Image currentTerrain;
    private Image nextArrow;
    private Image previousArrow;
    private Image biomeSelectButton;
    private Image biomeSelectButtonHover;
    private Image biomeSelectButtonState;
    private Image coin;
    private Image smallCoin;
    

           

    private void loadImages() {
        iib = new ImageIcon("resources/block.png");
        plainsTerrain[0] = iib.getImage();
        plainsTerrain[0] = plainsTerrain[0].getScaledInstance(blockWidth, (int)(337F/300F * blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/blockbone.png");
        plainsTerrain[1] = iib.getImage();
        plainsTerrain[1] = plainsTerrain[1].getScaledInstance(blockWidth, (int)(337F/300F * blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/blockruins.png");
        plainsTerrain[2] = iib.getImage();
        plainsTerrain[2] = plainsTerrain[2].getScaledInstance(blockWidth, (int)(337F/300F * blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/bridge.png");
        plainsTerrain[3] = iib.getImage();
        plainsTerrain[3] = plainsTerrain[3].getScaledInstance(blockWidth + 2, (int)(348F/705F*blockWidth), Image.SCALE_SMOOTH);
        
        iib = new ImageIcon("resources/cityblock.png");
        cityTerrain[0] = iib.getImage();
        cityTerrain[0] = cityTerrain[0].getScaledInstance(blockWidth + 2, (int)(335F/228F*blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/citypipeleft.png");
        cityTerrain[1] = iib.getImage();
        cityTerrain[1] = cityTerrain[1].getScaledInstance(blockWidth + 2, (int)(335F/224F*blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/citypiperight.png");
        cityTerrain[2] = iib.getImage();
        cityTerrain[2] = cityTerrain[2].getScaledInstance(blockWidth + 2, (int)(335F/227*blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/citysewer.png");
        cityTerrain[3] = iib.getImage();
        cityTerrain[3] = cityTerrain[3].getScaledInstance(blockWidth + 2, (int)(335F/228F*blockWidth), Image.SCALE_SMOOTH);
        
        iib = new ImageIcon("resources/desertblock.png");
        desertTerrain[0] = iib.getImage();
        desertTerrain[0] = desertTerrain[0].getScaledInstance(blockWidth + 2, (int)(335F/228F*blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/desertblock.png");
        desertTerrain[1] = iib.getImage();
        desertTerrain[1] = desertTerrain[1].getScaledInstance(blockWidth + 2, (int)(335F/224F*blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/desertblock.png");
        desertTerrain[2] = iib.getImage();
        desertTerrain[2] = desertTerrain[2].getScaledInstance(blockWidth + 2, (int)(335F/227*blockWidth), Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/desertlavapool.png");
        desertTerrain[3] = iib.getImage();
        desertTerrain[3] = desertTerrain[3].getScaledInstance(blockWidth + 2, (int)(335F/228F*blockWidth), Image.SCALE_SMOOTH);
        
        iib = new ImageIcon("resources/plainsbackground.png");
        background = iib.getImage();
        background = background.getScaledInstance(1431 / 533 * HEIGHT, HEIGHT, Image.SCALE_SMOOTH);
        backgroundImages[0] = background;
        iib = new ImageIcon("resources/desertbackground.png");
        background = iib.getImage();
        background = background.getScaledInstance(1431 / 533 * HEIGHT, HEIGHT, Image.SCALE_SMOOTH);
        backgroundImages[1] = background;
        iib = new ImageIcon("resources/neoncity.png");
        background = iib.getImage();
        background = background.getScaledInstance(1431 / 533 * HEIGHT, HEIGHT, Image.SCALE_SMOOTH);
        backgroundImages[2] = background;
        
        iib = new ImageIcon("resources/character.png");
        character = iib.getImage();
        character = character.getScaledInstance(60 + playerSize * 140 / 114, playerSize + 40, Image.SCALE_SMOOTH);
        
        iib = new ImageIcon("resources/scorepanel.png");
        scorepanel = iib.getImage();
        scorepanel = scorepanel.getScaledInstance(500, 120, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/gameoverpanel.png");
        gameoverpanel = iib.getImage();
        gameoverpanel = gameoverpanel.getScaledInstance(WIDTH * 1/2, WIDTH * 3 / 5 * 3 / 5, Image.SCALE_SMOOTH);
        
        iib = new ImageIcon("resources/spike.png");
        spikeImages[0] = iib.getImage();
        spikeImages[0] = spikeImages[0].getScaledInstance(spikeWidth, spikeHeight, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/desertspike.png");
        spikeImages[1] = iib.getImage();
        spikeImages[1] = spikeImages[1].getScaledInstance(spikeWidth, spikeHeight, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/cone.png");
        spikeImages[2] = iib.getImage();
        spikeImages[2] = spikeImages[2].getScaledInstance(spikeWidth, spikeHeight, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/homescreen.png");
        homescreen = iib.getImage();
        homescreen = homescreen.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/playbutton.png");
        playButton = iib.getImage();
        playButton = playButton.getScaledInstance(playButtonSize, playButtonSize, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/playbuttonhover.png");
        playbuttonhover = iib.getImage();
        playbuttonhover = playbuttonhover.getScaledInstance(playButtonSize, playButtonSize, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/cloud1.png");
        cloud = iib.getImage();
        cloudImages[0] = cloud.getScaledInstance(225, 75, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/cloud2.png");
        cloud = iib.getImage();
        cloudImages[1] = cloud;
        iib = new ImageIcon("resources/loadingscreen.png");
        loadingImage = iib.getImage();
        loadingImage = loadingImage.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/nextarrow.png");
        nextArrow = iib.getImage();
        nextArrow = nextArrow.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/previousarrow.png");
        previousArrow = iib.getImage();
        previousArrow = previousArrow.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/plainsicon.png");
        selectionIcons[0] = iib.getImage();
        selectionIcons[0] = selectionIcons[0].getScaledInstance(mapSelectionButtonSizeX, mapSelectionButtonSizeY, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/deserticon.png");
        selectionIcons[1] = iib.getImage();
        selectionIcons[1] = selectionIcons[1].getScaledInstance(mapSelectionButtonSizeX, mapSelectionButtonSizeY, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/cityicon.png");
        selectionIcons[2] = iib.getImage();
        selectionIcons[2] = selectionIcons[2].getScaledInstance(mapSelectionButtonSizeX, mapSelectionButtonSizeY, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/biomebutton.png");
        biomeSelectButton = iib.getImage();
        biomeSelectButton = biomeSelectButton.getScaledInstance(selectionButtonSize, selectionButtonSize, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/biomebuttonhover.png");
        biomeSelectButtonHover = iib.getImage();
        biomeSelectButtonHover = biomeSelectButtonHover.getScaledInstance(selectionButtonSize, selectionButtonSize, Image.SCALE_SMOOTH);
        iib = new ImageIcon("resources/coin.png");
        coin = iib.getImage();
        coin = coin.getScaledInstance(HEIGHT * 1/10 , HEIGHT * 1/10, Image.SCALE_SMOOTH);
        smallCoin = coin.getScaledInstance(60, 60, Image.SCALE_SMOOTH);




        for (String skin : characterRunningSkins) {
            iib = new ImageIcon("resources/characterassets/" + skin + ".png");
            tempCharacterImage = iib.getImage();
            tempCharacterImage = tempCharacterImage.getScaledInstance(65 + playerSize * 140 / 114, playerSize + 40, Image.SCALE_SMOOTH);
            characterRunningImages.add(tempCharacterImage);
            
        }

        for (String skin : characterJumpingSkins) {
            iib = new ImageIcon("resources/characterassets/" + skin + ".png");
            tempCharacterImage = iib.getImage();
            tempCharacterImage = tempCharacterImage.getScaledInstance(65 + playerSize * 152 / 119, playerSize + 40, Image.SCALE_SMOOTH);
            characterJumpingImages.add(tempCharacterImage);
        }

        for (String skin : characterDeathSkins) {
            iib = new ImageIcon("resources/characterassets/" + skin + ".png");
            tempCharacterImage = iib.getImage();
            tempCharacterImage = tempCharacterImage.getScaledInstance(65 + playerSize * 139/114, playerSize + 40, Image.SCALE_SMOOTH);
            characterDeathImages.add(tempCharacterImage);  
        }

        for (String skin : playButtonStages) {
            iib = new ImageIcon("resources/" + skin + ".png");
            tempCharacterImage = iib.getImage();
            tempCharacterImage = tempCharacterImage.getScaledInstance(playButtonSize, playButtonSize, Image.SCALE_SMOOTH);
            playButtonImages.add(tempCharacterImage);
        }


    }


    


    

    public void gameMusic() {
        try {
            
            if (homeScreen && !gameOver) {
                audioInputStream = AudioSystem.getAudioInputStream(new File(homeScreenMusic).getAbsoluteFile());
            }
            if (!gameOver && !homeScreen) {
                audioInputStream = AudioSystem.getAudioInputStream(new File(gameMusic).getAbsoluteFile());
            }
            
            music = AudioSystem.getClip();
            music.open(audioInputStream);
            runningSounds.add(music);
            music.start();
            music.loop(Clip.LOOP_CONTINUOUSLY);
            

        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Unsupported Audio File" + ex);
        } catch (LineUnavailableException ex) {
            System.out.println("Line is unavailable");
        } catch (IOException ex) {
            System.out.println("Failed import" + ex);
        }
    }

    public void playSound(String filepath) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(filepath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            runningSounds.add(clip);
            clip.start();
            
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Unsupported Audio File" + ex);
        } catch (LineUnavailableException ex) {
            System.out.println("Line is unavailable");
        } catch (IOException ex) {
            System.out.println("Failed import" + ex);
        }
    }

    public void stopAllSounds() {
        for (Clip sound : runningSounds) {
            sound.stop();
        }
        runningSounds.clear();
    }

    public void decreaseVolumeSlowly() {
        if (clip == null || !clip.isOpen()) {
            return; // Exit if the music clip is null or not open
        }

        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        final float minVolume = volumeControl.getMinimum(); // Minimum volume value
        final float maxVolume = volumeControl.getMaximum(); // Maximum volume value
        final int delay = 20; // Time between volume decreases (in milliseconds)

        // Initialize Timer to decrease volume gradually
        volumeTimer = new Timer(delay, new ActionListener() {
            float currentVolume = volumeControl.getValue();

            @Override
            public void actionPerformed(ActionEvent e) {
                currentVolume -= 0.01f; // Decrease volume incrementally

                if (currentVolume <= minVolume) {
                    currentVolume = minVolume; // Ensure volume doesn't go below minimum
                    volumeTimer.stop();
                    clip.stop(); // Stop the clip when volume reaches minimum
                }

                volumeControl.setValue(currentVolume); // Set the new volume
            }
        });

        volumeTimer.start(); // Start the timer to begin decreasing the volume
    }



     public GamePanel(JFrame frame) {

        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(new TAdapter());

        currentIconIndex = 0;
        currentIcon = selectionIcons[currentIconIndex];


        blocks = new ArrayList<>();
        backgroundRect = new ArrayList<>();  
        spikes = new ArrayList<>();   
        characterRunningImages = new ArrayList<>();
        characterJumpingImages = new ArrayList<>();
        characterDeathImages = new ArrayList<>();
        playButtonImages = new ArrayList<>();
        cloudRect = new ArrayList<>();
        terrainImages = new ArrayList<>();
        runningSounds = new ArrayList<>();

        playButtonState = playbuttonhover;
        biomeSelectButtonState = biomeSelectButton;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isPlayButtonClicked(e)) {
                    if (homeScreen && !gameoverAnimation && !buttonAnimating && !selectionScreen) {
                        timerDelay = 20;
                        fadeOut();
                        stopAllSounds();
                        startGame();
                        playSound(clickSound);
                    }

                    if (gameOver && !gameoverAnimation) {
                        timerDelay = 20;
                        gameOver = false;
                        timer.start();
                        fadeOut();
                        startGame();
                    }
                }

                if (!homeScreen && !gameOver) {
                    if (touchingGround && playerVelocity == GRAVITY) {
                        jumping = true; 
                        touchingGround = false;
                        jump();
                       
                    }
                }

                if (isBiomeSelectionClicked(e) && !selectionScreen && !animating) {
                    selectionScreen = true;
                    playSound(clickSound);
                    animateSelectionScreen();
                    currentIcon = selectionIcons[currentIconIndex];
                }
                
                if (isSkinSelectionClicked(e) && !selectionScreen && !animating) {
                    playSound(clickSound);

                }

                if (isMapIconNextClicked(e) && selectionScreen && !animating) {
                    currentIconIndex += 1;
                    if (currentIconIndex + 1 > selectionIcons.length) {
                        currentIconIndex = 0;
                    }
                    currentIcon = selectionIcons[currentIconIndex];
                }

                if (isMapIconPreviousClicked(e) && selectionScreen && !animating) {
                    currentIconIndex -= 1;
                    if (currentIconIndex < 0) {
                        currentIconIndex = selectionIcons.length - 1;
                    }
                    currentIcon = selectionIcons[currentIconIndex];
                }

                if (isMapSelectClicked(e) && selectionScreen && !animating) {
                    if (currentIcon == selectionIcons[0]) {
                        theme = "plains";
                    }
                    if (currentIcon == selectionIcons[1]) {
                        theme = "desert";
                    }
                    if (currentIcon == selectionIcons[2]) {
                        theme = "city";
                    }
                    playSound(clickSound);
                    selectionScreen = false;
                    animateSelectionScreen();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLocation = e.getPoint();
                mouseX = e.getX();
                mouseY = e.getY();
                if (isBiomeSelectionClicked(e)) {
                    biomeSelectButtonState = biomeSelectButtonHover;
                } else {
                    biomeSelectButtonState = biomeSelectButton;
                }
                repaint();
            }
        });



        loadImages();

        blocks = new ArrayList<>();
        random = new Random();
        this.spikes = new ArrayList<>();

        timer = new Timer(timerDelay, this);
        animationTimer = new Timer(timerDelay, this);

        

        try {
            
            for (String font : fontNames) {
                InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/" + font + ".ttf");
                if (stream == null) {
                    throw new IOException("Font file not found");
                }
                fonts[Arrays.asList(fontNames).indexOf(font)] = Font.createFont(Font.TRUETYPE_FONT, stream);
                
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(fonts[Arrays.asList(fontNames).indexOf(font)]);
            }

            
    
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        titlefont = fonts[0];
        regularFont = fonts[1];
        titlefontoutline = fonts[2];


    }

    private boolean isPlayButtonClicked(MouseEvent e) {
        Rectangle playButtonRect = new Rectangle(playButtonX, playButtonY, playButtonSize, playButtonSize);
        return playButtonRect.contains(e.getPoint());

    }

    private boolean isBiomeSelectionClicked(MouseEvent e) {
        Rectangle button = new Rectangle(playButtonX + playButtonSize + 100, (HEIGHT - selectionButtonSize)/2, selectionButtonSize, selectionButtonSize);
        return button.contains(e.getPoint());

    }

    private boolean isSkinSelectionClicked(MouseEvent e) {
        Rectangle button = new Rectangle(playButtonX - selectionButtonSize - 100, (HEIGHT - selectionButtonSize)/2, selectionButtonSize, selectionButtonSize);
        return button.contains(e.getPoint());

    }

    private boolean isMapIconNextClicked(MouseEvent e) {
        Polygon button = new Polygon(nextArrowXPoints, nextArrowYPoints, 3);
        return button.contains(e.getPoint());

    }

    private boolean isMapIconPreviousClicked(MouseEvent e) {
        Polygon button = new Polygon(previousArrowXPoints, previousArrowYPoints, 3);
        return button.contains(e.getPoint());

    }

    private boolean isMapSelectClicked(MouseEvent e) {
        Rectangle button = new Rectangle((WIDTH - 300)/2, (HEIGHT - 125)/2 + 325, 300, 125);
        return button.contains(e.getPoint());

    }

    private void animateButton() {

        playButtonTimer.start();
        playButtonTimer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (playButtonTouched) {
                    if (playButtonState != playButtonImages.get(playButtonImages.size() - 1) && playButtonState != playButton) {
                        for (Image skin : playButtonImages) {
                            playButtonState = skin;
                        }
                        playButtonState = playButton;
                    } else {
                        playButtonState = playButton;
                        playButtonTimer.stop();
                    }
                }
    

            }
        });
    }



    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (touchingGround && playerVelocity == GRAVITY) {
                    jumping = true; 
                    touchingGround = false;
                    jump();
                   
                }
                if (homeScreen) {
                    timerDelay = 20;
                    gameOver = false;
                    fadeOut();
                    startGame();
                }
                
                if (gameOver && !gameoverAnimation) {
                    timerDelay = 20;
                    gameOver = false;
                    timer.start();
                    terrainImages.clear();
                    fadeOut();
                    startGame();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (!homeScreen && gameOver && !gameoverAnimation) {
                    gameOver = false;
                    homeScreen = true;
                    cloudRect.clear();
                    fadeOut();
                    homeScreen();
                }

                if (homeScreen && selectionScreen && !animating) {
                    selectionScreen = false;
                    animateSelectionScreen();
                }
            }

        }
    }

    
    private void animateSelectionScreen() {
        
        if (!animating) {
            animating = true;

            if (selectionScreen == true) {
                selectionScreenStartX = 2*WIDTH;
                selectionScreenX = selectionScreenStartX;
                selectionScreenTargetX = 0;
            }
            if (selectionScreen == false) {
                selectionScreenStartX = 0;
                selectionScreenX = selectionScreenStartX;
                selectionScreenTargetX = 2*WIDTH;
            }

            playButtonStartX = (WIDTH - playButtonSize)/2;

            animationStartTime = System.currentTimeMillis();
        
            // Create and start the timer for smooth animation
            animationTimer = new Timer(5, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long elapsedTime = System.currentTimeMillis() - animationStartTime;
                    double t = Math.min(1.0, (double) elapsedTime / playButtonAnimationDuration); // Normalized time (0 to 1)
        
                    // Calculate the eased position
                    double easedT = easeOut(t);
                    selectionScreenX = (int) (selectionScreenStartX + (selectionScreenTargetX - selectionScreenStartX) * easedT);
        
                    repaint(); // Ensure the button is redrawn at its new position
                    if (selectionScreenX >= WIDTH) {
                        animating = false;
                    }
                    // Stop the timer when animation is complete
                    if (t >= 1.0) {
                        animationTimer.stop();
                    }
                }
            });
            animationTimer.start();
        }
            
    }
    
    private void animateButtonIn() {
        if (buttonAnimating == false && playButtonX < 0 && !selectionScreen) {
            
            buttonAnimating = true;
            playButtonStartX = -7 * playButtonSize;
            playButtonX = playButtonStartX;
            playButtonTargetX = (WIDTH - playButtonSize) / 2;
            animationStartTime = System.currentTimeMillis();
        
            // Create and start the timer for smooth animation
            playButtonTimer = new Timer(5, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long elapsedTime = System.currentTimeMillis() - animationStartTime;
                    double t = Math.min(1.0, (double) elapsedTime / playButtonAnimationDuration); // Normalized time (0 to 1)
        
                    // Calculate the eased position
                    double easedT = easeOut(t);
                    playButtonX = (int) (playButtonStartX + (playButtonTargetX - playButtonStartX) * easedT);
        
                    repaint(); // Ensure the button is redrawn at its new position
        
                    // Stop the timer when animation is complete
                    if (t >= 1.0) {
                        playButtonTimer.stop();
                        buttonAnimating = false;
                    }
                }
            });
            playButtonTimer.start();
        }
            
    }
    
    // easing out function
    private double easeOut(double t) {
        return 1 - Math.pow(1 - t, 6);
    }

    public void animateTip() {
        if (tipAnimating == false) {
            tipAnimating = true;
            tipStartX = tipX;
            animationStartTime = System.currentTimeMillis();
        
            // Create and start the timer for smooth animation
            animationTimer = new Timer(5, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long elapsedTime = System.currentTimeMillis() - animationStartTime;
                    double t = Math.min(1.0, (double) elapsedTime / playButtonAnimationDuration); // Normalized time (0 to 1)
        
                    // Calculate the eased position
                    double easedT = easeOut(t);
                    tipX = (int) (tipStartX + (tipTargetX - tipStartX) * easedT);
        
                    repaint(); // Ensure the button is redrawn at its new position
        
                    // Stop the timer when animation is complete
                    if (t >= 1.0) {
                        animationTimer.stop();
                    }
                }
            });
            animationTimer.start();
        }
            
    }

    public void loadAnimation() {


        loading = true;
        
        if (loadingPercentage == -20F) {
            stopAllSounds();
            stuckPoint = random.nextInt(99, 100);
            animationTimer = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (loadingPercentage < stuckPoint) {

                        loadingPercentage += 2F;

                        if (loadingPercentage == 0) {
                            stopAllSounds();
                            playSound(loadingSound);
                        }

                        repaint(); 

                    } else {
                        loadingPercentage += 0.03F;

                        if ((int)loadingPercentage == 100) {
                            fadeOut();
                            theme = "plains";
                            decreaseVolumeSlowly();

                            if (loadingPercentage >= 100.9) {
                                animationTimer.stop();
                                stopAllSounds();
                                
                            }
                        } 

                        
                    }
                }
            });
            animationTimer.start();
        }

    }

    private void fadeOut() {

        alpha = 1.0F;
    
        if (fading == false) {
            fading = true;
            alphaTimer = new Timer(10, new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                        alpha -= 0.02f;


                        if (alpha <= 0.0f) {
                            alpha = 0.0f;
                            fading = false;
                            if (homeScreen && loading) {
                                gameMusic();
                            }
                            loading = false;
                            if (!selectionScreen) {
                                animateButtonIn();
                            }
                            
                            alphaTimer.stop();
                            
                            
                        }
        
                    repaint();
                }
            });
        
            alphaTimer.start();
        }

    }
    

    public void homeScreen() {
        homeScreen = true;
        gameOver = false;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;
        if (!loading && !fading) {
            
        } 
        
        playButtonX = -playButtonSize;
        playButtonY = (HEIGHT - playButtonSize) / 2;
        playButtonState = playbuttonhover;
        biomeSelectButtonState = biomeSelectButton;
        cloudRect.clear();
        cloudX = 10;
        while (cloudX < WIDTH) {
            cloudRect.add(new Rectangle(cloudX, random.nextInt(50, HEIGHT*2/5), 0, 0));
            cloudX += 300;
        }
        
        if (!loading && !selectionScreen) {
            animateButtonIn();
            gameMusic();
        }

        timer.start();
        loadImages();
        
    }

    public void startGame() {
        
        if (theme == "plains") {
            currentTheme = plainsTerrain;
            currentThemeBackground = backgroundImages[0];
            spikeImage = spikeImages[0];
        }

        if (theme == "desert") {
            currentTheme = desertTerrain;
            currentThemeBackground = backgroundImages[1];
            spikeImage = spikeImages[1];
        }

        if (theme == "city") {
            currentTheme = cityTerrain;
            currentThemeBackground = backgroundImages[2];
            spikeImage = spikeImages[2];
        }
        
        loadImages();

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;
        homeScreen = false;

        stopAllSounds();
        gameMusic();
        


        if (!gameOver) {
            backgroundRect = new ArrayList<>();
            playerX = WIDTH / 3;
            playerY = HEIGHT - startHeight - playerSize;
            blockHeight = HEIGHT - startHeight;
            score = 0;
            blocks.clear();
            terrainImages.clear();
            spikes.clear();

            xPoints[0] = 3*blockWidth + 300;
            xPoints[1] = 3*blockWidth + 300 + spikeWidth / 2;
            xPoints[2] = 3*blockWidth + 300 + spikeWidth;
            yPoints[0] = HEIGHT - startHeight;
            yPoints[1] = HEIGHT - startHeight - spikeHeight;
            yPoints[2] = HEIGHT - startHeight;
            blocks.add(new Rectangle(0, HEIGHT - startHeight, WIDTH, startHeight));
            terrainImages.add(currentTheme[0]);
            blocks.add(new Rectangle(blockWidth, blockHeight, blockWidth, HEIGHT - blockHeight));
            terrainImages.add(currentTheme[0]);
            blocks.add(new Rectangle(2*blockWidth, blockHeight, blockWidth, HEIGHT - blockHeight));
            terrainImages.add(currentTheme[0]);
            blocks.add(new Rectangle(3*blockWidth, blockHeight, blockWidth, HEIGHT - blockHeight));
            terrainImages.add(currentTheme[0]);
            spikes.add(new Polygon(xPoints, yPoints, nPoints));
            backgroundRect.add(new Rectangle(0, 0, 1431 / 533 * HEIGHT, HEIGHT));
            addBlock(true);
            addSpike(true);
            backgroundScroll(true);

            
        }
    }

    private void addBlock(boolean start) {
        if (start) {
            if (starting <= 3) {
                starting += 1;
                blockHeight = HEIGHT - startHeight;
            } else {
                if (blocks.get(blocks.size() - 1).x < WIDTH - blockWidth + speed) {
                    boolean spawnBridge = false;
    
                    blockGenDirection = random.nextInt(1, 4);
    
                    if (blockGenDirection == 1) {
                        blockHeight = random.nextInt(blocks.get(blocks.size() - 1).y - blockHeightRange, blocks.get(blocks.size() - 1).y - 75);
                    } else if (blockGenDirection == 2) {
                        blockHeight = random.nextInt(blocks.get(blocks.size() - 1).y + 75, blocks.get(blocks.size() - 1).y + blockHeightRange);
                    } else if (blockGenDirection == 3 && terrainImages.get(terrainImages.size()-1) == currentTheme[3]) {
                        blockHeight = blocks.get(blocks.size() - 1).y;
                    }
    
                    if (terrainImages.get(terrainImages.size()-1) == currentTheme[3]) {
                        blockGenDirection = 3;
                        blockHeight = blocks.get(blocks.size() - 1).y;
                    }
    
                    if (blockGenDirection != 3) {
                        if (blocks.get(blocks.size() - 1).y + 200 >= HEIGHT - startHeight) {
                            blockHeight = random.nextInt(blocks.get(blocks.size() - 1).y - blockHeightRange, blocks.get(blocks.size() - 1).y - 75);
                        }
                        if (blocks.get(blocks.size() - 1).y - 125 <= 3 * playerSize) {
                            blockHeight = random.nextInt(blocks.get(blocks.size() - 1).y + 75, blocks.get(blocks.size() - 1).y + blockHeightRange);
                        }
                    }
    
                    int terrainChoice = random.nextInt(0, 8);
                    switch (terrainChoice) {
                        case 0:
                            if (terrainImages.get(terrainImages.size()-1) != currentTheme[3]) {
                                spawnBridge = true;
                                blockHeight = blocks.get(blocks.size() - 1).y;
                                currentTerrain = currentTheme[3];
                                terrainImages.add(currentTheme[3]);
                            } else {
                                terrainImages.add(currentTheme[random.nextInt(0, 3)]);
                            }

                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            currentTerrain = currentTheme[0];
                            terrainImages.add(currentTheme[0]);
                            break;
                        case 6:
                            currentTerrain = currentTheme[1];
                            terrainImages.add(currentTheme[1]);
                            break;
                        case 7:
                            currentTerrain = currentTheme[2];
                            terrainImages.add(currentTheme[2]);
                            break;
                    }
    
    
                    blocks.add(new Rectangle(WIDTH, blockHeight, blockWidth, HEIGHT - blockHeight));
                    yPoints[0] = blockHeight;
                    addSpike(true);
                }
            }
        }
    }
    

    private void backgroundScroll(boolean start) {
        if (start) {
            if (backgroundRect.get(backgroundRect.size() - 1).x < WIDTH - 1431 / 533 * HEIGHT + 10) {
                backgroundRect.add(new Rectangle(WIDTH, 0, 1431 / 533 * HEIGHT, HEIGHT));
            }
        }
    }

    private void addCloud(boolean start) {
        if(start) {
            if (cloudRect.get(cloudRect.size()-1).x < WIDTH - 300) {
                if (cloudRect.get(cloudRect.size()-1).y > HEIGHT * 2/5) {
                    cloudRect.add(new Rectangle(WIDTH, cloudRect.get(cloudRect.size()-1).y + random.nextInt(-400, -200), 0, 0));
                }
                if (cloudRect.get(cloudRect.size()-1).y <= HEIGHT * 1/5) {
                    cloudRect.add(new Rectangle(WIDTH, cloudRect.get(cloudRect.size()-1).y + random.nextInt(200, 400), 0, 0));
                }
                if (cloudRect.get(cloudRect.size()-1).y > HEIGHT * 1/5 && cloudRect.get(cloudRect.size()-1).y < HEIGHT * 2/5) {
                    cloudRect.add(new Rectangle(WIDTH, cloudRect.get(cloudRect.size()-1).y + random.nextInt(-400, 250), 0, 0));
                }
            
            }
                
        }
    }

    private void addSpike(boolean start) {
        int spikeAmount = random.nextInt(0, 4);
        int i = 1;
        spikeSpawn = WIDTH + random.nextInt(300, blockWidth - spikeAmount * playerSize - 15 - spikeWidth);
        nPoints = xPoints.length;

        if (start && score > 0) {
            while (i <= spikeAmount) {
                xPoints[0] = spikeSpawn;
                xPoints[1] = spikeSpawn + spikeWidth / 2;
                xPoints[2] = spikeSpawn + spikeWidth;
                yPoints[1] = yPoints[0] - spikeHeight;
                yPoints[2] = yPoints[0];
                spikes.add(new Polygon(xPoints, yPoints, nPoints));
                spikeSpawn += spikeWidth;
                i++;
            }
        }
    }

    private void paintSpike(Graphics g, Polygon spike) {
        g.setColor(Color.red);
        g.fillPolygon(spike);
    }

    private void jump() {

        playSound(jumpSound);
        jumping = true;
        touchingGround = false;
        playerVelocity = JUMP_STRENGTH;
   
    }

    private void gameOverAnimation() {


        if (!gameoverAnimation) {
            gameoverAnimation = true;
            gameOverStartX = 2*WIDTH;
            gameOverX = gameOverStartX;
            animationStartTime = System.currentTimeMillis();
        
            // Create and start the timer for smooth animation
            animationTimer = new Timer(5, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long elapsedTime = System.currentTimeMillis() - animationStartTime;
                    double t = Math.min(1.0, (double) elapsedTime / playButtonAnimationDuration); // Normalized time (0 to 1)
        
                    // Calculate the eased position
                    double easedT = easeOut(t);
                    gameOverX = (int) (gameOverStartX + (gameOverTargetX - gameOverStartX) * easedT);
        
                    repaint(); // Ensure the button is redrawn at its new position
        
                    // Stop the timer when animation is complete
                    if (t >= 1.0) {
                        animationTimer.stop();
                        gameoverAnimation = false;
                    }
                }
            });
            animationTimer.start();
        }
    }

    

    @Override
    public void actionPerformed(ActionEvent e) {

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;

        

        if (loading) {
            loadAnimation();
        }
    

        if (homeScreen && !loading) {

                
            ArrayList<Rectangle> toRemoveCloud = new ArrayList<>();
            if (mouseX > playButtonX && mouseX < playButtonX + playButtonSize && mouseY > playButtonY && mouseY < playButtonY + playButtonSize) {
                    
                playButtonTouched = true;
                animateButton();
               
            } else {
                
                playButtonTouched = false;
                playButtonState = playbuttonhover;
                
            }

            addCloud(true);

            for (Rectangle cloudRectangle : cloudRect) {
                cloudRectangle.x -= 2;
                if (cloudRectangle.x <= -400) {
                    toRemoveCloud.add(cloudRectangle);
                }
            }
            cloudRect.removeAll(toRemoveCloud);
            toRemoveCloud.clear();

    
        }

        if (!gameOver && !homeScreen) {

            if (touchingGround && !jumping) {
                
                if ((int)currentImageIndex == characterRunningImages.size() - 1) {
                    currentImageIndex = 0;
                } else {
                    currentImageIndex += 0.8;
                }
                
                currentImage = characterRunningImages.get((int) currentImageIndex);
            } else {
                currentImageIndex = 3;
                currentImage = characterJumpingImages.get((int)currentImageIndex);
            }
            

            if (score < 1000) {
                displayedScore = Long.toString(score);
            }
            if (score >= 1000 && score < 1000000) {
                condensedScore = (double) score / 1000;
                condensedScore = Math.round(condensedScore * 10.0) / 10.0;
                displayedScore = Double.toString(condensedScore) + "K";
            }
            if (score >= 1000000 && score < 1000000000) {
                condensedScore = (double) score / 1000000;
                condensedScore = Math.round(condensedScore * 10.0) / 10.0;
                displayedScore = Double.toString(condensedScore) + "M";
            }
            if (score >= 1000000000) {
                condensedScore = (double) score / 1000000000;
                condensedScore = Math.round(condensedScore * 10.0) / 10.0;
                displayedScore = Double.toString(condensedScore) + "B";
            }



            ArrayList<Rectangle> toRemoveBlock = new ArrayList<>();
            Image removedTerrain = null;

            int j = 0;
            for (Rectangle block : blocks) {
                block.x -= speed;

                if (block.x + block.width < 0) {
                    toRemoveBlock.add(block);
                    if (terrainImages.size() > j) {
                        removedTerrain = terrainImages.get(j);
                    }


                }
                j++;
            }
            blocks.removeAll(toRemoveBlock);
            toRemoveBlock.clear();
            terrainImages.remove(removedTerrain);

            addBlock(true);

            backgroundScroll(true);

            for (Rectangle block : blocks) {
                if (block.intersects(new Rectangle(playerX, playerY, playerSize - 20, (int) (playerSize + playerVelocity + 10))) && playerVelocity > 0) {
                    playerY = block.y;
                    playerVelocity = 0;
                    touchingGround = true;
                }

                if (block.intersects(new Rectangle(playerX, playerY, playerSize - 20, playerSize))) {
                    
                    
                    jumping = false;


                    playerY = block.y - playerSize;
                }
                if (block.intersects(new Rectangle(playerX, playerY, playerSize, playerSize - 10))) {
                    playerX = block.x - playerSize;
                    touchingWall = true;
                    if (playerX <= -playerSize) {
                        gameOver = true;
                    }
                }
            }

            ArrayList<Polygon> toRemoveSpikes = new ArrayList<>();

            for (Polygon spike : spikes) {
                if (spike.intersects(new Rectangle(playerX, playerY, playerSize, playerSize))) {
                    gameOver = true;
                }
                int[] xPoints = spike.xpoints;
                for (int i = 0; i < xPoints.length; i++) {
                    xPoints[i] -= speed;
                    if (spike.xpoints[0] <= -spikeWidth) {
                        toRemoveSpikes.add(spike);
                    }
                }

                spike.invalidate();
            }

            spikes.removeAll(toRemoveSpikes);
            toRemoveSpikes.clear();
                

            if (playerX <= WIDTH / 3 && !touchingWall) {
                playerX++;
            }

            ArrayList<Rectangle> toRemoveBackground = new ArrayList<>();

            for (Rectangle backRect : backgroundRect) {
                backRect.x -= backgroundSpeed;
                if (backRect.x <=  - 1431 / 533 * HEIGHT) {
                    toRemoveBackground.add(backRect);
                }
            }
            backgroundRect.removeAll(toRemoveBackground);
            toRemoveBackground.clear();

            touchingWall = false;
            playerVelocity += GRAVITY;
            playerY += playerVelocity;

            score += scoreMultiplier;
            coinsFloat += 0.1 * scoreMultiplier;
            coins = (int)(coinsFloat);
        }

        if (gameOver) { 
            gameOverAnimation();
            stopAllSounds();
            playSound(deathMusic);
            playSound(deathSound);
            gameoverAnimation = true;
            currentImage = characterDeathImages.get(3);
            currentImage = characterDeathImages.get(4);
            timer.stop();
            
        }

        repaint();
    }




    @Override
    protected void paintComponent(Graphics g) {

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (loading == true) {
            if (loadingImage != null) {

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                g2d.drawImage(loadingImage, 0, 0, this);

                g2d.setColor(Color.WHITE);

                if (loadingPercentage > 0) {
                    g2d.fillRect(WIDTH * 18 / 128, HEIGHT * 95 / 160, WIDTH * 101 / 13680  * (int) loadingPercentage, 35);
                }
                

                g2d.setFont(regularFont.deriveFont(140F));

                FontMetrics metrics = g2d.getFontMetrics();
                

                if (loadingPercentage >= 0F && loadingPercentage <= 100) {
                    int stringWidth = metrics.stringWidth((int) loadingPercentage + "%");
                    g2d.drawString((int) loadingPercentage + "%", (WIDTH - stringWidth) / 2, HEIGHT / 2 + 300);
                    if (loadingPercentage >= 100F) {
                        stringWidth = metrics.stringWidth("100%");
                        g2d.drawString("100%", (WIDTH - stringWidth) / 2, HEIGHT / 2 + 300);
                    }
                } else {
                    if (loadingPercentage < 0F) {
                        int stringWidth = metrics.stringWidth("0%");
                        g2d.drawString("0%", (WIDTH - stringWidth) / 2, HEIGHT / 2 + 300);
                    } 

                    if (loadingPercentage > 100) {
                        stringWidth = metrics.stringWidth("100%");
                        g2d.drawString("100%", (WIDTH - stringWidth) / 2, HEIGHT / 2 + 300);
                    }

                }

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
                
            }
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));

        if (homeScreen && !loading) {



            if (homescreen != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
                g2d.drawImage(homescreen, 0, 0, this);
            }

            
            for (Rectangle cloudRectangle : cloudRect) {
                cloud = cloudImages[random.nextInt(0, 1)];
                int cloudsizeX = random.nextInt(100, 300);
                
                if (cloud != null) { 
                    g2d.drawImage(cloud, cloudRectangle.x, cloudRectangle.y, this);
                }
            }
                
            

            if (titlefont != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
                g2d.setFont(titlefontoutline.deriveFont(200f));
                stringWidth = g.getFontMetrics().stringWidth(gameTitle);
                g2d.setColor(brown);
                g2d.drawString(gameTitle, WIDTH/2 - stringWidth/2 - 8, HEIGHT / 2 - 217);
                g2d.setFont(titlefont.deriveFont(200f));
                g2d.setColor(Color.WHITE);
                g2d.drawString(gameTitle, WIDTH/2 - stringWidth/2, HEIGHT / 2 - 225);
                g2d.setColor(green);
                g2d.setFont(titlefontoutline.deriveFont(200f));
                g2d.drawString(gameTitle, WIDTH/2 - stringWidth/2, HEIGHT / 2 - 225);
            } 

            if (playButtonState != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
                g2d.drawImage(playButtonState, playButtonX, playButtonY, this);
            } 

            g2d.fillRect(playButtonX - selectionButtonSize - 100, (HEIGHT - selectionButtonSize)/2, selectionButtonSize, selectionButtonSize);
            g2d.drawImage(biomeSelectButtonState, playButtonX + playButtonSize + 100, (HEIGHT - selectionButtonSize)/2, this);

            g2d.drawImage(coin, 50, 50, this);
            g2d.setColor(Color.black);
            g2d.setFont(regularFont.deriveFont(120F));
            g2d.drawString("" + coins, 100 + (int)(HEIGHT * 1/10) - 5, ((int)(HEIGHT * 1/10))/2 + 85);
            g2d.setColor(Color.WHITE);
            g2d.drawString("" + coins, 100 + (int)(HEIGHT * 1/10), ((int)(HEIGHT * 1/10))/2 + 80);

            if (selectionScreen || animating) {
                mapSelectionButtonSizeX = WIDTH * 9/20;
                mapSelectionButtonSizeY = WIDTH * 3/10;
                g2d.setColor(gray);
                g2d.fillRect(selectionScreenX, 0, WIDTH, HEIGHT);
                g2d.setColor(Color.darkGray);
                g2d.fillRect(selectionScreenX + (WIDTH - mapSelectionButtonSizeX)/2, (HEIGHT - mapSelectionButtonSizeY)/2 - 100, mapSelectionButtonSizeX, mapSelectionButtonSizeY);
                nextArrowXPoints[0] = selectionScreenX + WIDTH - 200;
                nextArrowXPoints[1] = selectionScreenX + WIDTH - 200;
                nextArrowXPoints[2] = selectionScreenX + WIDTH - 200 + 80;
                previousArrowXPoints[0] = selectionScreenX + 200;
                previousArrowXPoints[1] = selectionScreenX + 200;
                previousArrowXPoints[2] = selectionScreenX + 200 - 80;
                g2d.fillPolygon(nextArrowXPoints, nextArrowYPoints, 3);
                g2d.fillPolygon(previousArrowXPoints, previousArrowYPoints, 3);
                g2d.drawImage(nextArrow, nextArrowXPoints[0], nextArrowYPoints[0], this);
                g2d.drawImage(previousArrow, previousArrowXPoints[2], previousArrowYPoints[0], this);
                g2d.drawImage(currentIcon, selectionScreenX + (WIDTH - mapSelectionButtonSizeX)/2, (HEIGHT - mapSelectionButtonSizeY)/2 - 100, this);
                g2d.fillRect(selectionScreenX + (WIDTH - 300)/2, (HEIGHT - 125)/2 + 325, 300, 125);
                g2d.setColor(Color.RED);
                g2d.setFont(titlefont.deriveFont(70F));
                g2d.drawString("sEleCT", selectionScreenX + (WIDTH - 300)/2, (HEIGHT - 125)/2 + 400);
    
            }
            

        }



        if (!homeScreen) {
            
            g2d.setColor(Color.cyan);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            for (Rectangle backRect : backgroundRect) {
                if (currentThemeBackground != null) {
                    g.drawImage(currentThemeBackground, backRect.x, backRect.y, this);
                }
            }


            int i = 0;
            for (Rectangle block : blocks) {
                if (currentImage != null) {
                    if (!terrainImages.isEmpty() && terrainImages.size() > i) {
                        if (terrainImages.get(i) == currentTheme[3] && theme == "plains") {
                            g.drawImage(terrainImages.get(i), block.x, block.y - 170, this);
                        } else {
                            g.drawImage(terrainImages.get(i), block.x, block.y, this);
                        }
                    }
                    
                }
                i++;
            }


            for (Polygon spike : spikes) {
                int[] xPoints = spike.xpoints;
                int[] yPoints = spike.ypoints;
                if (spikeImage != null) {
                    g.drawImage(spikeImage, xPoints[0], yPoints[1], this);
                }
                
            }

            if (character != null) {
                g.drawImage(currentImage, playerX - playerSize * 140 / 228, playerY - 40, this);
            }

            if (score > 60 && score <= 200) {

                animateTip();
                tipAnimating = true;
                
                g2d.setFont(regularFont.deriveFont(50F));
                g2d.setColor(Color.BLACK);
                g2d.drawString("Press [space] to jump", tipX + 5, 655);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Press [space] to jump", tipX, 650);

            } else {
                tipX = -800;
            }


            if (gameOver && gameoverAnimation) {

                g.drawImage(currentImage, playerX - playerSize * 140 / 228, playerY - 40, this);
                
            }

            if (gameOver) {
                g2d.setColor(brown);
                if (gameoverpanel != null) {
                    g.drawImage(gameoverpanel, gameOverX, 200, this);
                }
                g2d.setFont(regularFont.deriveFont(170f));
                stringWidth = g.getFontMetrics().stringWidth("You Died!");
                g2d.drawString("You Died!", gameOverX + (WIDTH*1/2 - stringWidth)/2, HEIGHT / 2 - 100);
                g2d.setFont(regularFont.deriveFont(50f));
                stringWidth = g.getFontMetrics().stringWidth("Your Final Score Was: " + displayedScore);
                g2d.drawString("Your Final Score Was: " + displayedScore, gameOverX + (WIDTH*1/2 - stringWidth)/2, HEIGHT / 2 - 15);
                g2d.setFont(regularFont.deriveFont(50f));
                stringWidth = g.getFontMetrics().stringWidth(" +" + (int)((float)score * 0.1));
                g2d.drawImage(smallCoin, gameOverX + (WIDTH*1/2 - stringWidth)/2, HEIGHT / 2 + 15, this);
                g2d.drawString(" +" + (int)((float)score * 0.1), gameOverX + (WIDTH*1/2 - stringWidth)/2 + 60, HEIGHT / 2 + 60);
                g2d.setFont(regularFont.deriveFont(40f));
                stringWidth = g.getFontMetrics().stringWidth("Press (space) to play again or (esc) to quit");
                g2d.drawString("Press (space) to play again or (esc) to quit", gameOverX + (WIDTH*1/2 - stringWidth)/2, HEIGHT / 2 + 120);
                
            }

            if (scorepanel != null && !gameOver) {
                g.drawImage(scorepanel, 10, 10, this);
            }

            if (!gameOver) {
                g2d.setFont(regularFont.deriveFont(90f));
                g2d.setColor(brown);
                g2d.drawString("Score: " + displayedScore, 42, 97);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Score: " + displayedScore, 37, 92);
            }


            if (fading) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, WIDTH, HEIGHT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            Toolkit.getDefaultToolkit().sync();
        }

    
    }
}
