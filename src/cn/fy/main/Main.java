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
		JFrame win=new JFrame("������");
		Board b=new Board();
		b.initView();//��ʼ������
		b.setPreferredSize(new Dimension(750 , 686));
		win.add(b);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		win.setResizable(false);//ȥ����󻯰�ť		
		win.pack();//�����Ѵ�С
		win.setVisible(true);
		
	}
	
}
