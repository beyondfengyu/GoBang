package cn.fy.view;

import cn.fy.tool.CheckResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Board extends JPanel {

    /**
     * �������
     */
    private static final long serialVersionUID = 1L;
    // ��������λͼ�ֱ�������̡����ӡ�����
    private BufferedImage table;
    private BufferedImage black;
    private BufferedImage white;
    private BufferedImage bg;
    private BufferedImage cur;
    // ������ƶ�ʱ���ѡ���
    private BufferedImage selected;
    // �������̵Ĵ�С
    private static int BOARD_SIZE = 15;
    // �������̿��߶��ٸ�����
    private final int BOARD_WIDTH = 750;
    private final int BOARD_HEIGHT = 686;
    private final int TABLE_WIDTH = 535;
    private final int TABLE_HEIGHT = 536;
    //���巢����Ϣ������
    private final int MSG_START = 1;
    private final int MSG_XY = 2;
    private final int MSG_WIN = 3;
    private final int MSG_HE = 4;
    private final int MSG_FAIL = 5;
    // �����������������ֵ����������֮��ı��ʡ�
    private final int RATE = TABLE_WIDTH / BOARD_SIZE;
    // �����������������ֵ����������֮���ƫ�ƾࡣ
    private final int X_OFFSET = 5;
    private final int Y_OFFSET = 6;
    // ����һ����ά�������䵱����
    private int[][] board;
    private int[][] order;
    // ��ǰѡ�е������
    private int selectedX = -1;
    private int selectedY = -1;
    //ǰһ�����������
    private int curX = -1;
    private int curY = -1;
    //ͨ�� ��IP��ַ���˿ں�
    private static String HOST = "";
    private static int PORT = 8905;
    private ServerSocket server = null;
    private Socket sock = null;
    private boolean OPERABLE = false;//��ǰ�Ƿ�ɵ������
    private boolean HASSOUND = true;//���õ�ǰ����
    private boolean CANSTART = false;//
    private static int winc = 0;//Ӯ������
    private static int hec = 0;//���������
    private int type = 1;//Ĭ��Ϊ����
    private static int num = 1;//��¼��ս������
    private BufferedReader read;
    private PrintStream write;
    private Timer timer;//ʱ�������
    private static int wsecond;//�׷���ʱ
    private static int bsecond;//�ڷ���ʱ
    //������Ч���ŵı���
    private AudioClip as;
    private AudioClip music;
    // ��������Ϸ���̶�Ӧ��Canvas���
    private ChessBoard chessBoard;
    private JButton bstart;//
    private JButton bfail;//
    private JButton bequal;//
    private JButton bsound;//
    private JLabel lblackt;
    private JLabel lblackw;
    private JLabel lwhitet;
    private JLabel lwhitew;
    private JLabel lserver;
    private JLabel ltip;//��ʾ
    private JLabel lnotice;
    private JRadioButton rserver;

    public Board() throws IOException {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        order = new int[BOARD_SIZE][BOARD_SIZE];
        bg = ImageIO.read(new File("image/bg.jpg"));
        cur = ImageIO.read(new File("image/current.gif"));
        table = ImageIO.read(new File("image/board.jpg"));
        black = ImageIO.read(new File("image/black.gif"));
        white = ImageIO.read(new File("image/white.gif"));
        selected = ImageIO.read(new File("image/selected.gif"));
        // ��ÿ��Ԫ�ظ�Ϊ0��0����û������
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
                order[i][j] = 0;
            }
        }
        //��ʼ���������
        chessBoard = new ChessBoard();
        bstart = new JButton();
        bfail = new JButton();
        bequal = new JButton();
        bsound = new JButton(new ImageIcon("image/sound.gif"));
        lblackt = new JLabel();
        lwhitet = new JLabel();
        lblackw = new JLabel();
        lwhitew = new JLabel();
        lserver = new JLabel(new ImageIcon("image/server.png"));
        ltip = new JLabel();
        lnotice = new JLabel();
        rserver = new JRadioButton();
        //��ʼ��ʱ�������
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (OPERABLE) {
                    if (type == 1) lblackt.setText("    ʱ�䣺 " + (CheckResult.mAnds(++bsecond)));
                    else lwhitet.setText("    ʱ�䣺 " + (CheckResult.mAnds(++wsecond)));
                } else if (sock != null) {
                    if (type == 2) lblackt.setText("    ʱ�䣺 " + (CheckResult.mAnds(++bsecond)));
                    else lwhitet.setText("    ʱ�䣺 " + (CheckResult.mAnds(++wsecond)));
                }
            }
        });
        //��������
        music = java.applet.Applet.newAudioClip(new File("music/back.wav").toURI().toURL());
        music.loop();//ѭ������
        as = java.applet.Applet.newAudioClip(new File("music/hint.wav").toURI().toURL());
    }

    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(bg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
    }

    public void initView() throws IOException {
        //ȥ�����ֹ�������ʹ�þ��Զ�λ
        this.setLayout(null);
        chessBoard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (OPERABLE) {
                    // ���û�����¼�������ת����������������ꡣ
                    int x = (int) ((e.getX() - X_OFFSET) / RATE);
                    int y = (int) ((e.getY() - Y_OFFSET) / RATE);
                    if (board[x][y] == 0 && x < 15 && y < 15) {
                        curX = x;
                        curY = y;
                        board[curX][curY] = type;
                        write.println(MSG_XY + "," + curX + "," + curY);
                        OPERABLE = false;
                        //������ǰѡ�е������
                        selectedX = -1;
                        selectedY = -1;
                        //����������ʾ����
                        as.play();

                        if (CheckResult.checkWin(board, curX, curY, type)) {
                            write.println(MSG_WIN);
                            winc++;
                            initEnd();
                            JOptionPane.showMessageDialog(chessBoard, type == 1 ? "����ʤ��" : "����ʤ��");
                        }
                    }
                    chessBoard.repaint();
                }
            }

            // ������˳��������󣬸�λѡ�е�����
            public void mouseExited(MouseEvent e) {
                selectedX = -1;
                selectedY = -1;
                chessBoard.repaint();
            }
        });
        chessBoard.addMouseMotionListener(new MouseMotionAdapter() {
            // ������ƶ�ʱ���ı�ѡ�е������
            public void mouseMoved(MouseEvent e) {
                if (OPERABLE) {
                    selectedX = (e.getX() - X_OFFSET) / RATE;
                    selectedY = (e.getY() - Y_OFFSET) / RATE;
                    chessBoard.repaint();
                }
            }
        });
        bstart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                initData();//��ʼ������
                if (sock == null) {
                    new Thread() {
                        public void run() {
                            try {
                                rserver.setEnabled(false);
                                if (rserver.isSelected()) {//��Ϊ������
                                    server = new ServerSocket(PORT);
                                    sock = server.accept();
                                    type = 1;//Ĭ�����ӵ�����
                                } else {//��Ϊ�ͻ���
                                    while (HOST.equals(""))
                                        HOST = JOptionPane.showInputDialog(chessBoard, "������IP��ַ", "", JOptionPane.PLAIN_MESSAGE);
                                    sock = new Socket(HOST, PORT);
                                    type = 2;//�ı����ӵ�����
                                }
                                read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                                write = new PrintStream(sock.getOutputStream());
                                write.println(MSG_START);    //���Ϳ�ʼ��Ϣ
                                lblackw.setText("    ʤ " + winc + "    �� " + hec);
                                lblackt.setText("    ʱ��     0 : 0 : 0");
                                lwhitew.setText("    ʤ " + winc + "    �� " + hec);
                                lwhitet.setText("    ʱ��     0 : 0 : 0");
                                //ѭ����ȡ�������������Է���Ϣ
                                String str = null;
                                while ((str = read.readLine()) != null) {
                                    String[] strs = str.split(",");
                                    switch (Integer.parseInt(strs[0])) {
                                        case 1://��֪��ʼ��Ϣ
                                            ltip.setText("��Ϸ��ʼ");
                                            if ((num + type) % 2 == 0) {//���ھ���������һ������
                                                if (CANSTART) {
                                                    CANSTART = false;
                                                    OPERABLE = true;
                                                } else CANSTART = true;
                                            }
                                            break;
                                        case 2://��֪���ӵ�����
                                            curX = Integer.parseInt(strs[1]);
                                            curY = Integer.parseInt(strs[2]);
                                            board[curX][curY] = type == 1 ? 2 : 1;//������ɫ���Լ��෴
                                            OPERABLE = true;//���õ�ǰ״̬Ϊ�ɲ���
                                            break;
                                        case 3:
                                            initEnd();//��Ϸ������ʼ������
                                            chessBoard.repaint();
                                            JOptionPane.showMessageDialog(chessBoard, type == 2 ? "����ʤ��" : "����ʤ��");
                                            break;
                                        case 4:
                                            if (Integer.parseInt(strs[1]) == 3) {
                                                int result = JOptionPane.showConfirmDialog(chessBoard, "�Է��������", "", JOptionPane.YES_NO_OPTION);
                                                write.println(MSG_HE + "," + result);
                                                if (result == 0) {
                                                    hec++;//�����������1
                                                    initEnd();//��Ϸ������ʼ������
                                                }
                                            } else if (Integer.parseInt(strs[1]) == 0) {
                                                hec++;//�����������1
                                                initEnd();//��Ϸ������ʼ������
                                                JOptionPane.showMessageDialog(chessBoard, "�Է�ͬ�����");
                                            } else
                                                JOptionPane.showMessageDialog(chessBoard, "�Է��ܾ�����");
                                            break;
                                        case 5:
                                            winc++;//Ӯ��������1
                                            initEnd();//��Ϸ������ʼ������
                                            JOptionPane.showMessageDialog(chessBoard, "�Է�����");
                                            break;
                                    }
                                    chessBoard.repaint();
                                }
                            } catch (IOException e) {
                            }
                        }
                    }.start();
                } else {
                    write.println(MSG_START);
                }
                if (CANSTART) {
                    OPERABLE = true;
                    CANSTART = false;
                } else {
                    CANSTART = true;
                }
                chessBoard.repaint();
            }

        });
        bfail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                write.println(MSG_FAIL);
                initEnd();//��Ϸ������ʼ������
            }
        });
        bequal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                write.println(MSG_HE + "," + 3);
            }
        });
        bsound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (HASSOUND) {
                    HASSOUND = false;
                    bsound.setIcon(new ImageIcon("image/nsound.gif"));
                    music.stop();
                } else {
                    HASSOUND = true;
                    bsound.setIcon(new ImageIcon("image/sound.gif"));
                    music.loop();
                }
            }

        });
        chessBoard.setPreferredSize(new Dimension(
                TABLE_WIDTH, TABLE_HEIGHT));
        chessBoard.setBounds(208, 75, TABLE_WIDTH, TABLE_HEIGHT);
        //���ð�ť����
        bstart.setContentAreaFilled(false);//ȥ����ť���
        bstart.setBorderPainted(false);//ȥ����ť�߿�
        bstart.setBounds(452, 648, 60, 32);
        bfail.setContentAreaFilled(false);
        bfail.setBorderPainted(false);
        bfail.setBounds(528, 642, 60, 32);
        bequal.setContentAreaFilled(false);
        bequal.setBorderPainted(false);
        bequal.setBounds(365, 640, 60, 32);
        bsound.setContentAreaFilled(false);
        bsound.setBorderPainted(false);
        bsound.setBounds(25, 510, 25, 25);
        //���ñ�ǩ����
        Color c = new Color(0, 0, 0);
        lblackt.setBounds(60, 140, 130, 60);
        lblackt.setOpaque(true);
        lblackt.setBackground(c);
        lwhitet.setBounds(60, 335, 130, 60);
        lwhitet.setOpaque(true);
        lwhitet.setBackground(c);
        lblackw.setBounds(60, 200, 130, 60);
        lblackw.setOpaque(true);
        lblackw.setBackground(c);
        lwhitew.setBounds(60, 395, 130, 60);
        lwhitew.setOpaque(true);
        lwhitew.setBackground(c);
        lserver.setBounds(45, 550, 32, 32);
        ltip.setBounds(430, 210, 100, 30);
        ltip.setOpaque(false);
        lnotice.setOpaque(false);
        lnotice.setBounds(70, 250, 200, 80);
        rserver.setBounds(80, 560, 30, 30);
        rserver.setOpaque(false);
        rserver.setSelected(true);
        this.add(lblackt);
        this.add(lwhitet);
        this.add(lblackw);
        this.add(lwhitew);
        this.add(lserver);
        this.add(ltip);
        this.add(lnotice);
        this.add(rserver);
        this.add(bstart);
        this.add(bfail);
        this.add(bequal);
        this.add(bsound);
        this.add(chessBoard);
    }

    //��ʼ������
    void initData() {
        timer.start();
        wsecond = 0;//��ǰ��ʱ��ʼΪ0
        bsecond = 0;
        //��ǰ���ӵı��
        curX = -1;
        curY = -1;
        // ��ÿ��Ԫ�ظ�Ϊ0��0����û������
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
                order[i][j] = 0;
            }
        }
        lnotice.setText("�� " + num + " ��");//��ս������1
        bstart.setEnabled(false);//ʹ��ʼ��ť�ͷ�������ѡ��ť��Ч
        bfail.setEnabled(true);
        bequal.setEnabled(true);
    }

    //��Ϸ�������ʼ������
    void initEnd() {
        timer.stop();
        //������ǰѡ�е������
        selectedX = -1;
        selectedY = -1;
        //ʹ��ʼ��ť���á���������䰴ť��Ч
        bstart.setEnabled(true);
        bfail.setEnabled(false);
        bequal.setEnabled(false);
        if (type == 1) {
            lblackw.setText("    ʤ " + winc + "    �� " + hec);
            lblackt.setText("    ʱ��     0 : 0 : 0");
            lwhitew.setText("    ʤ " + (num - winc) + "    �� " + hec);
            lwhitet.setText("    ʱ��     0 : 0 : 0");
        } else {
            lblackw.setText("    ʤ " + (num - winc) + "    �� " + hec);
            lblackt.setText("    ʱ��     0 : 0 : 0");
            lwhitew.setText("    ʤ " + winc + "    �� " + hec);
            lwhitet.setText("    ʱ��     0 : 0 : 0");
        }
        num++;//�ܾ�����1
        CANSTART = false;
        OPERABLE = false;
    }

    //����������
    class ChessBoard extends JPanel {
        private static final long serialVersionUID = 1L;

        // ��дJPanel��paint������ʵ�ֻ滭
        public void paint(Graphics g) {
            // ����������������
            g.drawImage(table, 0, 0, null);
            // ����ѡ�е�ĺ��
            if (selectedX >= 0 && selectedY >= 0)
                g.drawImage(selected, selectedX * RATE + X_OFFSET,
                        selectedY * RATE + Y_OFFSET, null);
            // �������飬�������ӡ�
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    // ���ƺ���
                    if (board[i][j] == 1) {
                        g.drawImage(black, i * RATE + X_OFFSET
                                , j * RATE + Y_OFFSET, null);
                    }
                    // ���ư���
                    if (board[i][j] == 2) {
                        g.drawImage(white, i * RATE + X_OFFSET
                                , j * RATE + Y_OFFSET, null);
                    }
                }
            }
            //���Ƶ�ǰ����ı��
            if (curX >= 0 && curY >= 0)
                g.drawImage(cur, curX * RATE + X_OFFSET, curY * RATE + Y_OFFSET, null);

        }
    }
}
