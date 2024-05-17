package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import engine.Game;
import engine.Player;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Cover;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;

@SuppressWarnings("serial")
public class View extends JFrame{
	private Dimension screenSize;
	private JPanel firstPanel = new JPanel();
	private JLabel firstViewPlayerLabel = new JLabel("Enter First Player Name");
	private JTextField PlayerNameTextField = new JTextField(10);
	private JButton Next = new JButton("Next");
	
	private JLabel secondViewLabel = new JLabel("");
	private JPanel secondViewButtons = new JPanel();
	private JLabel secondViewLabelInfo = new JLabel("<html>");
	
	private JLabel firstPlayerInfo = new JLabel("");
	private JLabel secondPlayerInfo = new JLabel("");
	private JPanel board = new JPanel();
	private ArrayList<JButton> boardButtons = new ArrayList<JButton>();
	private JPanel right = new JPanel();
	private JLabel turnOrderLabel = new JLabel("");
	private JPanel left = new JPanel();
	private JLabel currChampLabel = new JLabel("");
	private JPanel buttons = new JPanel();
	private JButton move = new JButton("Move");
	private JButton attack = new JButton("Attack");
	private JButton ability1 = new JButton("Ability");
	private JButton ability2 = new JButton("Ability");
	private JButton ability3 = new JButton("Ability");
	private JButton leaderAbility = new JButton("<html>Use Leader<br>Ability");
	private JButton upB = new JButton("Up");
	private JButton downB = new JButton("Down");
	private JButton rightB = new JButton("Right");
	private JButton leftB = new JButton("Left");
	private JButton endTurn = new JButton("End Turn");
	
	public View() {
		super("Marvel");
		@SuppressWarnings("unused")
		Actions action = new Actions();
		Toolkit tk=Toolkit.getDefaultToolkit();
		screenSize = tk.getScreenSize();
		setSize(screenSize.width,screenSize.height);
		setVisible(true);
		
		firstPlayerInfo.setPreferredSize(new Dimension((int) (screenSize.width*0.6),(int) (screenSize.height*0.20)));
		secondPlayerInfo.setPreferredSize(new Dimension((int) (screenSize.width*0.6),(int) (screenSize.height*0.20)));
		board.setPreferredSize(new Dimension((int) (screenSize.width*0.55),(int) (screenSize.height*0.5)));
		right.setPreferredSize(new Dimension((int) (screenSize.width*0.2),screenSize.height));
		left.setPreferredSize(new Dimension((int) (screenSize.width*0.2),screenSize.height));
		
		firstPanel.add(firstViewPlayerLabel);	
		firstPanel.add(PlayerNameTextField);
		firstPanel.add(Next);
				
		leaderAbility.setEnabled(false);
		upB.setEnabled(false);
		downB.setEnabled(false);
		rightB.setEnabled(false);
		leftB.setEnabled(false);
		
		this.add(firstPanel);
		
		this.validate();
	}
	
	public void closeThis(){
		this.setVisible(false);
	}

	class Actions implements ActionListener {
		private boolean firstDone = false;
		private String shownView = "first";
		private String firstPlayerName = "";
		private String secondPlayerName = "";
		private Game game;
		private Player firstPlayer;
		private Player secondPlayer;
		private boolean moveBoolean = false;
		private boolean attackBoolean = false;
		private boolean castDirectionalBoolean = false;
		private boolean castSingleBoolean = false;
		private boolean wait = false;
		private Ability ability;
		
		public Actions() {
			Next.addActionListener(this);
			move.addActionListener(this);
			attack.addActionListener(this);
			ability1.addActionListener(this);
			ability2.addActionListener(this);
			ability3.addActionListener(this);
			leaderAbility.addActionListener(this);
			upB.addActionListener(this);
			downB.addActionListener(this);
			rightB.addActionListener(this);
			leftB.addActionListener(this);
			endTurn.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(shownView.equalsIgnoreCase("first")){
				if(((JButton)e.getSource()).getText().equals("Next")){
					if(!firstDone){
						firstPlayerName = PlayerNameTextField.getText();
						firstDone = true;
						firstViewPlayerLabel.setText("Enter Second Player Name");
						PlayerNameTextField.setText("");
					}
					else{
						secondPlayerName = PlayerNameTextField.getText();
						emptyFirstView();
						shownView = "second";
						secondViewLabel.setText(firstPlayerName + " Choose Your First Champion.");
						firstPlayer = new Player(firstPlayerName);
						secondPlayer = new Player(secondPlayerName);
						game = new Game(firstPlayer, secondPlayer);
						try {
							Game.loadAbilities("Abilities.csv");
							Game.loadChampions("Champions.csv");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						secondView();
					}
				}
			}
			else if(shownView.equalsIgnoreCase("second")){
				if(firstPlayer.getTeam().size() <= secondPlayer.getTeam().size()){
					for(int i=0; i<Game.getAvailableChampions().size(); i++){
						if(Game.getAvailableChampions().get(i).getName().equalsIgnoreCase(((JButton)e.getSource()).getText())){
							firstPlayer.getTeam().add(Game.getAvailableChampions().get(i));
							if(secondPlayer.getTeam().size() == 0)
								secondViewLabel.setText(secondPlayerName + " Choose Your First Champion.");
							else if(secondPlayer.getTeam().size() == 1)
								secondViewLabel.setText(secondPlayerName + " Choose Your Second Champion.");
							else if(secondPlayer.getTeam().size() == 2)
								secondViewLabel.setText(secondPlayerName + " Choose Your Third Champion.");
						}
					}
				}
				else {
					for(int i=0; i<Game.getAvailableChampions().size(); i++){
						if(Game.getAvailableChampions().get(i).getName().equalsIgnoreCase(((JButton)e.getSource()).getText())){
							secondPlayer.getTeam().add(Game.getAvailableChampions().get(i));
							if(firstPlayer.getTeam().size() == 1)
								secondViewLabel.setText(firstPlayerName + " Choose Your Second Champion.");
							else if(firstPlayer.getTeam().size() == 2)
								secondViewLabel.setText(firstPlayerName + " Choose Your Third Champion.");
						}
					}
				}
				((JButton)e.getSource()).setEnabled(false);
				if(firstPlayer.getTeam().size() == 3 && secondPlayer.getTeam().size() == 3){
					emptySecondView();
					shownView = "third";
					thirdView();
				}
			}		
			else if(shownView.equalsIgnoreCase("third")){
				for(int i=0; i<firstPlayer.getTeam().size(); i++){
					if(firstPlayer.getTeam().get(i).getName().equalsIgnoreCase(((JButton)e.getSource()).getText())){
						firstPlayer.setLeader(firstPlayer.getTeam().get(i));
						shownView = "thirdSecond";
						emptySecondView();
						thirdViewSecond();
					}
				}
			}
			else if(shownView.equalsIgnoreCase("thirdSecond")){
				for(int i=0; i<secondPlayer.getTeam().size(); i++){
					if(secondPlayer.getTeam().get(i).getName().equalsIgnoreCase(((JButton)e.getSource()).getText())){
						secondPlayer.setLeader(secondPlayer.getTeam().get(i));
						shownView = "fourth";
						emptyThirdView();
						game = new Game(firstPlayer, secondPlayer);
						fourthView();
					}
				}
			}
			else if(shownView.equalsIgnoreCase("fourth")){
				if(((JButton)e.getSource()).getText().equalsIgnoreCase("Move")){
					moveBoolean = true;
					wait = true;
				}
				else if(((JButton)e.getSource()).getText().equalsIgnoreCase("Attack")){
					attackBoolean = true;
					wait = true;
				}
				else if(((JButton)e.getSource()).getText().startsWith("<html>Cast<br>")){
					String s = ((JButton)e.getSource()).getText();
					s = s.substring(14,s.length());
					for(int i=0; i<game.getCurrentChampion().getAbilities().size(); i++){
						if(s.equals(game.getCurrentChampion().getAbilities().get(i).getName()))
							ability = game.getCurrentChampion().getAbilities().get(i);
					}
					if(ability.getCastArea().equals(AreaOfEffect.SELFTARGET) || ability.getCastArea().equals(AreaOfEffect.SURROUND) || ability.getCastArea().equals(AreaOfEffect.TEAMTARGET)){
						try {
							game.castAbility(ability);
						} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						fillView();
					}
					else if(ability.getCastArea().equals(AreaOfEffect.DIRECTIONAL)){
						castDirectionalBoolean = true;
						wait = true;
					}
					else if(ability.getCastArea().equals(AreaOfEffect.SINGLETARGET)){
						castSingleBoolean = true;
						wait = true;
					}
				}
				else if(((JButton)e.getSource()).getText().equalsIgnoreCase("<html>Use Leader<br>Ability")){
					wait = false;
					try {
						game.useLeaderAbility();
					} catch (LeaderNotCurrentException | LeaderAbilityAlreadyUsedException e1) {
						JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
					}
					fillView();
				}
				else if(((JButton)e.getSource()).getText().equalsIgnoreCase("End Turn")){
					wait = false;
					game.endTurn();
					fillView();
				}
				
				if(moveBoolean){
					if(((JButton)e.getSource()).getText().equalsIgnoreCase("up")){
						
							try {
								game.move(Direction.UP);
							} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
								JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
							}
						
						moveBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("down")){
						try {
							game.move(Direction.DOWN);
						} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						moveBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("right")){
						try {
							game.move(Direction.RIGHT);
						} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("left")){
						try {
							game.move(Direction.LEFT);
						} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						wait = false;
						fillView();
					}
					else{
						upB.setEnabled(true);
						downB.setEnabled(true);
						rightB.setEnabled(true);
						leftB.setEnabled(true);
					}
				}
				else if(attackBoolean){
					if(((JButton)e.getSource()).getText().equalsIgnoreCase("up")){
						try {
							game.attack(Direction.UP);
						} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						attackBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("down")){
						try {
							game.attack(Direction.DOWN);
						} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						attackBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("right")){
						try {
							game.attack(Direction.RIGHT);
						} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						attackBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("left")){
						try {
							game.attack(Direction.LEFT);
						} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						attackBoolean = false;
						wait = false;
						fillView();
					}
					else{
						upB.setEnabled(true);
						downB.setEnabled(true);
						rightB.setEnabled(true);
						leftB.setEnabled(true);
					}
				}
				else if(castDirectionalBoolean){
					if(((JButton)e.getSource()).getText().equalsIgnoreCase("up")){
						try {
							game.castAbility(ability, Direction.UP);
						} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						castDirectionalBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("down")){
						try {
							game.castAbility(ability, Direction.DOWN);
						} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						castDirectionalBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("right")){
						try {
							game.castAbility(ability, Direction.RIGHT);
						} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						castDirectionalBoolean = false;
						wait = false;
						fillView();
					}
					else if(((JButton)e.getSource()).getText().equalsIgnoreCase("left")){
						try {
							game.castAbility(ability, Direction.LEFT);
						} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						castDirectionalBoolean = false;
						wait = false;
						fillView();
					}
					else{
						upB.setEnabled(true);
						downB.setEnabled(true);
						rightB.setEnabled(true);
						leftB.setEnabled(true);
					}
				}
				else if(castSingleBoolean){
					if(((JButton)e.getSource()).getName() != null &&(((JButton)e.getSource()).getName().length() == 1 || ((JButton)e.getSource()).getName().length() == 2)){
						int y = getYBoard(Integer.parseInt(((JButton)e.getSource()).getName()));
						int x = getXBoard(Integer.parseInt(((JButton)e.getSource()).getName()));
						try {
							game.castAbility(ability, x, y);
						} catch (NotEnoughResourcesException | AbilityUseException | InvalidTargetException | CloneNotSupportedException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
						}
						castSingleBoolean = false;
						wait = false;
						fillView();
					}
				}
				if(!wait){
					moveBoolean = false;
					attackBoolean = false;
					castDirectionalBoolean = false;
					castSingleBoolean = false;
					upB.setEnabled(false);
					downB.setEnabled(false);
					rightB.setEnabled(false);
					leftB.setEnabled(false);
				}
				if(game.checkGameOver() != null && game.checkGameOver().equals(game.getFirstPlayer())){
					closeThis();
					JOptionPane.showMessageDialog(new JFrame(), game.getFirstPlayer().getName() +" Wins");

				}
				else if(game.checkGameOver() != null && game.checkGameOver().equals(game.getSecondPlayer())){
					closeThis();
					JOptionPane.showMessageDialog(new JFrame(), game.getSecondPlayer().getName() +" Wins");

				}
			}
			

		}
		
		public void emptyFirstView(){
			firstViewPlayerLabel.setVisible(false);
			PlayerNameTextField.setVisible(false);
			Next.setVisible(false);
		}
		
		public void secondView(){
			firstPanel.setLayout(new BorderLayout());
			firstPanel.add(secondViewLabel, BorderLayout.PAGE_START);
			for(int i=0; i<Game.getAvailableChampions().size(); i++){
				JButton button = new JButton(Game.getAvailableChampions().get(i).getName());
				secondViewButtons.add(button);
				button.addActionListener(this);
			}
			firstPanel.add(secondViewButtons, BorderLayout.CENTER);
			for(int i=0; i<Game.getAvailableChampions().size(); i++){
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + i + ") ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + Game.getAvailableChampions().get(i).getName() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Max Hp: " + Game.getAvailableChampions().get(i).getMaxHP() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Mana: " + Game.getAvailableChampions().get(i).getMana() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Speed: " + Game.getAvailableChampions().get(i).getSpeed() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Action Points Per Turn: " + Game.getAvailableChampions().get(i).getMaxActionPointsPerTurn() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Attack Damage: " + Game.getAvailableChampions().get(i).getAttackDamage() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Attack Range: " + Game.getAvailableChampions().get(i).getAttackRange() + ", ");
				if(Game.getAvailableChampions().get(i) instanceof Hero)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: Hero, Abilities:- ");
				if(Game.getAvailableChampions().get(i) instanceof Villain)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: Villain, Abilities:- ");
				if(Game.getAvailableChampions().get(i) instanceof AntiHero)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: AntiHero, Abilities:- ");
				for(int j=0; j<Game.getAvailableChampions().get(i).getAbilities().size(); j++){
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + j + ")");
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + Game.getAvailableChampions().get(i).getAbilities().get(j).getName() + " ");
				}
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "<br>");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "<br>");
			}
			firstPanel.add(secondViewLabelInfo, BorderLayout.PAGE_END);
		}
		
		public void emptySecondView(){
			for(int i=0; i<secondViewButtons.getComponentCount(); i++){
				secondViewButtons.getComponent(i).setVisible(false);
			}
		}
		
		public void thirdView(){
			secondViewLabel.setText(firstPlayerName + " Choose Your Leader.");
			for(int i=0; i<firstPlayer.getTeam().size(); i++){
				JButton button = new JButton(firstPlayer.getTeam().get(i).getName());
				secondViewButtons.add(button);
				button.addActionListener(this);
			}
			secondViewLabelInfo.setText("<html>");
			for(int i=0; i<firstPlayer.getTeam().size(); i++){
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + i + ") ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + firstPlayer.getTeam().get(i).getName() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Max Hp: " + firstPlayer.getTeam().get(i).getMaxHP() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Mana: " + firstPlayer.getTeam().get(i).getMana() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Speed: " + firstPlayer.getTeam().get(i).getSpeed() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Action Points Per Turn: " + firstPlayer.getTeam().get(i).getMaxActionPointsPerTurn() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Attack Damage: " + firstPlayer.getTeam().get(i).getAttackDamage() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Attack Range: " + firstPlayer.getTeam().get(i).getAttackRange() + ", ");
				if(firstPlayer.getTeam().get(i) instanceof Hero)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: Hero, Abilities:- ");
				if(firstPlayer.getTeam().get(i) instanceof Villain)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: Villain, Abilities:- ");
				if(firstPlayer.getTeam().get(i) instanceof AntiHero)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: AntiHero, Abilities:- ");
				for(int j=0; j<firstPlayer.getTeam().get(i).getAbilities().size(); j++){
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + j + ")");
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + firstPlayer.getTeam().get(i).getAbilities().get(j).getName() + " ");
				}
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "<br>");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "<br>");
			}
			firstPanel.add(secondViewLabelInfo, BorderLayout.PAGE_END);
		}
		
		public void thirdViewSecond(){
			secondViewLabel.setText(secondPlayerName + " Choose Your Leader.");
			for(int i=0; i<secondPlayer.getTeam().size(); i++){
				JButton button = new JButton(secondPlayer.getTeam().get(i).getName());
				secondViewButtons.add(button);
				button.addActionListener(this);
			}
			secondViewLabelInfo.setText("<html>");
			for(int i=0; i<secondPlayer.getTeam().size(); i++){
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + i + ") ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + secondPlayer.getTeam().get(i).getName() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Max Hp: " + secondPlayer.getTeam().get(i).getMaxHP() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Mana: " + secondPlayer.getTeam().get(i).getMana() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Speed: " + secondPlayer.getTeam().get(i).getSpeed() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Action Points Per Turn: " + secondPlayer.getTeam().get(i).getMaxActionPointsPerTurn() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Attack Damage: " + secondPlayer.getTeam().get(i).getAttackDamage() + ", ");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Attack Range: " + secondPlayer.getTeam().get(i).getAttackRange() + ", ");
				if(secondPlayer.getTeam().get(i) instanceof Hero)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: Hero, Abilities:- ");
				if(secondPlayer.getTeam().get(i) instanceof Villain)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: Villain, Abilities:- ");
				if(secondPlayer.getTeam().get(i) instanceof AntiHero)
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "Type: AntiHero, Abilities:- ");
				for(int j=0; j<secondPlayer.getTeam().get(i).getAbilities().size(); j++){
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + j + ")");
					secondViewLabelInfo.setText(secondViewLabelInfo.getText() + secondPlayer.getTeam().get(i).getAbilities().get(j).getName() + " ");
				}
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "<br>");
				secondViewLabelInfo.setText(secondViewLabelInfo.getText() + "<br>");
			}
			firstPanel.add(secondViewLabelInfo, BorderLayout.PAGE_END);
		}
		
		public void emptyThirdView(){
			secondViewLabel.setVisible(false);
			secondViewButtons.setVisible(false);
			secondViewLabelInfo.setVisible(false);
		}
		
		public void fourthView(){
			firstPanel.add(firstPlayerInfo, BorderLayout.PAGE_START);
			firstPanel.add(secondPlayerInfo, BorderLayout.PAGE_END);
			firstPanel.add(right, BorderLayout.LINE_END);
			firstPanel.add(left, BorderLayout.LINE_START);
			right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
			buttons.setLayout(new GridLayout(6, 2));
			buttons.add(move);
			buttons.add(attack);
			buttons.add(ability1);
			buttons.add(ability2);
			buttons.add(ability3);
			buttons.add(leaderAbility);
			buttons.add(upB);
			buttons.add(downB);
			buttons.add(rightB);
			buttons.add(leftB);
			buttons.add(endTurn);
			right.add(turnOrderLabel);
			right.add(buttons);
			left.add(currChampLabel);
			board.setLayout(new GridLayout(Game.getBoardheight(), Game.getBoardwidth()));
			firstPanel.add(board, BorderLayout.CENTER);
			for(int i=0; i<(Game.getBoardheight()*Game.getBoardwidth()); i++){
				JButton button = new JButton((i+1)+"");
				button.setName((i+1)+"");
				boardButtons.add(button);
				board.add(button);
				button.addActionListener(this);
			}
			fillView();
		}
		
		public void fillView(){
			firstPlayerInfo.setText("<html>" + game.getFirstPlayer().getName() +":- ");
			if(game.isFirstLeaderAbilityUsed())
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Used Leader Ability");
			else
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Didn't Use Leader Ability");
			firstPlayerInfo.setText(firstPlayerInfo.getText() + "<br>Champions:-<br>");
			for(int i=0; i<game.getFirstPlayer().getTeam().size(); i++){
				firstPlayerInfo.setText(firstPlayerInfo.getText() + game.getFirstPlayer().getTeam().get(i).getName() + ", ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Current Hp: " + game.getFirstPlayer().getTeam().get(i).getCurrentHP() + ", ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Mana: " + game.getFirstPlayer().getTeam().get(i).getMana() + ", ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Speed: " + game.getFirstPlayer().getTeam().get(i).getSpeed() + ", ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Action Points Per Turn: " + game.getFirstPlayer().getTeam().get(i).getMaxActionPointsPerTurn() + ", ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Attack Damage: " + game.getFirstPlayer().getTeam().get(i).getAttackDamage() + ", ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Attack Range: " + game.getFirstPlayer().getTeam().get(i).getAttackRange() + ", ");
				if(game.getFirstPlayer().getTeam().get(i) instanceof Hero)
					firstPlayerInfo.setText(firstPlayerInfo.getText() + "Type: Hero, ");
				if(game.getFirstPlayer().getTeam().get(i) instanceof Villain)
					firstPlayerInfo.setText(firstPlayerInfo.getText() + "Type: Villain, ");
				if(game.getFirstPlayer().getTeam().get(i) instanceof AntiHero)
					firstPlayerInfo.setText(firstPlayerInfo.getText() + "Type: AntiHero, ");
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "Leader: " + game.getFirstPlayer().getTeam().get(i).equals(game.getFirstPlayer().getLeader()) + ".<br>Applied Effects:-<br>");
				for(int j=0; j<game.getFirstPlayer().getTeam().get(i).getAppliedEffects().size(); j++){
					firstPlayerInfo.setText(firstPlayerInfo.getText() + j + ")");
					firstPlayerInfo.setText(firstPlayerInfo.getText() + "Name: " + game.getFirstPlayer().getTeam().get(i).getAppliedEffects().get(j).getName() + ", ");
					firstPlayerInfo.setText(firstPlayerInfo.getText() + "Duration: " + game.getFirstPlayer().getTeam().get(i).getAppliedEffects().get(j).getDuration() + ", ");
				}
				firstPlayerInfo.setText(firstPlayerInfo.getText() + "<br>");
			}
			secondPlayerInfo.setText("<html>" + game.getSecondPlayer().getName() + ":- ");
			if(game.isSecondLeaderAbilityUsed())
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Used Leader Ability");
			else
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Didn't Use Leader Ability");
			secondPlayerInfo.setText(secondPlayerInfo.getText() + "<br>Champions:-<br>");
			for(int i=0; i<game.getSecondPlayer().getTeam().size(); i++){
				secondPlayerInfo.setText(secondPlayerInfo.getText() + game.getSecondPlayer().getTeam().get(i).getName() + ", ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Current Hp: " + game.getSecondPlayer().getTeam().get(i).getCurrentHP() + ", ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Mana: " + game.getSecondPlayer().getTeam().get(i).getMana() + ", ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Speed: " + game.getSecondPlayer().getTeam().get(i).getSpeed() + ", ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Action Points Per Turn: " + game.getSecondPlayer().getTeam().get(i).getMaxActionPointsPerTurn() + ", ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Attack Damage: " + game.getSecondPlayer().getTeam().get(i).getAttackDamage() + ", ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Attack Range: " + game.getSecondPlayer().getTeam().get(i).getAttackRange() + ", ");
				if(game.getSecondPlayer().getTeam().get(i) instanceof Hero)
					secondPlayerInfo.setText(secondPlayerInfo.getText() + "Type: Hero, ");
				if(game.getSecondPlayer().getTeam().get(i) instanceof Villain)
					secondPlayerInfo.setText(secondPlayerInfo.getText() + "Type: Villain, ");
				if(game.getSecondPlayer().getTeam().get(i) instanceof AntiHero)
					secondPlayerInfo.setText(secondPlayerInfo.getText() + "Type: AntiHero, ");
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "Leader: " + game.getSecondPlayer().getTeam().get(i).equals(game.getSecondPlayer().getLeader()) + ".<br>Applied Effects:-<br>");
				for(int j=0; j<game.getSecondPlayer().getTeam().get(i).getAppliedEffects().size(); j++){
					secondPlayerInfo.setText(secondPlayerInfo.getText() + j + ")");
					secondPlayerInfo.setText(secondPlayerInfo.getText() + "Name: " + game.getSecondPlayer().getTeam().get(i).getAppliedEffects().get(j).getName() + ", ");
					secondPlayerInfo.setText(secondPlayerInfo.getText() + "Duration: " + game.getSecondPlayer().getTeam().get(i).getAppliedEffects().get(j).getDuration() + ", ");
				}
				secondPlayerInfo.setText(secondPlayerInfo.getText() + "<br>");
			}
			for(int i=0; i<boardButtons.size(); i++){
				int x = getXBoard(Integer.parseInt(boardButtons.get(i).getName()));
				int y = getYBoard(Integer.parseInt(boardButtons.get(i).getName()));
				if(game.getBoard()[x][y] == null)
					boardButtons.get(i).setText("Empty");
				else{
					if(game.getBoard()[x][y] instanceof Cover)
						boardButtons.get(i).setText("<html>Cover<br>HP: " + ((Cover)game.getBoard()[x][y]).getCurrentHP());
					else if(game.getFirstPlayer().getTeam().contains(((Champion)game.getBoard()[x][y])))
						boardButtons.get(i).setText("<html>" + ((Champion)game.getBoard()[x][y]).getName()+"<br>HP: "+((Champion)game.getBoard()[x][y]).getCurrentHP()+"<br>Team: "+game.getFirstPlayer().getName());
					else
						boardButtons.get(i).setText("<html>" + ((Champion)game.getBoard()[x][y]).getName()+"<br>HP: "+((Champion)game.getBoard()[x][y]).getCurrentHP()+"<br>Team: "+game.getSecondPlayer().getName());
				}
			}
			turnOrderLabel.setText("<html>Turn Order:<br>");
			ArrayList<Champion> temp = new ArrayList<Champion>();
			while(!game.getTurnOrder().isEmpty())
				temp.add((Champion) game.getTurnOrder().remove());
			for(int i=0; i<temp.size(); i++){
				game.getTurnOrder().insert(temp.get(i));
				turnOrderLabel.setText(turnOrderLabel.getText() + temp.get(i).getName()+"<br>");
			}
			currChampLabel.setText("<html>Current Champion:- ");
			currChampLabel.setText(currChampLabel.getText() +game.getCurrentChampion().getName()+"<br>");
			if(game.getCurrentChampion() instanceof Hero)
				currChampLabel.setText(currChampLabel.getText() + "Type: Hero, ");
			if(game.getCurrentChampion() instanceof Villain)
				currChampLabel.setText(currChampLabel.getText() + "Type: Villain, ");
			if(game.getCurrentChampion() instanceof AntiHero)
				currChampLabel.setText(currChampLabel.getText() + "Type: AntiHero, ");
			currChampLabel.setText(currChampLabel.getText() + "Current Hp: " + game.getCurrentChampion().getCurrentHP() + "<br>");
			currChampLabel.setText(currChampLabel.getText() + "Mana: " + game.getCurrentChampion().getMana() + ", ");
			currChampLabel.setText(currChampLabel.getText() + "Current Action Points: " + game.getCurrentChampion().getCurrentActionPoints() + "<br>");
			currChampLabel.setText(currChampLabel.getText() + "Abilities:-<br>");
			for(int i=0; i<game.getCurrentChampion().getAbilities().size(); i++){
				currChampLabel.setText(currChampLabel.getText() + i + ")");
				currChampLabel.setText(currChampLabel.getText() + "Name: " + game.getCurrentChampion().getAbilities().get(i).getName() + "<br>");
				if(game.getCurrentChampion().getAbilities().get(i) instanceof HealingAbility){
					currChampLabel.setText(currChampLabel.getText() + "Type: Healing Ability<br>");
					currChampLabel.setText(currChampLabel.getText() + "Healing Amount"+ ((HealingAbility) game.getCurrentChampion().getAbilities().get(i)).getHealAmount()+ "<br>");
				}
				else if(game.getCurrentChampion().getAbilities().get(i) instanceof DamagingAbility){
					currChampLabel.setText(currChampLabel.getText() + "Type: Damaging Ability<br>");
					currChampLabel.setText(currChampLabel.getText() + "Damaging Amount"+ ((DamagingAbility) game.getCurrentChampion().getAbilities().get(i)).getDamageAmount()+ "<br>");
				}
				else if(game.getCurrentChampion().getAbilities().get(i) instanceof CrowdControlAbility){
					currChampLabel.setText(currChampLabel.getText() + "Type: Crowd Control Ability<br>");
					currChampLabel.setText(currChampLabel.getText() + "Effect Name: "+ ((CrowdControlAbility) game.getCurrentChampion().getAbilities().get(i)).getEffect().getName()+ ", ");
					currChampLabel.setText(currChampLabel.getText() + "Effect Duration: "+ ((CrowdControlAbility) game.getCurrentChampion().getAbilities().get(i)).getEffect().getDuration()+ "<br>");
				}
				currChampLabel.setText(currChampLabel.getText() + "Cast Area: " +game.getCurrentChampion().getAbilities().get(i).getCastArea()+"<br>");
				currChampLabel.setText(currChampLabel.getText() + "Cast Range: " + game.getCurrentChampion().getAbilities().get(i).getCastRange() + ", ");
				currChampLabel.setText(currChampLabel.getText() + "Mana Cost: " + game.getCurrentChampion().getAbilities().get(i).getManaCost() + "<br>");
				currChampLabel.setText(currChampLabel.getText() + "Required Action Points: " + game.getCurrentChampion().getAbilities().get(i).getRequiredActionPoints() + "<br>");
				currChampLabel.setText(currChampLabel.getText() + "Current Cooldown: " + game.getCurrentChampion().getAbilities().get(i).getCurrentCooldown() + "<br>");
				currChampLabel.setText(currChampLabel.getText() + "Base Cooldown: " + game.getCurrentChampion().getAbilities().get(i).getBaseCooldown() + "<br>");
			}
			currChampLabel.setText(currChampLabel.getText() + "Effects:-<br>");
			for(int i=0; i<game.getCurrentChampion().getAppliedEffects().size(); i++){
				currChampLabel.setText(currChampLabel.getText() + i + ")");
				currChampLabel.setText(currChampLabel.getText() + "Name: " + game.getCurrentChampion().getAppliedEffects().get(i).getName() + ", ");
				currChampLabel.setText(currChampLabel.getText() + "Duration: " + game.getCurrentChampion().getAppliedEffects().get(i).getDuration() + "<br>");
			}
			currChampLabel.setText(currChampLabel.getText() + "<br>");
			currChampLabel.setText(currChampLabel.getText() + "Attack Damage: " + game.getCurrentChampion().getAttackDamage() + "<br>");
			currChampLabel.setText(currChampLabel.getText() + "Attack Range: " + game.getCurrentChampion().getAttackRange() + "<br>");
			ability1.setText("<html>Cast<br>" + game.getCurrentChampion().getAbilities().get(0).getName());
			ability2.setText("<html>Cast<br>" + game.getCurrentChampion().getAbilities().get(1).getName());
			ability3.setText("<html>Cast<br>" + game.getCurrentChampion().getAbilities().get(2).getName());
			if(game.getFirstPlayer().getLeader().equals(game.getCurrentChampion()) || game.getSecondPlayer().getLeader().equals(game.getCurrentChampion()))
				leaderAbility .setEnabled(true);
			else
				leaderAbility.setEnabled(false);
		}
		
		public int getXBoard(int number){
			int x = number-1;
			x = x/Game.getBoardheight();
			return Math.abs(x-(Game.getBoardwidth()-1));
		}
		
		public int getYBoard(int number){
			int y = number-1;
			y = y/Game.getBoardwidth();
			y = number - y*5;
			return y-1;
		}

	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		View view = new View();
	}
}





