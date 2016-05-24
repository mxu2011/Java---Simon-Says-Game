
package simonsays;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


/** This program creates a Simon says game with a simple GIU. The GIU is a 3 by 3
 * square grid of buttons and there is a start button. Upon pressing the start
 * button, the program highlights a random sequence of buttons, keeping the 
 * highlight on for a second and then turning the highlight off for another
 * second. The user has to click all highlighted buttons in succession, in the
 * same order in which they were highlighted by the program. The game starts
 * with a sequence of 1 and increases by one each time the user is successful in
 * reproducing the sequence. If the user fails to reproduce the sequence,
 * another sequence of the same length is given to the user. If the user is 
 * successful, the game will display a message box congratulating the user, and
 * tells the user that by pressing start, another sequence will be given. The
 * game will continue on until the user, x's out of the window.
 *
 * @author Ryan Hilsabeck
 */

//create enumeration  for states of gameplay
enum State {SequenceGeneration, MaxLengthReached, PlayersResponse}

public class SimonSays 
{
 public static void main(String[] args) 
 {
   
   //Create new JFrame, add an instance of the MyPanel Class, set location to 
   //the center of the screen and set size of the JFrame, create the default
   //close operation and set visible for user to see
   JFrame myFrame = new JFrame("Simon Says");
   myFrame.add(new MyPanel(3), BorderLayout.CENTER);
   myFrame.setLocation(600, 300);
   myFrame.setSize(250,180);
   myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   myFrame.setVisible(true);
 }
}

//This class will create a class of JButtons that we will later on create an 
//array that will populate our MyPanel
class MyButton extends JButton
{
   public final int NUMBER;
   //Constructor
   public MyButton(int number)
   {
     NUMBER = number;
     this.setText(String.valueOf(number + 1));   
   }
   //Set Highlight method that will let us know if we need to highlight the 
   //button or if we need to dehighlight the button
   public void setHighlight(boolean flag)
   {
     if(flag)
        this.setBackground(Color.yellow);
     else
        this.setBackground(null);
   }
}
//This class will create a JPanel class. This will create a main JPanel with
//two sub JPanels(one located in the center region that will hold the array of
//MyButtons and one in the south region that will hold the start button
class MyPanel extends JPanel
{
  //Constructor
  public MyPanel(int size)
  {
        
    this.setLayout(new BorderLayout());
    this.setBorder(new EmptyBorder(5, 5, 5, 5));
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(size, size, 5, 5));
    //Variable declaration for timer
    Timer timer;
    //create a Timer object that will fire off every one second
    timer = new Timer(1000,null);
    //declare an variable that will hold and array of the MyButton class
    MyButton [] buttons;
    //create size of the MyButton array
    buttons = new MyButton[size*size];
    //create an instance of the MyButtonsTimerListener
    MyButtonsTimerListener listener = new MyButtonsTimerListener(buttons,timer);
    //this loop will create the array of buttons from the MyButtons class. It
    //will add the actionListener to each button and then add the button to the
    //central sub JPanel
    for(int k = 0; k < buttons.length; k++)
    {
      buttons[k] =new MyButton(k);
      buttons[k].addActionListener(listener);
      buttonPanel.add(buttons[k]);
    }
    //add the actionListener to the Timer object
    timer.addActionListener(listener);
    //add button panel to the Main Panel in the center region
    this.add(buttonPanel, BorderLayout.CENTER);
    JPanel startPanel = new JPanel();
    startPanel.setLayout(new FlowLayout());
    JButton startButton = new JButton("Start");
    //add instance of the StartButtonListener to the start button
    startButton.addActionListener(new StartButtonListener(timer));
    //add start button to the other sub panel that will go in the south region
    startPanel.add(startButton);
    this.add(startPanel, BorderLayout.SOUTH);
  }
}
//The startButtonListener will handle the action event when the start button is
//pushed. this time
class StartButtonListener implements ActionListener
{
  private Timer timer;
  //Constructor  
  public StartButtonListener(Timer timer)
  {
    this.timer = timer;
  }
  //Overriding the abstract actionPerformed method
  @Override
  //the actionPerformed method will start the timer
  public void actionPerformed(ActionEvent e) 
  {
    timer.start();
  }
}
//The MyButtonsTimerListener will handle the events when the timer is started
//and when it is the user's turn to play and needs to push the buttons
class MyButtonsTimerListener implements ActionListener
{
  private MyButton[] buttons;
  private Timer timer;
  private List<Integer> buttonPositions;
  private State state;
  boolean deHighlighter;
  int currentMaxLength;
  int userIndex;
  Random randy;
  int currentButton;
  //Constructor
  public MyButtonsTimerListener(MyButton[] button, Timer timer)
  {
    this.buttons = button;              
    this.timer = timer;
    //this will set the current state to sequence generation
    state = State.SequenceGeneration;
    //create new ArrayList that will hold the computer's current button sequence
    buttonPositions = new ArrayList<>();    
    //set boolean flag to false
    deHighlighter = false;
    //start of game with have the max sequence at 1
    currentMaxLength = 1;
    //userIndex will point to us which point in ArraysList we are looking at to
    //compare to what button was pushed by user 
    userIndex = 0;
    randy = new Random();
    //this will hold the current random number
    currentButton = 0;
  }
    
  @Override
  //this actionPerfomed method will go to the handletimer method if the start
  //button was just pushed and the timer was starte and if it is the user's
  //turn to push the button sequence, it will go to the handleButtons method
  public void actionPerformed(ActionEvent e) 
  {
    if(e.getSource() == timer)
      handleTimer();
    else
      handleButtons((MyButton)e.getSource());
  }
  //The handleButtons method will take the source of the current button pushed
  //by the user. If we are in PlayersResponse state, we will get the action
  //command of the button. Then we will get the first number on the first
  //button from the computer sequence from the ArrayList, we will turn that into
  //a string so we can compare the two. If they don't matche, then a message
  //dialog will pop up saying you messed up, it will give the correct sequence
  //and then you can exit message dialog, push the start button and same
  //sequence length will start again. If the userIndex equals teh size of the 
  //array minus one. That means we have gone through the whole array and the 
  //user didn't make wrong selection, so a message dialog will pop up that
  //congratulates the user and tells them to click on start again to start the
  //next sequence of the game. If there are still more buttons to compare to 
  //the computer's sequence, the index will get incremented by one and we will
  //cycle through again comparing the next button the user clicks on.
  private void handleButtons(MyButton button) 
  {
    if(state.equals(State.PlayersResponse))
    {
      String actionCommand = button.getActionCommand();
      Integer currentComputerIndex = buttonPositions.get(userIndex);
      String s = String.valueOf(currentComputerIndex);
          
      if(!actionCommand.equals(s))
      {
        JOptionPane.showMessageDialog(button, "You messed up!! The " +
                                    "correct sequence was " + buttonPositions +
                                    ". Restart level by clicking start.");
        //this will clear the current ArrayList so we can start anew
        buttonPositions.clear();
        //this will set the state back to SequenceGeneration
        userIndex = 0;
        state = State.SequenceGeneration;
      }
      else
      {
        if(userIndex == buttonPositions.size() - 1)
        {
          JOptionPane.showMessageDialog(button, "Congratulations!! You " +
                                    "can move to the next level by pressing " +
                                    "start.");
          buttonPositions.clear();
          currentMaxLength++;
          //This will set userIndex back to zero for next sequence
          userIndex = 0;
          state = State.SequenceGeneration;
        }
        else
          userIndex++;
      }
    }
 }
   //this method will handle the game when the timer has started from the start
   //button timer listener. If we are in the Sequence Generation state, we will
   //first check to see if the ListArray created so far is equal to the current
   //rounds currentMaxLength. If it is, then we will make sure the last button
   //hightlighted has been dehighlighted, and set the state to the 
   //MaxLengthReached state. If we still need numbers for the sequence, we will
   //change the boolean flag to true, create a random number between 0-8. We
   //will then highlight that random index in the array of MyButtons, and then 
   //we will add that button's number to the ArrayList, so we can keep track of
   //the computer's sequence. If we are not done with the sequence and the 
   //last button is still highlighted, we will then change the boolean flage
   //to false and dehighlight the button and the timer will cycle through this 
   //method again until the currentMaxLength is reached
   private void handleTimer()
   {
     if(state.equals(State.SequenceGeneration))
     {
       if(buttonPositions.size() == currentMaxLength)
       {
         deHighlighter = false;
         buttons[currentButton].setHighlight(deHighlighter);
         state = State.MaxLengthReached;
       }
       else
       {
         if(!deHighlighter)
         {
           deHighlighter = true;
           currentButton = randy.nextInt(buttons.length);
           buttons[currentButton].setHighlight(deHighlighter);
           buttonPositions.add(currentButton + 1);
         }
         else
         {
           deHighlighter = false;
           buttons[currentButton].setHighlight(deHighlighter);
         }
       } 
      }
     //Once we switch over the the MaxLengthReached state, we will enter into
     //this if statement, stop the timer, and change state over to players
     //response, so now the buttons will react when the player clicks on them.
     if(state.equals(State.MaxLengthReached))  
     {
       timer.stop();
       state = State.PlayersResponse;
     }
   }
}
    

    
