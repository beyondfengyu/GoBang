package cn.fy.main;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import cn.fy.view.Board;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		JFrame win=new JFrame("五子棋");
		Board b=new Board();
		b.initView();//初始化界面
		b.setPreferredSize(new Dimension(750 , 686));
		win.add(b);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		win.setResizable(false);//去掉最大化按钮		
		win.pack();//获得最佳大小
		win.setVisible(true);
		
	}
	
}
