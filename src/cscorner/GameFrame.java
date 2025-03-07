package cscorner;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JComboBox;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameFrame frame = new GameFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 476, 445);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Simple Game");
		rdbtnNewRadioButton.setBounds(35, 7, 109, 23);
		contentPane.add(rdbtnNewRadioButton);
		//add if pressed change game mode to simple
		//rules for game changed
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("General Game");
		rdbtnNewRadioButton_1.setBounds(146, 7, 109, 23);
		contentPane.add(rdbtnNewRadioButton_1);
		//add if pressed change game mode to general
		//rules for game changed / updated
		
		textField = new JTextField();
		textField.setBounds(340, 7, 45, 23);
		contentPane.add(textField);
		textField.setColumns(10);
		//Text field for choosing board size
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("S");
		rdbtnNewRadioButton_2.setBounds(0, 145, 50, 23);
		contentPane.add(rdbtnNewRadioButton_2);
		//Left side S
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("O");
		rdbtnNewRadioButton_3.setBounds(0, 177, 50, 23);
		contentPane.add(rdbtnNewRadioButton_3);
		//Left side O
		
		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("S");
		rdbtnNewRadioButton_4.setBounds(355, 145, 109, 23);
		contentPane.add(rdbtnNewRadioButton_4);
		//Right side S
		
		JRadioButton rdbtnNewRadioButton_5 = new JRadioButton("O");
		rdbtnNewRadioButton_5.setBounds(355, 177, 109, 23);
		contentPane.add(rdbtnNewRadioButton_5);
		//Right side O
		
		JLabel lblNewLabel = new JLabel("Board size");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(261, 9, 78, 14);
		contentPane.add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setBounds(55, 109, 294, 214);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		//text field for actual board, work in progress on size
	}
}
