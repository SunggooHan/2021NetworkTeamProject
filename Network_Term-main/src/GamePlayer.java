import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

public class GamePlayer {
	public static JFrame frame = new JFrame("login");
	public static Register register;
	public static Socket socket;
	private JPanel contentPane;
	private JTextField txtGame;
	private JPasswordField passwordField;
	static String user;
	String id;
	String pw;
	
	public static void main(String[] args) {
		try {
			socket = new Socket("localhost", 59001);
			Scanner in = new Scanner(socket.getInputStream());
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			new GamePlayer(out, in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GamePlayer(PrintWriter out, Scanner in) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 651, 420);
		frame.setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 645, 391);
		panel.setBackground(Color.WHITE);
		contentPane.add(panel);

		JTextField txtrId = new JTextField();
		txtrId.setBounds(184, 182, 316, 21);

		JButton btnLogin = new JButton("login");
		btnLogin.setBounds(124, 262, 133, 68);
		btnLogin.setBackground(Color.LIGHT_GRAY);
		btnLogin.setFont(new Font("Microsoft Tai Le", Font.BOLD, 21));

		JButton btnJoin = new JButton("Register");
		btnJoin.setBounds(372, 262, 128, 68);
		btnJoin.setBackground(Color.LIGHT_GRAY);
		btnJoin.setFont(new Font("Microsoft Tai Le", Font.BOLD, 21));

		txtGame = new JTextField();
		txtGame.setBounds(124, 59, 376, 83);
		txtGame.setBackground(new Color(211, 211, 211));
		txtGame.setHorizontalAlignment(SwingConstants.CENTER);
		txtGame.setFont(new Font("Microsoft Tai Le", Font.BOLD, 30));
		txtGame.setEditable(false);
		txtGame.setText("WORD CHAIN 2021");
		txtGame.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(184, 217, 316, 21);
		panel.setLayout(null);
		panel.add(txtrId);
		panel.add(passwordField);
		panel.add(btnLogin);
		panel.add(txtGame);
		panel.add(btnJoin);
		
		JLabel IdLabel = new JLabel("ID");
		IdLabel.setFont(new Font("?????? ??????", Font.BOLD, 16));
		IdLabel.setBounds(124, 185, 57, 15);
		panel.add(IdLabel);
		
		JLabel pwLabel = new JLabel("PW");
		pwLabel.setFont(new Font("?????? ??????", Font.BOLD, 16));
		pwLabel.setBounds(124, 220, 57, 15);
		panel.add(pwLabel);
		

		frame.setVisible(true);

		// ???????????????????????????
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				id = txtrId.getText();
				pw = passwordField.getText();

				if (id.equals("")) { //id??? ???????????? ???????????? ????????? ??????
					JOptionPane.showMessageDialog(frame, "ID??? ???????????????");
				}
				else if (pw.equals("")) { //pw??? ???????????? ???????????? ????????? ??????
					JOptionPane.showMessageDialog(frame, "pw??? ???????????????");
				}
				else { //id pw ?????? ??????????????? ??????

					try {
						out.println("login&enter&id&" + id); //id??? ????????? ??????
						out.println("login&enter&pw&" + pw); //pw??? ????????? ??????

						txtrId.setText(""); //?????????
						passwordField.setText(""); //?????????
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		btnJoin.addActionListener(new ActionListener() { //???????????? ????????? ?????? ??????
			public void actionPerformed(ActionEvent e) { //Register ??? ?????????
				register = new Register(out, in);
				register.setVisible(true);
				frame.setVisible(false);
			}
		});

		int temp = 0;
		boolean[] loginOK = new boolean[2];
		for (int i = 0; i < 2; i++) {
			loginOK[i] = false;
		}

		while (in.hasNext()) { //server ??????
			temp++;
			String str = in.nextLine();
			String[] splitMessage = str.split("&");

			for (int i = 0; i < splitMessage.length; i++) {
				if (i == 1 && splitMessage[i].equalsIgnoreCase("id")) {
					if (splitMessage[2].equalsIgnoreCase("OK")) {
						System.out.println("ID??????");
						loginOK[0] = true;
					}
					else if (splitMessage[2].equalsIgnoreCase("ERROR")) { //id??? ?????? ??????????????? ??????
						System.out.println("ID??????");
						JOptionPane.showMessageDialog(null, "ID??? ?????? ???????????????");
						continue;
					}
				}
				if (i == 1 && splitMessage[i].equalsIgnoreCase("pw")) {
					if (splitMessage[2].equalsIgnoreCase("OK")) {
						System.out.println("PW??????");
						loginOK[1] = true;
					}
					else if (splitMessage[2].equalsIgnoreCase("ERROR")) { //password??? ?????? ??????????????? ??????
						System.out.println("PW??????");
						JOptionPane.showMessageDialog(null, "PW??? ?????? ???????????????");
						continue;
					}
				} else if (i == 0 || i == 2) {
					continue;
				}
			}

			if (temp == 2)
				break;

		}
		if (loginOK[0] == true && loginOK[1] == true) {
			System.out.println("???????????????");
			out.println("login&getpersoninfo&" + id);

			String str;
			String NAME = "xxx";
			String ID = "xxx";
			String PW = "xxx";
			String WIN = "xxx";
			String LOSE = "xxx";
			while (in.hasNext()) {
				str = in.nextLine();
				String[] splitMessage = str.split("&");
				if (splitMessage[0].equalsIgnoreCase("userinfo")) {
					NAME = splitMessage[1];
					ID = splitMessage[2];
					PW = splitMessage[3];
					WIN = splitMessage[4];
					LOSE = splitMessage[5];
					break;
				}

			}
			User newUser = new User(NAME, ID, PW, Integer.parseInt(WIN), Integer.parseInt(LOSE));

			frame.setVisible(false);
			frame.dispose();
			try {
				new WaitRoomFrame(newUser.getId(), out, in).run();
			} catch (Exception e) {

			}

		}
	}
}