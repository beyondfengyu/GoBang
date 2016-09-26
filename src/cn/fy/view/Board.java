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
     * 棋盘面板
     */
    private static final long serialVersionUID = 1L;
    // 下面三个位图分别代表棋盘、黑子、白子
    private BufferedImage table;
    private BufferedImage black;
    private BufferedImage white;
    private BufferedImage bg;
    private BufferedImage cur;
    // 当鼠标移动时候的选择框
    private BufferedImage selected;
    // 定义棋盘的大小
    private static int BOARD_SIZE = 15;
    // 定义棋盘宽、高多少个像素
    private final int BOARD_WIDTH = 750;
    private final int BOARD_HEIGHT = 686;
    private final int TABLE_WIDTH = 535;
    private final int TABLE_HEIGHT = 536;
    //定义发送信息的类型
    private final int MSG_START = 1;
    private final int MSG_XY = 2;
    private final int MSG_WIN = 3;
    private final int MSG_HE = 4;
    private final int MSG_FAIL = 5;
    // 定义棋盘坐标的像素值和棋盘数组之间的比率。
    private final int RATE = TABLE_WIDTH / BOARD_SIZE;
    // 定义棋盘坐标的像素值和棋盘数组之间的偏移距。
    private final int X_OFFSET = 5;
    private final int Y_OFFSET = 6;
    // 定义一个二维数组来充当棋盘
    private int[][] board;
    private int[][] order;
    // 当前选中点的坐标
    private int selectedX = -1;
    private int selectedY = -1;
    //前一步下棋的坐标
    private int curX = -1;
    private int curY = -1;
    //通信 的IP地址、端口号
    private static String HOST = "";
    private static int PORT = 8905;
    private ServerSocket server = null;
    private Socket sock = null;
    private boolean OPERABLE = false;//当前是否可点击下子
    private boolean HASSOUND = true;//设置当前声音
    private boolean CANSTART = false;//
    private static int winc = 0;//赢的总数
    private static int hec = 0;//和棋的总数
    private int type = 1;//默认为黑子
    private static int num = 1;//记录对战的总数
    private BufferedReader read;
    private PrintStream write;
    private Timer timer;//时间监听器
    private static int wsecond;//白方用时
    private static int bsecond;//黑方用时
    //背景音效播放的变量
    private AudioClip as;
    private AudioClip music;
    // 五子棋游戏棋盘对应的Canvas组件
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
    private JLabel ltip;//提示
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
        // 把每个元素赋为0，0代表没有棋子
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
                order[i][j] = 0;
            }
        }
        //初始化界面组件
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
        //初始化时间监听器
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (OPERABLE) {
                    if (type == 1) lblackt.setText("    时间： " + (CheckResult.mAnds(++bsecond)));
                    else lwhitet.setText("    时间： " + (CheckResult.mAnds(++wsecond)));
                } else if (sock != null) {
                    if (type == 2) lblackt.setText("    时间： " + (CheckResult.mAnds(++bsecond)));
                    else lwhitet.setText("    时间： " + (CheckResult.mAnds(++wsecond)));
                }
            }
        });
        //背景音乐
        music = java.applet.Applet.newAudioClip(new File("music/back.wav").toURI().toURL());
        music.loop();//循环播放
        as = java.applet.Applet.newAudioClip(new File("music/hint.wav").toURI().toURL());
    }

    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(bg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
    }

    public void initView() throws IOException {
        //去掉布局管理器，使用绝对定位
        this.setLayout(null);
        chessBoard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (OPERABLE) {
                    // 将用户鼠标事件的坐标转换成棋子数组的坐标。
                    int x = (int) ((e.getX() - X_OFFSET) / RATE);
                    int y = (int) ((e.getY() - Y_OFFSET) / RATE);
                    if (board[x][y] == 0 && x < 15 && y < 15) {
                        curX = x;
                        curY = y;
                        board[curX][curY] = type;
                        write.println(MSG_XY + "," + curX + "," + curY);
                        OPERABLE = false;
                        //消除当前选中点的坐标
                        selectedX = -1;
                        selectedY = -1;
                        //播放下棋提示声音
                        as.play();

                        if (CheckResult.checkWin(board, curX, curY, type)) {
                            write.println(MSG_WIN);
                            winc++;
                            initEnd();
                            JOptionPane.showMessageDialog(chessBoard, type == 1 ? "黑子胜利" : "白子胜利");
                        }
                    }
                    chessBoard.repaint();
                }
            }

            // 当鼠标退出棋盘区后，复位选中点坐标
            public void mouseExited(MouseEvent e) {
                selectedX = -1;
                selectedY = -1;
                chessBoard.repaint();
            }
        });
        chessBoard.addMouseMotionListener(new MouseMotionAdapter() {
            // 当鼠标移动时，改变选中点的坐标
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
                initData();//初始化数据
                if (sock == null) {
                    new Thread() {
                        public void run() {
                            try {
                                rserver.setEnabled(false);
                                if (rserver.isSelected()) {//作为服务器
                                    server = new ServerSocket(PORT);
                                    sock = server.accept();
                                    type = 1;//默认棋子的类型
                                } else {//作为客户端
                                    while (HOST.equals(""))
                                        HOST = JOptionPane.showInputDialog(chessBoard, "请输入IP地址", "", JOptionPane.PLAIN_MESSAGE);
                                    sock = new Socket(HOST, PORT);
                                    type = 2;//改变棋子的类型
                                }
                                read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                                write = new PrintStream(sock.getOutputStream());
                                write.println(MSG_START);    //发送开始消息
                                lblackw.setText("    胜 " + winc + "    和 " + hec);
                                lblackt.setText("    时间     0 : 0 : 0");
                                lwhitew.setText("    胜 " + winc + "    和 " + hec);
                                lwhitet.setText("    时间     0 : 0 : 0");
                                //循环读取网络流，监听对方信息
                                String str = null;
                                while ((str = read.readLine()) != null) {
                                    String[] strs = str.split(",");
                                    switch (Integer.parseInt(strs[0])) {
                                        case 1://获知开始信息
                                            ltip.setText("游戏开始");
                                            if ((num + type) % 2 == 0) {//用于决定轮流第一个下棋
                                                if (CANSTART) {
                                                    CANSTART = false;
                                                    OPERABLE = true;
                                                } else CANSTART = true;
                                            }
                                            break;
                                        case 2://获知下子的坐标
                                            curX = Integer.parseInt(strs[1]);
                                            curY = Integer.parseInt(strs[2]);
                                            board[curX][curY] = type == 1 ? 2 : 1;//棋子颜色与自己相反
                                            OPERABLE = true;//设置当前状态为可操作
                                            break;
                                        case 3:
                                            initEnd();//游戏结束初始化数据
                                            chessBoard.repaint();
                                            JOptionPane.showMessageDialog(chessBoard, type == 2 ? "黑子胜利" : "白子胜利");
                                            break;
                                        case 4:
                                            if (Integer.parseInt(strs[1]) == 3) {
                                                int result = JOptionPane.showConfirmDialog(chessBoard, "对方请求和棋", "", JOptionPane.YES_NO_OPTION);
                                                write.println(MSG_HE + "," + result);
                                                if (result == 0) {
                                                    hec++;//和棋的总数加1
                                                    initEnd();//游戏结束初始化数据
                                                }
                                            } else if (Integer.parseInt(strs[1]) == 0) {
                                                hec++;//和棋的总数加1
                                                initEnd();//游戏结束初始化数据
                                                JOptionPane.showMessageDialog(chessBoard, "对方同意和棋");
                                            } else
                                                JOptionPane.showMessageDialog(chessBoard, "对方拒绝和棋");
                                            break;
                                        case 5:
                                            winc++;//赢的总数加1
                                            initEnd();//游戏结束初始化数据
                                            JOptionPane.showMessageDialog(chessBoard, "对方认输");
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
                initEnd();//游戏结束初始化数据
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
        //设置按钮布局
        bstart.setContentAreaFilled(false);//去掉按钮填充
        bstart.setBorderPainted(false);//去掉按钮边框
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
        //设置标签布局
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

    //初始化数据
    void initData() {
        timer.start();
        wsecond = 0;//当前用时初始为0
        bsecond = 0;
        //当前下子的标记
        curX = -1;
        curY = -1;
        // 把每个元素赋为0，0代表没有棋子
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
                order[i][j] = 0;
            }
        }
        lnotice.setText("第 " + num + " 局");//对战总数加1
        bstart.setEnabled(false);//使开始按钮和服务器单选按钮无效
        bfail.setEnabled(true);
        bequal.setEnabled(true);
    }

    //游戏结束后初始化数据
    void initEnd() {
        timer.stop();
        //消除当前选中点的坐标
        selectedX = -1;
        selectedY = -1;
        //使开始按钮可用、和棋和认输按钮无效
        bstart.setEnabled(true);
        bfail.setEnabled(false);
        bequal.setEnabled(false);
        if (type == 1) {
            lblackw.setText("    胜 " + winc + "    和 " + hec);
            lblackt.setText("    时间     0 : 0 : 0");
            lwhitew.setText("    胜 " + (num - winc) + "    和 " + hec);
            lwhitet.setText("    时间     0 : 0 : 0");
        } else {
            lblackw.setText("    胜 " + (num - winc) + "    和 " + hec);
            lblackt.setText("    时间     0 : 0 : 0");
            lwhitew.setText("    胜 " + winc + "    和 " + hec);
            lwhitet.setText("    时间     0 : 0 : 0");
        }
        num++;//总局数加1
        CANSTART = false;
        OPERABLE = false;
    }

    //定义棋盘类
    class ChessBoard extends JPanel {
        private static final long serialVersionUID = 1L;

        // 重写JPanel的paint方法，实现绘画
        public void paint(Graphics g) {
            // 将绘制五子棋棋盘
            g.drawImage(table, 0, 0, null);
            // 绘制选中点的红框
            if (selectedX >= 0 && selectedY >= 0)
                g.drawImage(selected, selectedX * RATE + X_OFFSET,
                        selectedY * RATE + Y_OFFSET, null);
            // 遍历数组，绘制棋子。
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    // 绘制黑棋
                    if (board[i][j] == 1) {
                        g.drawImage(black, i * RATE + X_OFFSET
                                , j * RATE + Y_OFFSET, null);
                    }
                    // 绘制白棋
                    if (board[i][j] == 2) {
                        g.drawImage(white, i * RATE + X_OFFSET
                                , j * RATE + Y_OFFSET, null);
                    }
                }
            }
            //绘制当前下棋的标记
            if (curX >= 0 && curY >= 0)
                g.drawImage(cur, curX * RATE + X_OFFSET, curY * RATE + Y_OFFSET, null);

        }
    }
}
